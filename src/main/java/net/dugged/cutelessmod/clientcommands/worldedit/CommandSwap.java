package net.dugged.cutelessmod.clientcommands.worldedit;

import static net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection.Position.A;
import static net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection.Position.B;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.dugged.cutelessmod.clientcommands.ClientCommand;
import net.dugged.cutelessmod.clientcommands.ClientCommandHandler;
import net.dugged.cutelessmod.clientcommands.HandlerSetBlock;
import net.dugged.cutelessmod.clientcommands.HandlerUndo;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class CommandSwap extends ClientCommand {

	@Override
	public String getName() {
		return "swap";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation(
			"text.cutelessmod.clientcommands.worldEdit.swap.usage").getUnformattedText();
	}

	public void swapBlocks(World world, WorldEditSelection selection, IBlockState state1,
		IBlockState state2, boolean ignoreBlockState1, boolean preserveMeta,
		boolean ignoreBlockState2) {
		HandlerSetBlock setBlockHandler = (HandlerSetBlock) ClientCommandHandler.instance.createHandler(
			HandlerSetBlock.class, world, selection);
		List<BlockPos> undoBlockPositions = new ArrayList<>();
		HandlerUndo undoHandler = (HandlerUndo) ClientCommandHandler.instance.createHandler(
			HandlerUndo.class, world, selection);
		undoHandler.setHandler(setBlockHandler);
		undoHandler.running = false;
		for (BlockPos pos : BlockPos.MutableBlockPos.getAllInBox(selection.getPos(A),
			selection.getPos(B))) {
			if (Thread.interrupted()) {
				return;
			}
			IBlockState currentState = world.getBlockState(pos);
			boolean isState1 = ignoreBlockState1 ? currentState.getBlock() == state1.getBlock()
				: currentState.equals(state1);
			boolean isState2 = ignoreBlockState2 ? currentState.getBlock() == state2.getBlock()
				: currentState.equals(state2);
			if (isState1) {
				undoBlockPositions.add(pos);
				if (preserveMeta) {
					int meta = currentState.getBlock().getMetaFromState(currentState);
					IBlockState newState = state2.getBlock().getStateFromMeta(meta);
					setBlockHandler.setBlock(pos, newState);
				} else {
					setBlockHandler.setBlock(pos, state2);
				}
			} else if (isState2) {
				undoBlockPositions.add(pos);
				if (preserveMeta) {
					int meta = currentState.getBlock().getMetaFromState(currentState);
					IBlockState newState = state1.getBlock().getStateFromMeta(meta);
					setBlockHandler.setBlock(pos, newState);
				} else {
					setBlockHandler.setBlock(pos, state1);
				}
			}
		}
		undoHandler.saveBlocks(undoBlockPositions);
		undoHandler.running = true;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
		throws CommandException {
		if (WorldEdit.hasCurrentSelection()) {
			WorldEditSelection selection = WorldEdit.getCurrentSelection();
			int len = args.length;
			if (len < 2 || len > 4) {
				WorldEdit.sendMessage(getUsage(sender));
				return;
			}
			final World world = sender.getEntityWorld();
			Block block1 = getBlockByText(sender, args[0]);
			IBlockState state1;
			boolean ignoreBlockState1;
			boolean preserveMeta;
			if (len >= 3) {
				if (args[1].equals("*")) {
					state1 = block1.getDefaultState();
					ignoreBlockState1 = true;
					preserveMeta = false;
				} else if (args[1].equals("#")) {
					state1 = block1.getDefaultState();
					ignoreBlockState1 = true;
					preserveMeta = true;
				} else {
					state1 = convertArgToBlockState(block1, args[1]);
					ignoreBlockState1 = false;
					preserveMeta = false;
				}
			} else {
				preserveMeta = false;
				ignoreBlockState1 = false;
				state1 = block1.getDefaultState();
			}
			Block block2;
			IBlockState state2;
			boolean ignoreBlockState2;
			if (len == 2) {
				ignoreBlockState2 = false;
				block2 = getBlockByText(sender, args[1]);
				state2 = block2.getDefaultState();
			} else if (len == 3) {
				ignoreBlockState2 = false;
				block2 = getBlockByText(sender, args[2]);
				state2 = block2.getDefaultState();
			} else {
				block2 = getBlockByText(sender, args[2]);
				if (args[3].equals("*")) {
					state2 = block2.getDefaultState();
					ignoreBlockState2 = true;
				} else {
					state2 = convertArgToBlockState(block2, args[3]);
					ignoreBlockState2 = false;
				}
			}
			Thread t = new Thread(
				() -> swapBlocks(world, selection, state1, state2, ignoreBlockState1, preserveMeta,
					ignoreBlockState2));
			t.start();
			ClientCommandHandler.instance.threads.add(t);
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