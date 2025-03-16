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
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class CommandOutlineFill extends ClientCommand {

	@Override
	public String getName() {
		return "outlinefill";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation(
			"text.cutelessmod.clientcommands.worldEdit.outlinefill.usage").getUnformattedText();
	}

	private void checkNeighbor(World world, BlockPos neighbor, BlockPos startPos, int radius,
		List<BlockPos> blocksToCheck, List<BlockPos> checkedBlocks) {
		if (world.getBlockState(neighbor).getBlock() instanceof BlockAir &&
			WorldEdit.checkCircle(neighbor.getX() - startPos.getX(),
				neighbor.getZ() - startPos.getZ(), radius)) {
			if (!checkedBlocks.contains(neighbor)) {
				blocksToCheck.add(neighbor);
				checkedBlocks.add(neighbor);
			}
		}
	}

	private void outLineFill(World world, IBlockState blockState, BlockPos startPos, int height,
		int radius) {
		Map<BlockPos, IBlockState> blocksToPlace = new HashMap<>();
		List<BlockPos> checkedBlocks = new ArrayList<>();
		List<BlockPos> blocksToCheck = new ArrayList<>();
		blocksToCheck.add(startPos);
		checkedBlocks.add(startPos);
		while (!blocksToCheck.isEmpty()) {
			if (Thread.interrupted()) {
				return;
			}
			BlockPos pos = blocksToCheck.get(0);
			checkNeighbor(world, pos.north(), startPos, radius, blocksToCheck, checkedBlocks);
			checkNeighbor(world, pos.east(), startPos, radius, blocksToCheck, checkedBlocks);
			checkNeighbor(world, pos.south(), startPos, radius, blocksToCheck, checkedBlocks);
			checkNeighbor(world, pos.west(), startPos, radius, blocksToCheck, checkedBlocks);
			WorldEditRenderer.bbToRender.add(new WorldEditRenderer.RenderedBB(
				pos,
				new BlockPos(pos.getX(), Math.max(pos.getY() + height, 0), pos.getZ()),
				4, 255, 0, 0));
			for (int y = pos.getY(); y < pos.getY() + height; y++) {
				BlockPos columnPos = new BlockPos(pos.getX(), Math.max(y, 0), pos.getZ());
				blocksToPlace.put(columnPos, blockState);
			}
			blocksToCheck.remove(0);
		}
		TaskSetBlock task = new TaskSetBlock(blocksToPlace, world);
		TaskManager.getInstance().addTask(task);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
		throws CommandException {
		if (args.length == 3 || args.length == 4) {
			World world = sender.getEntityWorld();
			BlockPos pos = WorldEdit.playerPos();
			if (world.getBlockState(pos).getBlock() instanceof BlockAir) {
				Block block = getBlockByText(sender, args[0]);
				IBlockState blockState = convertArgToBlockState(block, args[1]);
				int height = parseInt(args[2]);
				int radius = (args.length == 4) ? parseInt(args[3]) : 100;
				Thread t = new Thread(() -> outLineFill(world, blockState, pos, height, radius));
				t.start();
				TaskManager.getInstance().threads.add(t);
			} else {
				WorldEdit.sendMessage(new TextComponentTranslation(
					"text.cutelessmod.clientcommands.worldEdit.floodfill.noSpaceToFlood"));
			}
		} else {
			WorldEdit.sendMessage(new TextComponentTranslation(
				"text.cutelessmod.clientcommands.worldEdit.outlinefill.usage"));
		}
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
		String[] args, @Nullable BlockPos pos) {
		if (args.length == 1) {
			return getListOfStringsMatchingLastWord(args, Block.REGISTRY.getKeys());
		} else {
			return Collections.emptyList();
		}
	}
}