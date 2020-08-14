package net.dugged.cutelessmod.mixins;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityPistonRenderer;
import net.minecraft.tileentity.TileEntityPiston;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntityPistonRenderer.class)
public abstract class MixinTileEntityPistonRenderer {
	//TODO: Fix piston Z-fighting
	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;disableCull()V"))
	private void cullMovingBlocks(final TileEntityPiston te, final double x, final double y, final double z, final float partialTicks, final int destroyStage, final float alpha) {
		GlStateManager.enableCull();
		GlStateManager.enablePolygonOffset();
		GlStateManager.doPolygonOffset(-te.getFacing().getIndex() * 0.01F, 0.01F);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderHelper;enableStandardItemLighting()V"))
	private void uncullMovingBlocks(CallbackInfo ci) {
		GlStateManager.disableCull();
		GlStateManager.disablePolygonOffset();
	}

	@ModifyConstant(method = "render", constant = @Constant(floatValue = 0.25F))
	private float fixShortArm(final float value) {
		return 0.5F;
	}

	@ModifyConstant(method = "render", constant = @Constant(floatValue = 1F))
	private float fixPistonBlink(final float value) {
		return Float.MAX_VALUE;
	}
}
