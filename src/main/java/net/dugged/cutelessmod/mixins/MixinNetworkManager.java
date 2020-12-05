package net.dugged.cutelessmod.mixins;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import net.dugged.cutelessmod.CutelessMod;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetworkManager.class)
public abstract class MixinNetworkManager {
	@Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"))
	private void countSendPackets(final Packet<?> packetIn, final CallbackInfo ci) {
		CutelessMod.sendPacketsThisTick++;
	}

	@Inject(method = "exceptionCaught", at = @At("HEAD"), cancellable = true)
	private void onExceptionCaught(final ChannelHandlerContext context, final Throwable throwable, final CallbackInfo ci) {
		if (throwable instanceof DecoderException && "java.lang.IndexOutOfBoundsException: readerIndex(1) + length(1) exceeds writerIndex(1): PooledUnsafeDirectByteBuf(ridx: 1, widx: 1, cap: 1)".equals(throwable.getMessage())) {
			ci.cancel();
		}
	}
}
