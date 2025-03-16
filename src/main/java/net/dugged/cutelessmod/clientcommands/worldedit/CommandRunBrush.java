package net.dugged.cutelessmod.clientcommands.worldedit;

import static net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection.Position.A;
import static net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection.Position.B;

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
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class CommandRunBrush extends ClientCommand {

	@Override
	public String getName() {
		return "runbrush";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation(
			"text.cutelessmod.clientcommands.worldEdit.runbrush.usage").getUnformattedText();
	}

	private void runBrush(World world, WorldEditSelection selection, IBlockState maskBlockState,
		BrushBase brush) {
		List<BlockPos> positions = new ArrayList<>();
		for (BlockPos pos : BlockPos.getAllInBox(selection.getPos(A), selection.getPos(B))) {
			if (Thread.interrupted()) {
				return;
			}
			if (world.getBlockState(pos).equals(maskBlockState)) {
				positions.addAll(brush.run(world, pos));
			}
		}
		if (!positions.isEmpty()) {
			Map<BlockPos, IBlockState> blocksToPlace = new HashMap<>();
			for (BlockPos pos : positions) {
				blocksToPlace.put(pos, brush.getBlockState());
			}
			TaskSetBlock task = new TaskSetBlock(blocksToPlace, world);
			TaskManager.getInstance().addTask(task);
		}
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
		throws CommandException {
		if (WorldEdit.hasCurrentSelection()) {
			if (args.length > 3 && args.length < 7) {
				Block maskBlock = getBlockByText(sender, args[0]);
				IBlockState maskBlockState = convertArgToBlockState(maskBlock, args[1]);
				BrushBase brush;
				try {
					brush = WorldEdit.getBrush(args[2]).getClass().newInstance();
				} catch (Exception e) {
					brush = null;
				}
				if (brush != null) {
					Block block = getBlockByText(sender, args[3]);
					IBlockState blockState = block.getDefaultState();
					if (args.length > 4) {
						blockState = convertArgToBlockState(block, args[4]);
					}
					brush.setBlockState(blockState);
					brush.setRadius(3);
					if (args.length > 5) {
						brush.setRadius(parseInt(args[5], 1));
					}
					BrushBase finalBrush = brush;
					Thread t = new Thread(
						() -> runBrush(sender.getEntityWorld(), WorldEdit.getCurrentSelection(),
							maskBlockState, finalBrush));
					t.start();
					TaskManager.getInstance().threads.add(t);
				} else {
					WorldEdit.sendMessage(new TextComponentTranslation(
						"text.cutelessmod.clientcommands.worldEdit.brush.notFound", args[0]));
				}
			} else {
				WorldEdit.sendMessage(getUsage(sender));
			}
		} else {
			WorldEdit.sendMessage(new TextComponentTranslation(
				"text.cutelessmod.clientcommands.worldEdit.noAreaSelected"));
		}
	}

	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
		String[] args, @Nullable BlockPos pos) {
		List<String> result = new ArrayList<>();
		if (args.length == 3) {
			for (BrushBase brush : WorldEdit.brushes) {
				result.add(brush.getName());
			}
			return getListOfStringsMatchingLastWord(args, result);
		} else if (args.length == 1 || args.length == 4) {
			return getListOfStringsMatchingLastWord(args, Block.REGISTRY.getKeys());
		} else {
			return Collections.emptyList();
		}
	}
}