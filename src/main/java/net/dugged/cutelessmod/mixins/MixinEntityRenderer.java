package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.AreaSelectionRenderer;
import net.dugged.cutelessmod.BeaconAreaRenderer;
import net.dugged.cutelessmod.Configuration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer {
	@Unique
	private float cutelessmodEyeHeight;
	@Unique
	private float cuteless;

	@Inject(method = "renderWorldPass", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V", args = "ldc=litParticles"))
	private void onPostRenderEntities(int pass, float partialTicks, long finishTimeNano, CallbackInfo ci) {
		AreaSelectionRenderer.render(partialTicks);
		BeaconAreaRenderer.render(partialTicks);
	}

	@Inject(method = "renderHand", at = @At("HEAD"), cancellable = true)
	private void hideHand(float partialTicks, int pass, CallbackInfo ci) {
		if (!Configuration.showHand) {
			ci.cancel();
		}
	}

	@SuppressWarnings("ConstantConditions")
	@Inject(method = "updateRenderer", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/multiplayer/WorldClient;getLightBrightness(Lnet/minecraft/util/math/BlockPos;)F"))
	private void updateEyeHeights(CallbackInfo ci) {
		this.cuteless = this.cutelessmodEyeHeight;
		this.cutelessmodEyeHeight += (Minecraft.getMinecraft().getRenderViewEntity().getEyeHeight() - this.cutelessmodEyeHeight) * 0.5F;
	}

	@SuppressWarnings("ConstantConditions")
	@Redirect(method = "orientCamera", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;translate(FFF)V", ordinal = 4))
	private void eyeHeightTranslate(float x, float y, float z, float partialTicks) {
		if (Configuration.showSneakEyeHeight) {
			y += Minecraft.getMinecraft().getRenderViewEntity().getEyeHeight() - this.cuteless - (this.cutelessmodEyeHeight - this.cuteless) * partialTicks;
		}

		GlStateManager.translate(x, y, z);
	}
}
