package net.dugged.cutelessmod.clientcommands.worldedit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import net.dugged.cutelessmod.clientcommands.ClientCommand;
import net.dugged.cutelessmod.clientcommands.TaskManager;
import net.dugged.cutelessmod.clientcommands.TaskSetBlock;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class CommandDrain extends ClientCommand {

	@Override
	public String getName() {
		return "drain";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation(
			"text.cutelessmod.clientcommands.worldEdit.drain.usage").getUnformattedText();
	}

	private void checkAndAddNeighbor(World world, BlockPos neighbor, BlockPos startPos, int radius,
		ChunkPos currentChunk, List<BlockPos> checkedBlocks, List<BlockPos> blocksToCheck,
		Map<ChunkPos, BlockPos> chunkMap, List<ChunkPos> chunkQueue,
		Map<BlockPos, IBlockState> blocksToPlace, boolean drainImmediate) {
		if (!(world.getBlockState(neighbor).getBlock() instanceof BlockLiquid)) {
			return;
		}
		if (!WorldEdit.checkCircle(neighbor.getX() - startPos.getX(),
			neighbor.getZ() - startPos.getZ(), radius)) {
			return;
		}
		ChunkPos np = world.getChunk(neighbor).getPos();
		if (np.equals(currentChunk)) {
			if (!checkedBlocks.contains(neighbor)) {
				blocksToCheck.add(neighbor);
				checkedBlocks.add(neighbor);
			}
		} else {
			if (drainImmediate) {
				blocksToPlace.put(neighbor, Blocks.AIR.getDefaultState());
			}
			if (!chunkMap.containsKey(np)) {
				chunkQueue.add(np);
				chunkMap.put(np, neighbor);
			}
		}
	}

	private void drainBody(World world, BlockPos startPos, int radius) {
		Map<BlockPos, IBlockState> blocksToPlace = new HashMap<>();
		Map<ChunkPos, BlockPos> chunkMap = new HashMap<>();
		Queue<ChunkPos> chunkQueue = new LinkedList<>();
		ChunkPos startChunk = world.getChunk(startPos).getPos();
		chunkQueue.add(startChunk);
		chunkMap.put(startChunk, startPos);
		int[][] offsets = {{0, 1, 0}, {0, -1, 0}, {0, 0, -1}, {1, 0, 0}, {0, 0, 1}, {-1, 0, 0}};
		boolean[] immediate = {false, false, true, true, true, true};
		while (!chunkQueue.isEmpty()) {
			ChunkPos currentChunk = chunkQueue.poll();
			BlockPos chunkStart = chunkMap.get(currentChunk);
			List<BlockPos> checkedBlocks = new ArrayList<>();
			List<BlockPos> blocksToCheck = new ArrayList<>();
			blocksToCheck.add(chunkStart);
			checkedBlocks.add(chunkStart);
			while (!blocksToCheck.isEmpty()) {
				BlockPos pos = blocksToCheck.remove(0);
				for (int i = 0; i < offsets.length; i++) {
					BlockPos neighbor = pos.add(offsets[i][0], offsets[i][1], offsets[i][2]);
					checkAndAddNeighbor(world, neighbor, startPos, radius, currentChunk,
						checkedBlocks, blocksToCheck, chunkMap, new ArrayList<>(chunkQueue),
						blocksToPlace, immediate[i]);
				}
				blocksToPlace.put(pos, Blocks.AIR.getDefaultState());
			}
		}
		TaskSetBlock task = new TaskSetBlock(blocksToPlace, world);
		TaskManager.getInstance().addTask(task);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
		throws CommandException {
		if (args.length == 0 || args.length == 1) {
			World world = sender.getEntityWorld();
			BlockPos pos = WorldEdit.playerPos();
			if (world.getBlockState(pos).getBlock() instanceof BlockLiquid) {
				int radius = args.length == 1 ? parseInt(args[0]) : 50;
				Thread t = new Thread(() -> drainBody(world, pos, radius));
				t.start();
				TaskManager.getInstance().threads.add(t);
			} else {
				WorldEdit.sendMessage(new TextComponentTranslation(
					"text.cutelessmod.clientcommands.worldEdit.drain.notInWater"));
			}
		} else {
			WorldEdit.sendMessage(new TextComponentTranslation(
				"text.cutelessmod.clientcommands.worldEdit.drain.usage"));
		}
	}
}