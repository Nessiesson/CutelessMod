package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.AreaSelectionRenderer;
import net.dugged.cutelessmod.Configuration;
import net.dugged.cutelessmod.CutelessMod;
import net.dugged.cutelessmod.chunk_display.gui.GuiChunkGrid;
import net.dugged.cutelessmod.clientcommands.worldedit.WorldEdit;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
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
	private float cutelessmodLastEyeHeight;

	@Inject(method = "renderWorldPass", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V", args = "ldc=litParticles"))
	private void onPostRenderEntities(final int pass, final float partialTicks, final long finishTimeNano, final CallbackInfo ci) {
		AreaSelectionRenderer.render(partialTicks);
		if (WorldEdit.posA != null | WorldEdit.posB != null) {
			final EntityPlayerSP player = Minecraft.getMinecraft().player;
			final double d1 = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
			final double d2 = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
			final double d3 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
			GlStateManager.depthMask(false);
			GlStateManager.disableTexture2D();
			GlStateManager.disableLighting();
			GlStateManager.disableCull();
			GlStateManager.disableBlend();
			GlStateManager.glLineWidth(4F);
			if (WorldEdit.posA != null) {
				AxisAlignedBB posBB = new AxisAlignedBB(WorldEdit.posA).offset(-d1, -d2, -d3);
				RenderGlobal.drawSelectionBoundingBox(posBB, 1F, 0F, 0F, 0.75F);
			}
			if (WorldEdit.posB != null) {
				AxisAlignedBB posBB = new AxisAlignedBB(WorldEdit.posB).offset(-d1, -d2, -d3);
				RenderGlobal.drawSelectionBoundingBox(posBB, 0F, 0F, 1F, 0.75F);
			}
			if (WorldEdit.posA != null && WorldEdit.posB != null) {
				AxisAlignedBB posBB = new AxisAlignedBB(WorldEdit.posA, WorldEdit.posB).offset(-d1, -d2, -d3).expand(1, 1, 1);
				//Purple BB to differentiate from structure block
				RenderGlobal.drawSelectionBoundingBox(posBB, 1.0F, 85.0F / 255, 1.0F, 0.75F);
			}
			GlStateManager.glLineWidth(1F);
			GlStateManager.enableTexture2D();
			GlStateManager.enableLighting();
			GlStateManager.enableCull();
			GlStateManager.disableBlend();
			GlStateManager.depthMask(true);
		}
		if (Configuration.showDungeonLocations && CutelessMod.dungeonPositions.size() > 0) {
			GlStateManager.depthMask(false);
			GlStateManager.disableFog();
			GlStateManager.disableLighting();
			GlStateManager.disableTexture2D();
			GlStateManager.glLineWidth(3F);
			for (BlockPos blockpos : CutelessMod.dungeonPositions.keySet()) {
				AxisAlignedBB posBB = new AxisAlignedBB(blockpos);
				RenderGlobal.drawSelectionBoundingBox(posBB, 1.0F, 1.0F, 1.0F, 0.75F);
			}
			GlStateManager.enableTexture2D();
			GlStateManager.enableLighting();
			GlStateManager.enableFog();
			GlStateManager.depthMask(true);
		}
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

	@Redirect(method = "renderWorldPass", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isInsideOfMaterial(Lnet/minecraft/block/material/Material;)Z"))
	private boolean renderBlockSelectorUnderwater(final Entity entity, final Material material) {
		return !Configuration.showBlockSelectorUnderwater && entity.isInsideOfMaterial(material);
	}
}
