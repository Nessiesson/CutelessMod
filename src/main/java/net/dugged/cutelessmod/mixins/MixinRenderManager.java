package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFireworkRocket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderManager.class)
public abstract class MixinRenderManager {
	@Inject(method = "renderEntityStatic", at = @At("HEAD"), cancellable = true)
	private void hideDeathAnimation(Entity entity, float f, boolean z, CallbackInfo ci) {
		if (Configuration.showDeathAnimations || !(entity instanceof EntityLivingBase)) {
			return;
		}

		final EntityLivingBase mob = (EntityLivingBase) entity;
		if (mob.deathTime > 0) {
			ci.cancel();
		}
	}

	@Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
	private void hideFireworksWhenRidden(Entity entity, ICamera camera, double camX, double camY, double camZ, CallbackInfoReturnable<Boolean> cir) {
		if (entity instanceof EntityFireworkRocket && ((EntityFireworkRocket) entity).isAttachedToEntity()) {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "renderDebugBoundingBox", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;disableBlend()V", ordinal = 0))
	private void cutelessmod$mc93479(final Entity entityIn, final double x, final double y, final double z, final float entityYaw, final float partialTicks, final CallbackInfo ci) {
		GlStateManager.glLineWidth(2);
	}
}
