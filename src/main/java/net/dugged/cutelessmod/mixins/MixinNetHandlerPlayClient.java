package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.dugged.cutelessmod.CutelessMod;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketCombatEvent;
import net.minecraft.network.play.server.SPacketJoinGame;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Mixin(NetHandlerPlayClient.class)
public abstract class MixinNetHandlerPlayClient {
	@Unique
	private static final String START_OF_PACKET = "Lnet/minecraft/network/PacketThreadUtil;checkThreadAndEnqueue(Lnet/minecraft/network/Packet;Lnet/minecraft/network/INetHandler;Lnet/minecraft/util/IThreadListener;)V";
	@Shadow
	private WorldClient world;

	@Inject(method = "handleCombatEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;displayGuiScreen(Lnet/minecraft/client/gui/GuiScreen;)V"))
	private void sendDeathLocation(SPacketCombatEvent packetIn, CallbackInfo ci) {
		if (Configuration.respawnOnDeath) {
			Minecraft.getMinecraft().player.respawnPlayer();
		}

		if (Configuration.deathLocation) {
			final Minecraft mc = Minecraft.getMinecraft();
			final BlockPos pos = mc.player.getPosition();
			final String formatted = String.format("You died @ %d %d %d", pos.getX(), pos.getY(), pos.getZ());
			final ITextComponent message = new TextComponentString(formatted);
			message.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, formatted));
			mc.ingameGUI.getChatGUI().printChatMessage(message);
		}
	}

	@Redirect(method = "handleTimeUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/play/server/SPacketTimeUpdate;getWorldTime()J"))
	private long alwaysDay(SPacketTimeUpdate packet) {
		final long time = packet.getWorldTime();
		if (Configuration.alwaysDay) {
			return time >= 0 ? -(time - time % 24000L + 6000L) : time;
		}

		return time;
	}

	@Redirect(method = "handlePlayerListHeaderFooter", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiPlayerTabOverlay;setFooter(Lnet/minecraft/util/text/ITextComponent;)V"))
	private void redirectTabFooter(GuiPlayerTabOverlay guiPlayerTabOverlay, ITextComponent footerIn) {
		CutelessMod.tabFooter = footerIn;
	}

	@Inject(method = "handleTimeUpdate", at = @At("RETURN"))
	private void onTimeUpdate(SPacketTimeUpdate packetIn, CallbackInfo ci) {
		final long currentTime = System.nanoTime();
		final long dt = currentTime - CutelessMod.lastTimeUpdate;
		CutelessMod.lastTimeUpdate = currentTime;
		if (dt > 0L && CutelessMod.overlayTimer == 0) {
			CutelessMod.mspt = (int) Math.max(50, dt * 5E-8D);
		}
	}

	@Redirect(method = "handleSetPassengers", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;warn(Ljava/lang/String;)V", remap = false))
	private void noopWarn(Logger logger, String message) {
		// noop
	}

	@Redirect(method = "handleChunkData", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z", remap = false))
	private boolean replaceChunkDataBlockEntityLoop(Iterator<NBTTagCompound> iterator) {
		while (iterator.hasNext()) {
			final NBTTagCompound compound = iterator.next();
			final BlockPos pos = new BlockPos(compound.getInteger("x"), compound.getInteger("y"), compound.getInteger("z"));
			final boolean isPiston = compound.getString("id").equals("minecraft:piston");
			if (isPiston) {
				compound.setFloat("progress", Math.min(compound.getFloat("progress") + 0.5F, 1F));
			}

			TileEntity te = this.world.getTileEntity(pos);
			if (te != null) {
				te.readFromNBT(compound);
			} else {
				if (!isPiston) {
					continue;
				}

				final IBlockState state = this.world.getBlockState(pos);
				if (state.getBlock() != Blocks.PISTON_EXTENSION) {
					continue;
				}

				te = new TileEntityPiston();
				te.readFromNBT(compound);
				this.world.setTileEntity(pos, te);
				te.updateContainingBlockInfo();
			}
		}

		return false;
	}

	@Inject(method = "handleOpenWindow", at = @At(value = "INVOKE", target = START_OF_PACKET, shift = At.Shift.AFTER), cancellable = true)
	private void onHandleOpenWindow(SPacketOpenWindow packet, CallbackInfo ci) {
		if (CutelessMod.spy.onOpenWindow(packet.getWindowId(), packet.getSlotCount())) {
			ci.cancel();
		}
	}

	@Inject(method = "handleWindowItems", at = @At(value = "INVOKE", target = START_OF_PACKET, shift = At.Shift.AFTER))
	private void onHandleWindowItems(SPacketWindowItems packet, CallbackInfo ci) {
		CutelessMod.spy.onGetContent(packet.getWindowId(), packet.getItemStacks());
	}

	@Inject(method = "handleChat", at = @At(value = "INVOKE", target = START_OF_PACKET, shift = At.Shift.AFTER), cancellable = true)
	private void onHandleChat(SPacketChat packet, CallbackInfo ci) {
		if (packet.isSystem() && CutelessMod.spy.onChatReceived(packet.getChatComponent())) {
			ci.cancel();
		}
	}

	//TODO: Improve xd
	@Inject(method = "handleJoinGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkManager;sendPacket(Lnet/minecraft/network/Packet;)V"), cancellable = true)
	private void addChatHistory(SPacketJoinGame packetIn, CallbackInfo ci) {
		final Minecraft mc = Minecraft.getMinecraft();
		String currentServer = "SINGLEPLAYER";
		if (mc.getCurrentServerData() != null) {
			currentServer = mc.getCurrentServerData().serverIP;
		}

		if (CutelessMod.tabCompleteHistory.containsKey(currentServer)) {
			for (String message : CutelessMod.tabCompleteHistory.get(currentServer)) {
				mc.ingameGUI.getChatGUI().addToSentMessages(message);
			}
		}

		if (CutelessMod.chatHistory.containsKey(currentServer)) {
			List<ChatLine> history = CutelessMod.chatHistory.get(currentServer);
			Collections.reverse(history);
			for (ChatLine message : history) {
				mc.ingameGUI.getChatGUI().printChatMessage(message.getChatComponent());
			}
		}
	}
}
