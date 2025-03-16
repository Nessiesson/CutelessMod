package net.dugged.cutelessmod.clientcommands;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockHopper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public class TaskReplaceItem extends TaskChunk {

	public static boolean replaceItemPermission = false;
	private final Map<ChunkPos, Queue<ReplaceItemOperation>> opsMap = new HashMap<>();
	private final World world;
	private final int totalOps;
	private int processedOps = 0;

	public TaskReplaceItem(Map<BlockPos, ItemStack> containers, World world) {
		this.world = world;
		for (Map.Entry<BlockPos, ItemStack> entry : containers.entrySet()) {
			BlockPos pos = entry.getKey();
			ItemStack stack = entry.getValue();
			int invSize = getContainerSize(world.getBlockState(pos).getBlock());
			ReplaceItemOperation op = new ReplaceItemOperation(pos, stack, invSize);
			ChunkPos cp = new ChunkPos(pos);
			opsMap.computeIfAbsent(cp, k -> new LinkedList<>()).offer(op);
		}
		totalOps = containers.size();
	}

	private int getContainerSize(Block block) {
		if (block instanceof BlockDispenser) {
			return 9;
		} else if (block instanceof BlockHopper) {
			return 5;
		} else {
			return 27;
		}
	}

	@Override
	public int processChunk(ChunkPos pos, int maxPackets) {
		int count = 0;
		Queue<ReplaceItemOperation> queue = opsMap.get(pos);
		if (queue == null || !replaceItemPermission) {
			return 0;
		}
		while (count < maxPackets && !queue.isEmpty()) {
			ReplaceItemOperation op = queue.poll();
			for (int slot = 0; slot < op.inventorySize; slot++) {
				String cmd = String.format("/replaceitem block %d %d %d slot.container.%d %s %d %d",
					op.pos.getX(), op.pos.getY(), op.pos.getZ(),
					slot,
					op.stack.getItem().getRegistryName().toString(),
					op.stack.getCount(), op.stack.getItemDamage());
				sendCommand(cmd);
			}
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

	public static class ReplaceItemOperation {

		public final BlockPos pos;
		public final ItemStack stack;
		public final int inventorySize;

		public ReplaceItemOperation(BlockPos pos, ItemStack stack, int inventorySize) {
			this.pos = pos;
			this.stack = stack;
			this.inventorySize = inventorySize;
		}
	}
}