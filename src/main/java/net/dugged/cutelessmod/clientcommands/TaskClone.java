package net.dugged.cutelessmod.clientcommands;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public class TaskClone extends TaskChunk {

	private static final int SUBCHUNK = 16;
	public static boolean clonePermission = false;
	private final Map<ChunkPos, Queue<CloneOperation>> opsMap = new HashMap<>();
	private final World world;
	private final Mode mode;
	private final int totalOps;
	private int processedOps = 0;

	public TaskClone(BlockPos src1, BlockPos src2, BlockPos dstOrigin, World world, Mode mode) {
		this.world = world;
		this.mode = mode;
		BlockPos minSrc = getMinPos(src1, src2);
		BlockPos maxSrc = getMaxPos(src1, src2);
		BlockPos maxDst = dstOrigin.add(maxSrc.getX() - minSrc.getX(),
			maxSrc.getY() - minSrc.getY(), maxSrc.getZ() - minSrc.getZ());
		Map<BlockPos, net.minecraft.block.state.IBlockState> undoData = UndoRecorder.recordRegion(
			dstOrigin, maxDst, world);
		UndoManager.getInstance().pushSnapshot(new UndoSnapshot(undoData));
		for (int x = minSrc.getX(); x <= maxSrc.getX(); x += SUBCHUNK) {
			int ex = Math.min(x + SUBCHUNK - 1, maxSrc.getX());
			for (int y = minSrc.getY(); y <= maxSrc.getY(); y += SUBCHUNK) {
				int ey = Math.min(y + SUBCHUNK - 1, maxSrc.getY());
				for (int z = minSrc.getZ(); z <= maxSrc.getZ(); z += SUBCHUNK) {
					int ez = Math.min(z + SUBCHUNK - 1, maxSrc.getZ());
					int dx = dstOrigin.getX() + (x - minSrc.getX());
					int dy = dstOrigin.getY() + (y - minSrc.getY());
					int dz = dstOrigin.getZ() + (z - minSrc.getZ());
					CloneOperation op = new CloneOperation(x, y, z, ex, ey, ez, dx, dy, dz);
					ChunkPos cp = new ChunkPos(new BlockPos(dx, dy, dz));
					opsMap.computeIfAbsent(cp, k -> new LinkedList<>()).offer(op);
				}
			}
		}

		totalOps = opsMap.values().stream().mapToInt(Queue::size).sum();
	}

	@Override
	public int processChunk(ChunkPos pos, int maxPackets) {
		int count = 0;
		Queue<CloneOperation> queue = opsMap.get(pos);
		if (queue == null) {
			return 0;
		}

		while (count < maxPackets && !queue.isEmpty()) {
			CloneOperation op = queue.poll();
			String cmd = String.format("/clone %d %d %d %d %d %d %d %d %d %s", op.x1, op.y1, op.z1,
				op.x2, op.y2, op.z2, op.x3, op.y3, op.z3, mode.getCommandArg());
			sendCommand(cmd);
			count++;
			processedOps++;
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

	public enum Mode {
		FORCE("replace force"), MASKED("masked force"), MOVE("replace move");

		private final String commandArg;

		Mode(String commandArg) {
			this.commandArg = commandArg;
		}

		public String getCommandArg() {
			return commandArg;
		}
	}

	public static class CloneOperation {

		public final int x1, y1, z1, x2, y2, z2;
		public final int x3, y3, z3;

		public CloneOperation(int x1, int y1, int z1, int x2, int y2, int z2, int x3, int y3,
			int z3) {
			this.x1 = x1;
			this.y1 = y1;
			this.z1 = z1;
			this.x2 = x2;
			this.y2 = y2;
			this.z2 = z2;
			this.x3 = x3;
			this.y3 = y3;
			this.z3 = z3;
		}
	}
}