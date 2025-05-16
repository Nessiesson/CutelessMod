package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.world.WorldProvider;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer {
	@Shadow
	private float farPlaneDistance;
	@Unique
	private float cutelessmodEyeHeight;
	@Unique
	private float cutelessmodLastEyeHeight;

	@Inject(method = "renderWorldPass", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V", args = "ldc=litParticles"))
	private void onPostRenderEntities(final int pass, final float partialTicks, final long finishTimeNano, final CallbackInfo ci) {
		AreaSelectionRenderer.render(partialTicks);
		DespawnSphereRenderer.getInstance().render(partialTicks);
		FrequencyAnalyzer.render(partialTicks);
		ItemCounter.renderPos(partialTicks);
		RandomTickRenderer.getInstance().render(partialTicks);
		PistonHelper.draw(partialTicks);
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
		this.cutelessmodLastEyeHeight = this.cutelessmodEyeHeight;
		this.cutelessmodEyeHeight += (Minecraft.getMinecraft().getRenderViewEntity().getEyeHeight() - this.cutelessmodEyeHeight) * 0.5F;
	}

	@SuppressWarnings("ConstantConditions")
	@Redirect(method = "orientCamera", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;translate(FFF)V", ordinal = 4))
	private void eyeHeightTranslate(final float x, float y, final float z, final float partialTicks) {
		if (Configuration.showSneakEyeHeight && !Minecraft.getMinecraft().playerController.isSpectator()) {
			y += Minecraft.getMinecraft().getRenderViewEntity().getEyeHeight() - this.cutelessmodLastEyeHeight - partialTicks * (this.cutelessmodEyeHeight - this.cutelessmodLastEyeHeight);
		}

		GlStateManager.translate(x, y, z);
	}

	@Redirect(method = "getFOVModifier", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/state/IBlockState;getMaterial()Lnet/minecraft/block/material/Material;"))
	private Material staticWaterFov(final IBlockState state) {
		return Configuration.waterModifiesFoV ? state.getMaterial() : Material.AIR;
	}

	@Redirect(method = "setupCameraTransform", at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;viewBobbing:Z", opcode = Opcodes.GETFIELD))
	private boolean cutelessmod$doNotBobWorld(GameSettings instance) {
		return Configuration.showWorldBobbing && instance.viewBobbing;
	}

	@Redirect(method = "renderWorldPass", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isInsideOfMaterial(Lnet/minecraft/block/material/Material;)Z"))
	private boolean renderBlockSelectorUnderwater(final Entity entity, final Material material) {
		return !Configuration.showBlockSelectorUnderwater && entity.isInsideOfMaterial(material);
	}

	// Definitely not stolen from masa's tweakeroo. :)))
	// https://github.com/maruohon/tweakeroo/blob/0253bdd40bbe774f2fa781adb3dc0b89128c451d/src/main/java/tweakeroo/mixin/MixinEntityRenderer.java#L93
	@Inject(method = "setupFog",
			slice = @Slice(
					from = @At(value = "FIELD", ordinal = 1,
							target = "Lnet/minecraft/client/renderer/EntityRenderer;farPlaneDistance:F"),
					to = @At(value = "FIELD", ordinal = 1,
							target = "Lorg/lwjgl/opengl/ContextCapabilities;GL_NV_fog_distance:Z", remap = false)),
			at = @At(value = "INVOKE", shift = At.Shift.AFTER,
					target = "Lnet/minecraft/client/renderer/GlStateManager;setFogEnd(F)V"))
	private void disableRenderDistanceFog(final int startCoords, final float partialTicks, final CallbackInfo ci) {
		if (!Configuration.showWorldFog) {
			float renderDistance = this.farPlaneDistance;
			GlStateManager.setFogStart(renderDistance * 1.6F);
			GlStateManager.setFogEnd(renderDistance * 2F);
		}
	}

	@Redirect(method = "setupFog", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldProvider;doesXZShowFog(II)Z"))
	public boolean disableDimensionFog(final WorldProvider instance, final int x, final int z) {
		return Configuration.showWorldFog && instance.doesXZShowFog(x, z);
	}
}
