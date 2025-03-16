package net.dugged.cutelessmod.clientcommands;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

public class UndoSnapshot {

	private final Map<BlockPos, IBlockState> snapshot;

	public UndoSnapshot(Map<BlockPos, IBlockState> snapshot) {
		this.snapshot = new HashMap<>(snapshot);
	}

	public Map<BlockPos, IBlockState> getSnapshot() {
		return snapshot;
	}
}