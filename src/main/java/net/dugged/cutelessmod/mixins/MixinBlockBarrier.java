package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBarrier;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockBarrier.class)
public abstract class MixinBlockBarrier extends Block {
	public MixinBlockBarrier(final Material material) {
		super(material);
	}

	@Override
	public EnumBlockRenderType getRenderType(final IBlockState state) {
		return Configuration.showBarrierBlocks ? super.getRenderType(state) : EnumBlockRenderType.INVISIBLE;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return Configuration.showBarrierBlocks ? BlockRenderLayer.TRANSLUCENT : super.getRenderLayer();
	}

	@Override
	public boolean shouldSideBeRendered(final IBlockState state, final IBlockAccess world, final BlockPos pos, final EnumFacing side) {
		if (!Configuration.showBarrierBlocks) {
			return super.shouldSideBeRendered(state, world, pos, side);
		}

		final IBlockState neighbour = world.getBlockState(pos.offset(side));
		final Block block = neighbour.getBlock();

		if (this == Blocks.BARRIER) {
			if (state != neighbour) {
				return true;
			}

			if (block == this) {
				return false;
			}
		}

		return super.shouldSideBeRendered(state, world, pos, side);
	}
}
