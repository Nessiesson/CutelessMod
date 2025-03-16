package net.dugged.cutelessmod.clientcommands.worldedit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.dugged.cutelessmod.clientcommands.ClientCommand;
import net.dugged.cutelessmod.clientcommands.TaskManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class CommandPerimeterVolume extends ClientCommand {

	public CommandPerimeterVolume() {
		creativeOnly = false;
	}

	@Override
	public String getName() {
		return "perimetervolume";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation(
			"text.cutelessmod.clientcommands.worldEdit.perimetervolume.usage").getUnformattedText();
	}

	private void checkNeighbor(World world, BlockPos startPos, int radius, BlockPos neighbor,
		List<BlockPos> blocksToCheck, List<BlockPos> checkedBlocks) {
		if (world.isBlockLoaded(neighbor)
			&& world.getBlockState(neighbor).getBlock() instanceof BlockAir
			&& WorldEdit.checkCircle(neighbor.getX() - startPos.getX(),
			neighbor.getZ() - startPos.getZ(), radius)) {
			if (!checkedBlocks.contains(neighbor)) {
				blocksToCheck.add(neighbor);
				checkedBlocks.add(neighbor);
			}
		}
	}

	private void countBlocks(World world, BlockPos startPos, int radius, int minY) {
		List<BlockPos> checkedBlocks = new ArrayList<>();
		List<BlockPos> blocksToCheck = new ArrayList<>();
		blocksToCheck.add(startPos);
		checkedBlocks.add(startPos);
		int count = 0;
		while (!blocksToCheck.isEmpty()) {
			if (Thread.interrupted()) {
				return;
			}
			BlockPos pos = blocksToCheck.get(0);
			checkNeighbor(world, startPos, radius, pos.north(), blocksToCheck, checkedBlocks);
			checkNeighbor(world, startPos, radius, pos.east(), blocksToCheck, checkedBlocks);
			checkNeighbor(world, startPos, radius, pos.south(), blocksToCheck, checkedBlocks);
			checkNeighbor(world, startPos, radius, pos.west(), blocksToCheck, checkedBlocks);
			WorldEditRenderer.bbToRender.add(new WorldEditRenderer.RenderedBB(
				pos, new BlockPos(pos.getX(), minY, pos.getZ()), 4, 255, 0, 0));
			BlockPos currentPos = pos;
			while (currentPos.getY() >= minY) {
				final IBlockState block = world.getBlockState(currentPos);
				if (block.isNormalCube() && !block.getBlock().equals(Blocks.BEDROCK)) {
					count++;
				}
				currentPos = currentPos.down();
			}
			blocksToCheck.remove(0);
		}
		WorldEdit.sendMessage(new TextComponentTranslation(
			"text.cutelessmod.clientcommands.worldEdit.perimetervolume.response", count));
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
		throws CommandException {
		if (args.length == 0 || args.length == 1 || args.length == 2) {
			World world = sender.getEntityWorld();
			BlockPos pos = WorldEdit.playerPos();
			if (world.getBlockState(pos).getBlock() instanceof BlockAir) {
				int radius = (args.length >= 1) ? parseInt(args[0]) : 100;
				int minY = (args.length == 2) ? parseInt(args[1]) : 0;
				Thread t = new Thread(() -> countBlocks(world, pos, radius, minY));
				t.start();
				TaskManager.getInstance().threads.add(t);
			} else {
				WorldEdit.sendMessage(new TextComponentTranslation(
					"text.cutelessmod.clientcommands.worldEdit.floodfill.noSpaceToFlood"));
			}
		} else {
			WorldEdit.sendMessage(new TextComponentTranslation(
				"text.cutelessmod.clientcommands.worldEdit.perimetervolume.usage"));
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