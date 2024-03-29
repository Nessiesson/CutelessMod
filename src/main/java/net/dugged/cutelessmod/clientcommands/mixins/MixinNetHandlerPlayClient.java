package net.dugged.cutelessmod.clientcommands.mixins;

import net.dugged.cutelessmod.clientcommands.*;
import net.dugged.cutelessmod.clientcommands.worldedit.WorldEdit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketTabComplete;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient {
	@Unique
	private static final String START_OF_PACKET = "Lnet/minecraft/network/PacketThreadUtil;checkThreadAndEnqueue(Lnet/minecraft/network/Packet;Lnet/minecraft/network/INetHandler;Lnet/minecraft/util/IThreadListener;)V";

	@Inject(method = "handleChat", at = @At(value = "INVOKE", target = START_OF_PACKET, shift = At.Shift.AFTER), cancellable = true)
	private void onHandleChat(SPacketChat packet, CallbackInfo ci) {
		if (packet.isSystem()) {
			final String chatLine = packet.getChatComponent().getUnformattedText();
			boolean flag = false;
			if (chatLine.contains("sendCommandFeedback") && !chatLine.contains("updated")) {
				Handler.sendCommandfeedback = chatLine.contains("true");
				flag = true;
			} else if (chatLine.contains("doTileDrops")) {
				Handler.doTileDrops = chatLine.contains("true");
				flag = true;
			} else if (chatLine.contains("logAdminCommands")) {
				Handler.logAdminCommands = chatLine.contains("true");
				flag = true;
			}
			if (flag && !chatLine.contains("updated")) {
				ci.cancel();
			}
			if (ClientCommandHandler.instance.handlers.size() > 0 && (chatLine.contains("The block couldn't be placed") || chatLine.contains("Block placed") || chatLine.contains("No blocks filled"))) {
				ci.cancel();
			}
		}
	}

	private boolean contains(String[] matches, String word) {
		for (String s : matches) {
			if (s.contains(word)) {
				return true;
			}
		}
		return false;
	}

	@Inject(method = "handleTabComplete", at = @At(value = "INVOKE", target = START_OF_PACKET, shift = At.Shift.AFTER), cancellable = true)
	private void onHandleTabComplete(SPacketTabComplete packetIn, CallbackInfo ci) {
		if (contains(packetIn.getMatches(), "setblock")) {
			HandlerSetBlock.setblockPermission = true;
		} else if (contains(packetIn.getMatches(), "fill")) {
			HandlerFill.fillPermission = true;
		} else if (contains(packetIn.getMatches(), "clone")) {
			HandlerClone.clonePermission = true;
		} else if (contains(packetIn.getMatches(), "replaceitem")) {
			HandlerReplaceItem.replaceitemPermission = true;
		} else if (contains(packetIn.getMatches(), "gamerule")) {
			Handler.gamerulePermission = true;
		}
	}

	@Inject(method = "sendPacket", at = @At(value = "INVOKE"))
	private void onSendChat(Packet<?> packetIn, CallbackInfo ci) {
		if (packetIn instanceof CPacketChatMessage) {
			String msg = ((CPacketChatMessage) packetIn).getMessage();
			String[] args = msg.split(" ");
			if (args[0].equals("/tp")) {
				ClientCommandHandler.instance.lastPosition.update(WorldEdit.playerPos(), Minecraft.getMinecraft().player.dimension);
			}
		}
	}
}
