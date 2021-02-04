package net.dugged.cutelessmod.clientcommands;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

import java.util.HashMap;
import java.util.Map;

public class RegistryCache {
	private static final Map<Block, String> cache = new HashMap<>();

	public static String getBlockName(IBlockState blockState) {
		return getBlockName(blockState.getBlock());
	}

	public static String getBlockName(Block block) {
		if (cache.containsKey(block)) {
			return (cache.get(block));
		} else {
			String name = block.getRegistryName().toString();
			cache.put(block, name);
			return name;
		}
	}
}
