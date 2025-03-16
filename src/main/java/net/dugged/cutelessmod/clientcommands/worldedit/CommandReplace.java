package net.dugged.cutelessmod.clientcommands.worldedit;

import static net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection.Position.A;
import static net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection.Position.B;

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

public class CommandReplace extends ClientCommand {

	@Override
	public String getName() {
		return "replace";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation(
			"text.cutelessmod.clientcommands.worldEdit.replace.usage").getUnformattedText();
	}

	public void replaceBlocks(World world, WorldEditSelection selection, IBlockState stateToReplace,
		IBlockState replacementState, boolean ignoreBlockState, boolean preserveMeta) {
		Map<BlockPos, IBlockState> blocksToPlace = new HashMap<>();
		for (BlockPos pos : BlockPos.MutableBlockPos.getAllInBox(selection.getPos(A),
			selection.getPos(B))) {
			if (Thread.interrupted()) {
				return;
			}
			IBlockState blockState = world.getBlockState(pos);
			if (blockState == stateToReplace || (ignoreBlockState
				&& blockState.getBlock() == stateToReplace.getBlock())) {
				if (preserveMeta) {
					int meta = blockState.getBlock().getMetaFromState(blockState);
					IBlockState newReplacementState = replacementState.getBlock()
						.getStateFromMeta(meta);
					blocksToPlace.put(pos, newReplacementState);
				} else {
					blocksToPlace.put(pos, replacementState);
				}
			}
		}
		TaskSetBlock task = new TaskSetBlock(blocksToPlace, world);
		TaskManager.getInstance().addTask(task);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
		throws CommandException {
		if (WorldEdit.hasCurrentSelection()) {
			WorldEditSelection selection = WorldEdit.getCurrentSelection();
			if (args.length == 3 || args.length == 4) {
				final World world = sender.getEntityWorld();
				Block block1 = getBlockByText(sender, args[0]);
				boolean ignoreBlockState;
				boolean preserveMeta;
				IBlockState blockState1;
				if (args[1].equals("*")) {
					preserveMeta = false;
					ignoreBlockState = true;
					blockState1 = block1.getDefaultState();
				} else if (args[1].equals("#")) {
					ignoreBlockState = true;
					preserveMeta = true;
					blockState1 = block1.getDefaultState();
				} else {
					preserveMeta = false;
					ignoreBlockState = false;
					blockState1 = convertArgToBlockState(block1, args[1]);
				}
				Block block2 = getBlockByText(sender, args[2]);
				IBlockState blockState2;
				if (args.length == 4) {
					blockState2 = convertArgToBlockState(block2, args[3]);
				} else {
					blockState2 = block2.getDefaultState();
				}
				Thread t = new Thread(() ->
					replaceBlocks(world, selection, blockState1, blockState2, ignoreBlockState,
						preserveMeta));
				t.start();
				TaskManager.getInstance().threads.add(t);
			} else {
				WorldEdit.sendMessage(getUsage(sender));
			}
		} else {
			WorldEdit.sendMessage(new TextComponentTranslation(
				"text.cutelessmod.clientcommands.worldEdit.noAreaSelected"));
		}
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
		String[] args, @Nullable BlockPos pos) {
		if (args.length == 1 || args.length == 3) {
			return getListOfStringsMatchingLastWord(args, Block.REGISTRY.getKeys());
		} else {
			return Collections.emptyList();
		}
	}
}