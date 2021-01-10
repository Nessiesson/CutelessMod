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
		if (WorldEdit.hasSelection()) {
			if (args.length >= 0 && args.length <= 2) {
				HandlerSetBlock setBlockHandler = (HandlerSetBlock) ClientCommandHandler.instance.createHandler(HandlerSetBlock.class, sender.getEntityWorld());
				List<BlockPos> undoBlockPositions = new ArrayList<>();
				HandlerUndo undoHandler = (HandlerUndo) ClientCommandHandler.instance.createHandler(HandlerUndo.class, sender.getEntityWorld());
				undoHandler.setHandler(setBlockHandler);
				Block block = Blocks.GLOWSTONE;
				if (args.length > 0) {
					block = getBlockByText(sender, args[0]);
				}
				IBlockState blockstate = block.getDefaultState();
				if (args.length >= 2) {
					blockstate = convertArgToBlockState(block, args[1]);
				}
				BlockPos center = new BlockPos(WorldEdit.minPos().getX() + WorldEdit.widthX() / 2, WorldEdit.minPos().getY() + WorldEdit.widthY() / 2, WorldEdit.minPos().getZ() + WorldEdit.widthZ() / 2);
				setBlockHandler.setBlock(center, blockstate);
				undoBlockPositions.add(center);
				boolean x = WorldEdit.widthX() % 2 == 0;
				boolean y = WorldEdit.widthY() % 2 == 0;
				boolean z = WorldEdit.widthZ() % 2 == 0;
				if (x) {
					setBlockHandler.setBlock(center.west(), blockstate);
					undoBlockPositions.add(center.west());
				}
				if (y) {
					setBlockHandler.setBlock(center.down(), blockstate);
					undoBlockPositions.add(center.down());
				}
				if (z) {
					setBlockHandler.setBlock(center.north(), blockstate);
					undoBlockPositions.add(center.north());
				}
				if (x && y) {
					setBlockHandler.setBlock(center.west().down(), blockstate);
					undoBlockPositions.add(center.west().down());
				}
				if (y && z) {
					setBlockHandler.setBlock(center.down().north(), blockstate);
					undoBlockPositions.add(center.down().north());
				}
				if (x && z) {
					setBlockHandler.setBlock(center.west().north(), blockstate);
					undoBlockPositions.add(center.west().north());
				}
				if (x && y && z) {
					setBlockHandler.setBlock(center.west().down().north(), blockstate);
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
