package net.dugged.cutelessmod.clientcommands.worldedit;

import net.dugged.cutelessmod.clientcommands.ClientCommand;
import net.dugged.cutelessmod.clientcommands.ClientCommandHandler;
import net.dugged.cutelessmod.clientcommands.HandlerSetBlock;
import net.dugged.cutelessmod.clientcommands.HandlerUndo;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandCenter extends ClientCommand {
	@Override
	public String getName() {
		return "center";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.center.usage").getUnformattedText();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (WorldEdit.hasCurrentSelection()) {
			if (args.length >= 0 && args.length <= 2) {
				WorldEditSelection selection = WorldEdit.getCurrentSelection();
				Block block = Blocks.GLOWSTONE;
				if (args.length > 0) {
					block = getBlockByText(sender, args[0]);
				}
				IBlockState blockState = block.getDefaultState();
				if (args.length >= 2) {
					blockState = convertArgToBlockState(block, args[1]);
				}
				HandlerSetBlock setBlockHandler = (HandlerSetBlock) ClientCommandHandler.instance.createHandler(HandlerSetBlock.class, sender.getEntityWorld(), selection);
				List<BlockPos> undoBlockPositions = new ArrayList<>();
				HandlerUndo undoHandler = (HandlerUndo) ClientCommandHandler.instance.createHandler(HandlerUndo.class, sender.getEntityWorld(), selection);
				undoHandler.setHandler(setBlockHandler);
				BlockPos center = new BlockPos(selection.minPos().getX() + selection.widthX() / 2, selection.minPos().getY() + selection.widthY() / 2, selection.minPos().getZ() + selection.widthZ() / 2);
				setBlockHandler.setBlock(center, blockState);
				undoBlockPositions.add(center);
				boolean x = selection.widthX() % 2 == 0;
				boolean y = selection.widthY() % 2 == 0;
				boolean z = selection.widthZ() % 2 == 0;
				if (x) {
					setBlockHandler.setBlock(center.west(), blockState);
					undoBlockPositions.add(center.west());
				}
				if (y) {
					setBlockHandler.setBlock(center.down(), blockState);
					undoBlockPositions.add(center.down());
				}
				if (z) {
					setBlockHandler.setBlock(center.north(), blockState);
					undoBlockPositions.add(center.north());
				}
				if (x && y) {
					setBlockHandler.setBlock(center.west().down(), blockState);
					undoBlockPositions.add(center.west().down());
				}
				if (y && z) {
					setBlockHandler.setBlock(center.down().north(), blockState);
					undoBlockPositions.add(center.down().north());
				}
				if (x && z) {
					setBlockHandler.setBlock(center.west().north(), blockState);
					undoBlockPositions.add(center.west().north());
				}
				if (x && y && z) {
					setBlockHandler.setBlock(center.west().down().north(), blockState);
					undoBlockPositions.add(center.west().down().north());
				}
				undoHandler.saveBlocks(undoBlockPositions);
			} else {
				WorldEdit.sendMessage(getUsage(sender));
			}
		} else {
			WorldEdit.sendMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.noAreaSelected"));
		}
	}

	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
		if (args.length == 1) {
			return getListOfStringsMatchingLastWord(args, Block.REGISTRY.getKeys());
		} else {
			return Collections.emptyList();
		}
	}
}
