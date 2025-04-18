package net.dugged.cutelessmod.clientcommands.worldedit;

import static net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection.Position.A;
import static net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection.Position.B;

import java.util.HashMap;
import java.util.Map;
import net.dugged.cutelessmod.clientcommands.ClientCommand;
import net.dugged.cutelessmod.clientcommands.TaskManager;
import net.dugged.cutelessmod.clientcommands.TaskSetBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class CommandErode extends ClientCommand {

	private static final int[][] OFFSETS = new int[][]{
		{1, 0, 0},
		{-1, 0, 0},
		{0, 1, 0},
		{0, -1, 0},
		{0, 0, 1},
		{0, 0, -1}
	};

	@Override
	public String getName() {
		return "erode";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation(
			"text.cutelessmod.clientcommands.worldEdit.erode.usage").getUnformattedText();
	}

	public boolean isSurrounded(BlockPos pos, World world, WorldEditSelection selection) {
		for (int[] offset : OFFSETS) {
			BlockPos neighborPos = pos.add(offset[0], offset[1], offset[2]);
			if (neighborPos.getX() < selection.minPos().getX()
				|| neighborPos.getX() > selection.maxPos().getX() ||
				neighborPos.getY() < selection.minPos().getY()
				|| neighborPos.getY() > selection.maxPos().getY() ||
				neighborPos.getZ() < selection.minPos().getZ()
				|| neighborPos.getZ() > selection.maxPos().getZ()) {
				continue;
			}
			if (!world.getBlockState(neighborPos).isFullBlock()) {
				return false;
			}
		}
		return true;
	}

	public void erode(World world, WorldEditSelection selection) {
		Map<BlockPos, IBlockState> blocksToPlace = new HashMap<>();
		for (BlockPos pos : BlockPos.MutableBlockPos.getAllInBox(selection.getPos(A),
			selection.getPos(B))) {
			if (Thread.interrupted()) {
				return;
			}
			if (world.isAirBlock(pos) || !isSurrounded(pos, world, selection)) {
				continue;
			}
			blocksToPlace.put(pos, Blocks.AIR.getDefaultState());
		}
		if (!blocksToPlace.isEmpty()) {
			TaskSetBlock task = new TaskSetBlock(blocksToPlace, world);
			TaskManager.getInstance().addTask(task);
		}
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
		throws CommandException {
		if (args.length == 0) {
			if (WorldEdit.hasCurrentSelection()) {
				WorldEditSelection selection = WorldEdit.getCurrentSelection();
				final World world = sender.getEntityWorld();
				Thread t = new Thread(() -> erode(world, selection));
				t.start();
				TaskManager.getInstance().threads.add(t);
			} else {
				WorldEdit.sendMessage(new TextComponentTranslation(
					"text.cutelessmod.clientcommands.worldEdit.noAreaSelected"));
			}
		} else {
			WorldEdit.sendMessage(getUsage(sender));
		}
	}
}