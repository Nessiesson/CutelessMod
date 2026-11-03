package net.dugged.cleancuteless.mixins;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.dugged.cleancuteless.AreaSelectionRenderer;
import net.dugged.cleancuteless.Configuration;
import net.dugged.cleancuteless.DespawnSphereRenderer;
import net.dugged.cleancuteless.FrequencyAnalyzer;
import net.dugged.cleancuteless.ItemCounter;
import net.dugged.cleancuteless.PistonHelper;
import net.dugged.cleancuteless.RandomTickRenderer;
import net.dugged.cleancuteless.Zoomer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntitySelectors;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer {
	@Shadow
	private float farPlaneDistance;

	@Unique
	private float cleancutelessEyeHeight;
	@Unique
	private float cleancutelessLastEyeHeight;

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
		if (!Configuration.visuals.showHand) {
			ci.cancel();
		}
	}

	@SuppressWarnings("ConstantConditions")
	@Inject(method = "updateRenderer", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/multiplayer/WorldClient;getLightBrightness(Lnet/minecraft/util/math/BlockPos;)F"))
	private void updateEyeHeights(final CallbackInfo ci) {
		this.cleancutelessLastEyeHeight = this.cleancutelessEyeHeight;
		this.cleancutelessEyeHeight += (Minecraft.getMinecraft().getRenderViewEntity().getEyeHeight() - this.cleancutelessEyeHeight) * 0.5F;
	}

	@SuppressWarnings("ConstantConditions")
	@Redirect(method = "orientCamera", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;translate(FFF)V", ordinal = 4))
	private void eyeHeightTranslate(final float x, float y, final float z, final float partialTicks) {
		if (Configuration.visuals.showSneakEyeHeight && !Minecraft.getMinecraft().playerController.isSpectator()) {
			y += Minecraft.getMinecraft().getRenderViewEntity().getEyeHeight() - this.cleancutelessLastEyeHeight - partialTicks * (this.cleancutelessEyeHeight - this.cleancutelessLastEyeHeight);
		}

		GlStateManager.translate(x, y, z);
	}

	@Redirect(method = {"updateCameraAndRender", "updateRenderer"}, at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;mouseSensitivity:F", opcode = Opcodes.GETFIELD))
	private float cleancuteless$zoomMouseSensitivity(final GameSettings settings) {
		return Zoomer.INSTANCE.adjustMouseSensitivity(settings.mouseSensitivity);
	}

	@Redirect(method = {"updateCameraAndRender", "updateRenderer"}, at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;smoothCamera:Z", opcode = Opcodes.GETFIELD))
	private boolean cleancuteless$zoomSmoothCamera(final GameSettings settings) {
		return Zoomer.INSTANCE.adjustSmoothCamera(settings.smoothCamera);
	}

	@Redirect(method = "getFOVModifier", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/state/IBlockState;getMaterial()Lnet/minecraft/block/material/Material;"))
	private Material staticWaterFov(final IBlockState state) {
		return Configuration.visuals.waterAffectsFov ? state.getMaterial() : Material.AIR;
	}

	@Redirect(method = "setupCameraTransform", at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;viewBobbing:Z", opcode = Opcodes.GETFIELD))
	private boolean cleancleancuteless$doNotBobWorld(GameSettings instance) {
		return Configuration.visuals.showWorldBobbing && instance.viewBobbing;
	}

	@Redirect(method = "renderWorldPass", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isInsideOfMaterial(Lnet/minecraft/block/material/Material;)Z"))
	private boolean renderBlockSelectorUnderwater(final Entity entity, final Material material) {
		return !Configuration.visuals.showBlockSelectorUnderwater && entity.isInsideOfMaterial(material);
	}

	@Redirect(method = "getMouseOver", at = @At(value = "FIELD", target = "Lnet/minecraft/util/EntitySelectors;NOT_SPECTATING:Lcom/google/common/base/Predicate;"))
	private Predicate<Entity> cleancleancuteless$clickableSpectators() {
		return Minecraft.getMinecraft().player.isSpectator() ? e -> !(e instanceof EntityPlayerSP) : EntitySelectors.NOT_SPECTATING;
	}

	@Inject(method = "setupCameraTransform", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;matrixMode(I)V", ordinal = 0))
	private void cleancleancuteless$increaseFarPlane(final float partialTicks, final int pass, final CallbackInfo ci) {
		if (!Configuration.visuals.showWorldFog) {
			this.farPlaneDistance = Minecraft.getMinecraft().gameSettings.renderDistanceChunks * 512;
		}
	}
}
