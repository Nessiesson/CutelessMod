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
	private void onPostRenderEntities(final int pass, final float partialTicks, final long finishTimeNano, final CallbackInfo ci) {
		AreaSelectionRenderer.render(partialTicks);
		BeaconAreaRenderer.render(partialTicks);
	}

	@Inject(method = "renderHand", at = @At("HEAD"), cancellable = true)
	private void hideHand(final float partialTicks, final int pass, final CallbackInfo ci) {
		if (!Configuration.showHand) {
			ci.cancel();
		}
	}

	@SuppressWarnings("ConstantConditions")
	@Inject(method = "updateRenderer", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/multiplayer/WorldClient;getLightBrightness(Lnet/minecraft/util/math/BlockPos;)F"))
	private void updateEyeHeights(final CallbackInfo ci) {
		this.cuteless = this.cutelessmodEyeHeight;
		this.cutelessmodEyeHeight += (Minecraft.getMinecraft().getRenderViewEntity().getEyeHeight() - this.cutelessmodEyeHeight) * 0.5F;
	}

	@SuppressWarnings("ConstantConditions")
	@Redirect(method = "orientCamera", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;translate(FFF)V", ordinal = 4))
	private void eyeHeightTranslate(final float x, float y, final float z, final float partialTicks) {
		if (Configuration.showSneakEyeHeight) {
			y += Minecraft.getMinecraft().getRenderViewEntity().getEyeHeight() - this.cuteless - partialTicks * (this.cutelessmodEyeHeight - this.cuteless);
		}

		GlStateManager.translate(x, y, z);
	}
}
