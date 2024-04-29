package net.dugged.cutelessmod.mixins;

import net.minecraft.client.renderer.entity.RenderPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(RenderPlayer.class)
public abstract class MixinRenderPlayer {
	@ModifyArg(method = "applyRotations(Lnet/minecraft/client/entity/AbstractClientPlayer;FFF)V", at = @At(value = "INVOKE", target = "Ljava/lang/Math;acos(D)D", remap = false))
	private double mc111516(final double a) {
		return Math.min(1D, a);
	}
}
