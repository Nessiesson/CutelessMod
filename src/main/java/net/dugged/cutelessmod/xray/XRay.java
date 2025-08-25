package net.dugged.cutelessmod.xray;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class XRay {
	public boolean enabled;

	public boolean isHiddenByXray(final Block block) {
		if (!this.enabled) {
			return false;
		}

		return block == Blocks.STONE || block == Blocks.WATER || block == Blocks.FLOWING_WATER || block == Blocks.DIRT || block == Blocks.GRASS || block == Blocks.GRAVEL;
	}
}
