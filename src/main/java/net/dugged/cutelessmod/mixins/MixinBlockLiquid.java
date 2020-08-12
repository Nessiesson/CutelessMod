package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockLiquid.class)
public abstract class MixinBlockLiquid extends Block {
	public MixinBlockLiquid(final Material material) {
		super(material);
	}

	@Override
	public boolean isTranslucent(final IBlockState state) {
		return Configuration.showSmoothWaterLighting || super.isTranslucent(state);
	}
}
