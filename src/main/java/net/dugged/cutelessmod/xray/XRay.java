package net.dugged.cutelessmod.xray;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

public class XRay {
	public boolean enabled;

	public boolean isHiddenByXray(final IBlockState state) {
		if (!this.enabled) {
			return false;
		}

		return !(state.getBlock() instanceof BlockHopper && state.getValue(BlockHopper.ENABLED));
		//return state == Blocks.STONE || state == Blocks.WATER || state == Blocks.FLOWING_WATER || state == Blocks.DIRT || state == Blocks.GRASS || state == Blocks.GRAVEL;
	}
}
