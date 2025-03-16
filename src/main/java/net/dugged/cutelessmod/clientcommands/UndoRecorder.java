package net.dugged.cutelessmod.clientcommands;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class UndoRecorder {

	public static Map<BlockPos, IBlockState> recordPositions(Set<BlockPos> positions, World world) {
		Map<BlockPos, IBlockState> data = new HashMap<>();
		for (BlockPos pos : positions) {
			data.put(pos, world.getBlockState(pos));
		}
		return data;
	}

	public static Map<BlockPos, IBlockState> recordRegion(BlockPos pos1, BlockPos pos2,
		World world) {
		int x1 = Math.min(pos1.getX(), pos2.getX());
		int y1 = Math.min(pos1.getY(), pos2.getY());
		int z1 = Math.min(pos1.getZ(), pos2.getZ());
		int x2 = Math.max(pos1.getX(), pos2.getX());
		int y2 = Math.max(pos1.getY(), pos2.getY());
		int z2 = Math.max(pos1.getZ(), pos2.getZ());
		Map<BlockPos, IBlockState> undoData = new HashMap<>();
		for (int x = x1; x <= x2; x++) {
			for (int y = y1; y <= y2; y++) {
				for (int z = z1; z <= z2; z++) {
					BlockPos pos = new BlockPos(x, y, z);
					undoData.put(pos, world.getBlockState(pos));
				}
			}
		}
		return undoData;
	}
}
