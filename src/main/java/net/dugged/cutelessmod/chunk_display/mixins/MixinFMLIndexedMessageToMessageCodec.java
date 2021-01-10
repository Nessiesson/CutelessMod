package net.dugged.cutelessmod.chunk_display.mixins;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.AttributeKey;
import it.unimi.dsi.fastutil.objects.Object2ByteMap;
import net.dugged.cutelessmod.chunk_display.CarpetPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.FMLIndexedMessageToMessageCodec;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.ref.WeakReference;
import java.util.List;

import static net.dugged.cutelessmod.chunk_display.CarpetPluginChannel.CARPET_CHANNEL_NAME;

@Mixin(FMLIndexedMessageToMessageCodec.class)
public abstract class MixinFMLIndexedMessageToMessageCodec<A> extends MessageToMessageCodec<FMLProxyPacket, A> {
	@Shadow(remap = false)
	@Final
	public static AttributeKey<ThreadLocal<WeakReference<FMLProxyPacket>>> INBOUNDPACKETTRACKER;
	@Shadow(remap = false)
	@Final
	private Object2ByteMap<Class<? extends A>> types;

	@Shadow(remap = false)
	protected abstract void testMessageValidity(FMLProxyPacket msg);

	@Shadow(remap = false)
	public abstract void decodeInto(ChannelHandlerContext ctx, ByteBuf source, A msg);

	@Shadow(remap = false)
	public abstract void encodeInto(ChannelHandlerContext ctx, A msg, ByteBuf target) throws Exception;

	// Sadly Forge SimpleNetworkWrapper contains a discriminator byte which carpet doesnt support
	// Its used for sending multiple packets over the same plugin channel
	// This requires this insane hackfix to inject into forge skipping he discriminator for carpet...
	// This should somehow be changed to handle packets without discriminator natively with forge

	@Inject(method = "decode", at = @At("HEAD"), cancellable = true, remap = false)
	private void ignoreDiscriminator(ChannelHandlerContext ctx, FMLProxyPacket msg, List<Object> out, CallbackInfo ci) throws IllegalAccessException, InstantiationException {
		if (msg.channel().equals(CARPET_CHANNEL_NAME)) {
			testMessageValidity(msg);
			ByteBuf payload = msg.payload().duplicate();
			if (payload.readableBytes() < 1) {
				FMLLog.log.error("The FMLIndexedCodec has received an empty buffer on channel {}, likely a result of a LAN server issue. Pipeline parts : {}", ctx.channel().attr(NetworkRegistry.FML_CHANNEL), ctx.pipeline().toString());
			}
			A newMsg = (A) CarpetPacket.class.newInstance();
			ctx.channel().attr(INBOUNDPACKETTRACKER).get().set(new WeakReference<FMLProxyPacket>(msg));
			decodeInto(ctx, payload.slice(), newMsg);
			out.add(newMsg);
			payload.release();
			ci.cancel();
		}
	}

	@Inject(method = "encode", at = @At("HEAD"), cancellable = true, remap = false)
	private void ignoreDiscriminator(ChannelHandlerContext ctx, A msg, List<Object> out, CallbackInfo ci) throws Exception {
		if (ctx.channel().equals(CARPET_CHANNEL_NAME)) {
			String channel = ctx.channel().attr(NetworkRegistry.FML_CHANNEL).get();
			PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
			encodeInto(ctx, msg, buffer);
			FMLProxyPacket proxy = new FMLProxyPacket(buffer, channel);
			WeakReference<FMLProxyPacket> ref = ctx.channel().attr(INBOUNDPACKETTRACKER).get().get();
			FMLProxyPacket old = ref == null ? null : ref.get();
			if (old != null) {
				proxy.setDispatcher(old.getDispatcher());
			}
			out.add(proxy);
			ci.cancel();
		}
	}
}
