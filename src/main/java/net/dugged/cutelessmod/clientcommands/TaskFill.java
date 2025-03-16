package net.dugged.cutelessmod.clientcommands;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public class TaskFill extends TaskChunk {

	private static final int SUBCHUNK = 16;
	public static boolean fillPermission = false;
	private final Map<ChunkPos, Queue<CubeOperation>> opsMap = new HashMap<>();
	private final World world;
	private final int totalOps;
	private int processedOps = 0;

	public TaskFill(BlockPos pos1, BlockPos pos2, IBlockState state, World world) {
		this.world = world;
		BlockPos minPos = getMinPos(pos1, pos2);
		BlockPos maxPos = getMaxPos(pos1, pos2);
		Map<BlockPos, IBlockState> undoData = UndoRecorder.recordRegion(minPos, maxPos, world);
		UndoManager.getInstance().pushSnapshot(new UndoSnapshot(undoData));
		for (int x = minPos.getX(); x <= maxPos.getX(); x += SUBCHUNK) {
			int ex = Math.min(x + SUBCHUNK - 1, maxPos.getX());
			for (int y = minPos.getY(); y <= maxPos.getY(); y += SUBCHUNK) {
				int ey = Math.min(y + SUBCHUNK - 1, maxPos.getY());
				for (int z = minPos.getZ(); z <= maxPos.getZ(); z += SUBCHUNK) {
					int ez = Math.min(z + SUBCHUNK - 1, maxPos.getZ());
					CubeOperation op = new CubeOperation(x, y, z, ex, ey, ez, state);
					ChunkPos cp = new ChunkPos(new BlockPos(x, 0, z));
					opsMap.computeIfAbsent(cp, k -> new LinkedList<>()).offer(op);
				}
			}
		}
		totalOps = opsMap.values().stream().mapToInt(Queue::size).sum();
	}

	public TaskFill(Map<AxisAlignedBB, IBlockState> regionMap, World world) {
		this.world = world;
		Map<BlockPos, IBlockState> undoData = new HashMap<>();
		for (Map.Entry<AxisAlignedBB, IBlockState> entry : regionMap.entrySet()) {
			AxisAlignedBB bb = entry.getKey();
			IBlockState stateForRegion = entry.getValue();
			undoData.putAll(UndoRecorder.recordRegion(
				new BlockPos((int) bb.minX, (int) bb.minY, (int) bb.minZ),
				new BlockPos((int) bb.maxX, (int) bb.maxY, (int) bb.maxZ), world));
			for (int x = (int) bb.minX; x <= (int) bb.maxX; x += SUBCHUNK) {
				int ex = Math.min(x + SUBCHUNK - 1, (int) bb.maxX);
				for (int y = (int) bb.minY; y <= (int) bb.maxY; y += SUBCHUNK) {
					int ey = Math.min(y + SUBCHUNK - 1, (int) bb.maxY);
					for (int z = (int) bb.minZ; z <= (int) bb.maxZ; z += SUBCHUNK) {
						int ez = Math.min(z + SUBCHUNK - 1, (int) bb.maxZ);
						CubeOperation op = new CubeOperation(x, y, z, ex, ey, ez, stateForRegion);
						ChunkPos cp = new ChunkPos(new BlockPos(x, 0, z));
						opsMap.computeIfAbsent(cp, k -> new LinkedList<>()).offer(op);
					}
				}
			}
		}
		UndoManager.getInstance().pushSnapshot(new UndoSnapshot(undoData));
		totalOps = opsMap.values().stream().mapToInt(Queue::size).sum();
	}

	@Override
	public int processChunk(ChunkPos pos, int maxPackets) {
		int count = 0;
		Queue<CubeOperation> queue = opsMap.get(pos);
		if (queue == null || !fillPermission) {
			return 0;
		}
		while (count < maxPackets && !queue.isEmpty()) {
			CubeOperation op = queue.poll();
			String name = op.state.getBlock().getRegistryName().toString();
			int meta = op.state.getBlock().getMetaFromState(op.state);
			String cmd = String.format("/fill %d %d %d %d %d %d %s %d", op.x1, op.y1, op.z1, op.x2,
				op.y2, op.z2, name, meta);
			sendCommand(cmd);
			processedOps++;
			count++;
		}
		if (queue.isEmpty()) {
			opsMap.remove(pos);
		}
		return count;
	}

	@Override
	public int getTotalOperations() {
		return totalOps;
	}

	@Override
	public int getProcessedOperations() {
		return processedOps;
	}

	@Override
	public Set<ChunkPos> getPendingChunks() {
		return opsMap.keySet();
	}

	@Override
	public boolean isComplete() {
		return opsMap.isEmpty();
	}

	public static class CubeOperation {

		public final int x1, y1, z1, x2, y2, z2;
		public final IBlockState state;

		public CubeOperation(int x1, int y1, int z1, int x2, int y2, int z2, IBlockState state) {
			this.x1 = x1;
			this.y1 = y1;
			this.z1 = z1;
			this.x2 = x2;
			this.y2 = y2;
			this.z2 = z2;
			this.state = state;
		}
	}
}