package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.dugged.cutelessmod.CutelessMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.*;
import net.minecraft.stats.StatBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Mixin(NetHandlerPlayClient.class)
public abstract class MixinNetHandlerPlayClient {
	@Unique
	private static final String START_OF_PACKET = "Lnet/minecraft/network/PacketThreadUtil;checkThreadAndEnqueue(Lnet/minecraft/network/Packet;Lnet/minecraft/network/INetHandler;Lnet/minecraft/util/IThreadListener;)V";

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

	@Inject(method = "handleStatistics", at = @At(value = "INVOKE", target = "Lnet/minecraft/stats/StatisticsManager;unlockAchievement(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/stats/StatBase;I)V"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void addStat(final SPacketStatistics packetIn, final CallbackInfo ci, final Iterator<Map.Entry<StatBase, Integer>> it, final Map.Entry<StatBase, Integer> b, final StatBase key, final int value) {
		if (key.statId.matches(CutelessMod.statPluginFilter)) {
			CutelessMod.statPlugin.sendStatIncrease(value, true);
		}
	}

	@Inject(method = "handleBlockAction", at = @At(value = "INVOKE", target = START_OF_PACKET), cancellable = true)
	private void onHandleBlockAction(final SPacketBlockAction packet, final CallbackInfo ci) {
		if (Configuration.ignoreBlockEvents) {
			ci.cancel();
		}
	}
}
