package net.dugged.cutelessmod.clientcommands.worldedit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.dugged.cutelessmod.clientcommands.ClientCommand;
import net.dugged.cutelessmod.clientcommands.TaskManager;
import net.dugged.cutelessmod.clientcommands.TaskSetBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class CommandFloodFill extends ClientCommand {

	@Override
	public String getName() {
		return "floodfill";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation(
			"text.cutelessmod.clientcommands.worldEdit.floodfill.usage").getUnformattedText();
	}

	private void checkAndAddNeighbor(World world, BlockPos neighbor, BlockPos startPos, int radius,
		ChunkPos currentChunk, List<BlockPos> checkedBlocks, List<BlockPos> blocksToCheck,
		Map<ChunkPos, BlockPos> chunkMap, List<ChunkPos> chunkList) {
		if (!(world.getBlockState(neighbor).getBlock() instanceof BlockAir)) {
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
			if (!chunkMap.containsKey(np)) {
				chunkList.add(np);
				chunkMap.put(np, neighbor);
			}
		}
	}

	private void floodFill(World world, IBlockState blockState, BlockPos startPos, int radius) {
		Map<BlockPos, IBlockState> blocksToPlace = new HashMap<>();
		List<ChunkPos> chunkList = new ArrayList<>();
		Map<ChunkPos, BlockPos> chunkMap = new HashMap<>();
		ChunkPos startChunk = world.getChunk(startPos).getPos();
		chunkList.add(startChunk);
		chunkMap.put(startChunk, startPos);
		while (!chunkList.isEmpty()) {
			ChunkPos currentChunk = chunkList.remove(0);
			BlockPos chunkStart = chunkMap.get(currentChunk);
			List<BlockPos> checkedBlocks = new ArrayList<>();
			List<BlockPos> blocksToCheck = new ArrayList<>();
			blocksToCheck.add(chunkStart);
			checkedBlocks.add(chunkStart);
			while (!blocksToCheck.isEmpty()) {
				if (Thread.interrupted()) {
					return;
				}
				BlockPos pos = blocksToCheck.remove(0);
				BlockPos up = pos.up();
				if (up.getY() <= startPos.getY()) {
					checkAndAddNeighbor(world, up, startPos, radius, currentChunk, checkedBlocks,
						blocksToCheck, chunkMap, chunkList);
				}
				checkAndAddNeighbor(world, pos.down(), startPos, radius, currentChunk,
					checkedBlocks, blocksToCheck, chunkMap, chunkList);
				checkAndAddNeighbor(world, pos.north(), startPos, radius, currentChunk,
					checkedBlocks, blocksToCheck, chunkMap, chunkList);
				checkAndAddNeighbor(world, pos.east(), startPos, radius, currentChunk,
					checkedBlocks, blocksToCheck, chunkMap, chunkList);
				checkAndAddNeighbor(world, pos.south(), startPos, radius, currentChunk,
					checkedBlocks, blocksToCheck, chunkMap, chunkList);
				checkAndAddNeighbor(world, pos.west(), startPos, radius, currentChunk,
					checkedBlocks, blocksToCheck, chunkMap, chunkList);
				blocksToPlace.put(pos, blockState);
			}
		}
		TaskSetBlock task = new TaskSetBlock(blocksToPlace, world);
		TaskManager.getInstance().addTask(task);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
		throws CommandException {
		if (args.length >= 1 && args.length <= 3) {
			World world = sender.getEntityWorld();
			BlockPos pos = WorldEdit.playerPos();
			if (world.getBlockState(pos).getBlock() instanceof BlockAir) {
				Block block = getBlockByText(sender, args[0]);
				IBlockState blockState = args.length >= 2 ? convertArgToBlockState(block, args[1])
					: block.getDefaultState();
				int radius = args.length == 3 ? parseInt(args[2]) : 100;
				Thread t = new Thread(() -> floodFill(world, blockState, pos, radius));
				t.start();
				TaskManager.getInstance().threads.add(t);
			} else {
				WorldEdit.sendMessage(new TextComponentTranslation(
					"text.cutelessmod.clientcommands.worldEdit.floodfill.noSpaceToFlood"));
			}
		} else {
			WorldEdit.sendMessage(new TextComponentTranslation(
				"text.cutelessmod.clientcommands.worldEdit.floodfill.usage"));
		}
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
		String[] args, @Nullable BlockPos pos) {
		if (args.length == 1) {
			return getListOfStringsMatchingLastWord(args, Block.REGISTRY.getKeys());
		}
		return Collections.emptyList();
	}
}