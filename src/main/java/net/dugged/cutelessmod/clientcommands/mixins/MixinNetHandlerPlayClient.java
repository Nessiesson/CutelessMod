package net.dugged.cutelessmod.clientcommands.mixins;

import net.dugged.cutelessmod.clientcommands.ClientCommandHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketChatMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient {
	@Inject(method = "sendPacket", at = @At(value = "HEAD"))
	private void onSendChat(Packet<?> packetIn, CallbackInfo ci) {
		if (packetIn instanceof CPacketChatMessage) {
			String msg = ((CPacketChatMessage) packetIn).getMessage();
			String[] args = msg.split(" ");
			if (args[0].equals("/tp")) {
				ClientCommandHandler.getInstance().lastPlayerPos.update(Minecraft.getMinecraft().player.getPosition(),
						Minecraft.getMinecraft().player.dimension);
			}
		}
	}
}
