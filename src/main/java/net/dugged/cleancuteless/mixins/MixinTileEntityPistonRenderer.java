package net.dugged.cleancuteless.mixins;

import net.dugged.cleancuteless.Configuration;
import net.dugged.cleancuteless.PistonModelCache;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityPistonRenderer;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(TileEntityPistonRenderer.class)
public abstract class MixinTileEntityPistonRenderer {
	private double cleancuteless$translationX;
	private double cleancuteless$translationY;
	private double cleancuteless$translationZ;

	//TODO: Fix piston Z-fighting
	@Redirect(method = "render(Lnet/minecraft/tileentity/TileEntityPiston;DDDFIF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;disableCull()V"))
	private void cullMovingBlocks(final TileEntityPiston te, final double x, final double y, final double z, final float partialTicks, final int destroyStage, final float alpha) {
		if (Configuration.building.smoothPistons) {
			GlStateManager.enableCull();
			GlStateManager.enablePolygonOffset();
			GlStateManager.doPolygonOffset(-te.getFacing().getIndex() * 0.01F, 0.01F);
		} else {
			GlStateManager.disableCull();
		}
	}

	@Inject(method = "render(Lnet/minecraft/tileentity/TileEntityPiston;DDDFIF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderHelper;enableStandardItemLighting()V"))
	private void uncullMovingBlocks(CallbackInfo ci) {
		if (Configuration.building.smoothPistons) {
			GlStateManager.disableCull();
			GlStateManager.disablePolygonOffset();
		}
	}

	/** Remember the translation the renderer applies to the buffer so it can be restored after a cache miss. */
	@Redirect(method = "render(Lnet/minecraft/tileentity/TileEntityPiston;DDDFIF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/BufferBuilder;setTranslation(DDD)V"))
	private void cleancuteless$trackTranslation(final BufferBuilder buffer, final double x, final double y, final double z) {
		this.cleancuteless$translationX = x;
		this.cleancuteless$translationY = y;
		this.cleancuteless$translationZ = z;
		buffer.setTranslation(x, y, z);
	}

	/**
	 * Replays cached vertices instead of re-running the (very expensive) block model + smooth
	 * lighting pipeline for a piston model that has already been tessellated this animation.
	 */
	@Redirect(method = "renderStateModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/BlockModelRenderer;renderModel(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/client/renderer/block/model/IBakedModel;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/renderer/BufferBuilder;Z)Z"))
	private boolean cleancuteless$cachedRenderModel(final BlockModelRenderer renderer, final IBlockAccess worldAccess, final IBakedModel model, final IBlockState state, final BlockPos pos, final BufferBuilder buffer, final boolean checkSides) {
		if (!Configuration.building.cachePistonModels || !(worldAccess instanceof World)) {
			return renderer.renderModel(worldAccess, model, state, pos, buffer, checkSides);
		}

		final World world = (World) worldAccess;
		if (PistonModelCache.emit(buffer, world, pos, state, checkSides)) {
			return true;
		}

		// Cache miss: render with no translation so the captured vertices are translation-free.
		// The real translation is applied when the cached quads are replayed via putPosition.
		final double x = this.cleancuteless$translationX;
		final double y = this.cleancuteless$translationY;
		final double z = this.cleancuteless$translationZ;
		final BufferBuilder.State before = buffer.getVertexState();
		boolean result = false;
		int[] raw = null;
		buffer.setTranslation(0D, 0D, 0D);
		try {
			result = renderer.renderModel(worldAccess, model, state, pos, buffer, checkSides);
			final int[] beforeData = before.getRawBuffer();
			final int[] afterData = buffer.getVertexState().getRawBuffer();
			if (afterData.length > beforeData.length) {
				raw = Arrays.copyOfRange(afterData, beforeData.length, afterData.length);
			}
		} finally {
			buffer.setVertexState(before);
			buffer.setTranslation(x, y, z);
		}

		PistonModelCache.store(buffer, world, pos, state, checkSides, raw);
		if (!PistonModelCache.emit(buffer, world, pos, state, checkSides)) {
			// Extremely unlikely (e.g. an empty model); fall back to the normal path.
			result = renderer.renderModel(worldAccess, model, state, pos, buffer, checkSides);
		}
		return result;
	}

	@ModifyConstant(method = "render(Lnet/minecraft/tileentity/TileEntityPiston;DDDFIF)V", constant = @Constant(floatValue = 0.25F))
	private float fixShortArm(final float value) {
		return 0.5F;
	}

	@ModifyConstant(method = "render(Lnet/minecraft/tileentity/TileEntityPiston;DDDFIF)V", constant = @Constant(floatValue = 1F))
	private float fixPistonBlink(final float value) {
		return Float.MAX_VALUE;
	}
}
