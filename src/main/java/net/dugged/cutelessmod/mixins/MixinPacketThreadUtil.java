package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.CutelessMod;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.util.IThreadListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PacketThreadUtil.class)
public abstract class MixinPacketThreadUtil {
	@Inject(method = "checkThreadAndEnqueue", at = @At("HEAD"))
	private static <T extends INetHandler> void countSendPackets(final Packet<T> packet, final T processor, final IThreadListener scheduler, final CallbackInfo ci) {
		CutelessMod.receivedPacketsThisTick++;
	}
}
