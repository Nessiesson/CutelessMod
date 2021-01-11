package net.dugged.cutelessmod.clientcommands.worldedit;

import net.dugged.cutelessmod.clientcommands.*;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandReplace extends ClientCommand {
	@Override
	public String getName() {
		return "replace";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.replace.usage").getUnformattedText();
	}

	public void replaceBlocks(World world, IBlockState stateToReplace, IBlockState replacementState) {
		HandlerSetBlock setBlockHandler = (HandlerSetBlock) ClientCommandHandler.instance.createHandler(HandlerSetBlock.class, world);
		List<BlockPos> undoBlockPositions = new ArrayList<>();
		HandlerUndo undoHandler = (HandlerUndo) ClientCommandHandler.instance.createHandler(HandlerUndo.class, world);
		undoHandler.setHandler(setBlockHandler);
		undoHandler.running = false;
		for (BlockPos pos : BlockPos.MutableBlockPos.getAllInBox(WorldEdit.posA, WorldEdit.posB)) {
			if (world.getBlockState(pos) == stateToReplace) {
				undoBlockPositions.add(pos);
				setBlockHandler.setBlock(pos, replacementState);
			}
		}
		undoHandler.saveBlocks(undoBlockPositions);
		undoHandler.running = true;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (WorldEdit.hasSelection()) {
			if (args.length == 3 || args.length == 4) {
				final World world = sender.getEntityWorld();
				Block block1 = getBlockByText(sender, args[0]);
				IBlockState blockstate1 = convertArgToBlockState(block1, args[1]);
				Block block2 = getBlockByText(sender, args[2]);
				IBlockState blockstate2;
				if (args.length == 4) {
					blockstate2 = convertArgToBlockState(block2, args[3]);
				} else {
					blockstate2 =  block2.getDefaultState();
				}
				Thread t = new Thread(() -> replaceBlocks(world, blockstate1, blockstate2));
				t.start();
			} else {
				WorldEdit.sendMessage(getUsage(sender));
			}
		} else {
			WorldEdit.sendMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.noAreaSelected"));
		}
	}

	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
		if (args.length == 1 || args.length == 3) {
			return getListOfStringsMatchingLastWord(args, Block.REGISTRY.getKeys());
		} else {
			return Collections.emptyList();
		}
	}
}
