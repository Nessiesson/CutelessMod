package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.CutelessMod;
import net.dugged.cutelessmod.Reference;
import net.minecraft.client.renderer.tileentity.TileEntityChestRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntityChestRenderer.class)
public abstract class MixinTileEntityChestRenderer extends TileEntitySpecialRenderer<TileEntityChest> {
	//TODO add & apply the same for trapped and handle christmas
	private static final ResourceLocation TEXTURE_NORMAL_FILLED = new ResourceLocation(Reference.MODID, "textures/chest_normal_filled.png");
	private static final ResourceLocation TEXTURE_NORMAL_FULL = new ResourceLocation(Reference.MODID, "textures/chest_normal_full.png");
	private static final ResourceLocation TEXTURE_NORMAL_DOUBLE_FILLED = new ResourceLocation(Reference.MODID, "textures/chest_normal_double_filled.png");
	private static final ResourceLocation TEXTURE_NORMAL_DOUBLE_FULL = new ResourceLocation(Reference.MODID, "textures/chest_normal_double_full.png");

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;enableRescaleNormal()V"))
	public void render(final TileEntityChest te, final double x, final double y, final double z, final float partialTicks, final int destroyStage, final float alpha, final CallbackInfo ci) {
		BlockPos pos = te.getPos();
		int usedSlots = 0;
		if (CutelessMod.spy.getChests().containsKey(pos)) {
			usedSlots = CutelessMod.spy.getChests().get(pos).countUsedSlots();
		}
		if (te.adjacentChestZNeg == null && te.adjacentChestXNeg == null && usedSlots > 0) {
			if (te.adjacentChestXPos == null && te.adjacentChestZPos == null) {
				if (usedSlots == 27) {
					this.bindTexture(TEXTURE_NORMAL_FULL);
				} else {
					this.bindTexture(TEXTURE_NORMAL_FILLED);
				}
			} else {
				BlockPos secondPos;
				if (te.adjacentChestXPos != null) {
					secondPos = te.adjacentChestXPos.getPos();
				} else {
					secondPos = te.adjacentChestZPos.getPos();
				}
				if (CutelessMod.spy.getChests().containsKey(secondPos)) {
					usedSlots += CutelessMod.spy.getChests().get(secondPos).countUsedSlots();
				}
				if (usedSlots == 54) {
					this.bindTexture(TEXTURE_NORMAL_DOUBLE_FULL);
				} else {
					this.bindTexture(TEXTURE_NORMAL_DOUBLE_FILLED);
				}
			}
		}
	}
}
