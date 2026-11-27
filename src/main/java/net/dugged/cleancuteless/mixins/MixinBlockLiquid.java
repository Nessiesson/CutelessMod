package net.dugged.cleancuteless.mixins;

import net.dugged.cleancuteless.Configuration;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockLiquid.class)
public abstract class MixinBlockLiquid extends Block {
	public MixinBlockLiquid(final Material material) {
		super(material);
	}

	@Override
	public boolean isTranslucent(final IBlockState state) {
		return Configuration.visuals.smoothWaterLighting || super.isTranslucent(state);
	}

	@Inject(method = "getRenderLayer", at = @At("HEAD"), cancellable = true)
	private void cleancuteless$makeLavaTransparent(CallbackInfoReturnable<BlockRenderLayer> cir) {
		if (Configuration.visuals.transparentLiquids) {
			cir.setReturnValue(BlockRenderLayer.TRANSLUCENT);
		}
	}
}
