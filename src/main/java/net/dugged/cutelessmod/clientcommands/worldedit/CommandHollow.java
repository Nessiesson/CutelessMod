package net.dugged.cutelessmod.clientcommands.worldedit;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.dugged.cutelessmod.clientcommands.ClientCommand;
import net.dugged.cutelessmod.clientcommands.ClientCommandHandler;
import net.dugged.cutelessmod.clientcommands.HandlerFill;
import net.dugged.cutelessmod.clientcommands.HandlerUndo;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

public class CommandHollow extends ClientCommand {

	@Override
	public String getName() {
		return "hollow";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation(
			"text.cutelessmod.clientcommands.worldEdit.hollow.usage").getUnformattedText();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
		throws CommandException {
		if (WorldEdit.hasCurrentSelection()) {
			WorldEditSelection selection = WorldEdit.getCurrentSelection();
			if (args.length > 0 && args.length <= 3) {
				Block block = getBlockByText(sender, args[0]);
				IBlockState blockState = block.getDefaultState();
				int thickness = 0;
				if (args.length >= 2) {
					blockState = convertArgToBlockState(block, args[1]);
				}
				if (args.length >= 3) {
					thickness = parseInt(args[2]) - 1;
					if (thickness <= 0) {
						thickness = 0;
					}
				}
				HandlerFill fillHandler = (HandlerFill) ClientCommandHandler.instance.createHandler(
					HandlerFill.class, sender.getEntityWorld(), selection);
				HandlerUndo undoHandler = (HandlerUndo) ClientCommandHandler.instance.createHandler(
					HandlerUndo.class, sender.getEntityWorld(), selection);
				undoHandler.setHandler(fillHandler);
				BlockPos posMin = selection.minPos();
				BlockPos posMax = selection.maxPos();
				undoHandler.saveBox(posMin, new BlockPos(posMax.getX(), posMax.getY(),
					posMin.getZ() + Math.min(thickness, selection.widthZ() - 1)));
				fillHandler.fill(posMin, new BlockPos(posMax.getX(), posMax.getY(),
					posMin.getZ() + Math.min(thickness, selection.widthZ() - 1)), blockState);
				undoHandler.saveBox(posMin,
					new BlockPos(posMin.getX() + Math.min(thickness, selection.widthX() - 1),
						posMax.getY(), posMax.getZ()));
				fillHandler.fill(posMin,
					new BlockPos(posMin.getX() + Math.min(thickness, selection.widthX() - 1),
						posMax.getY(), posMax.getZ()), blockState);
				undoHandler.saveBox(posMin, new BlockPos(posMax.getX(),
					posMin.getY() + Math.min(thickness, selection.widthY() - 1), posMax.getZ()));
				fillHandler.fill(posMin, new BlockPos(posMax.getX(),
						posMin.getY() + Math.min(thickness, selection.widthY() - 1), posMax.getZ()),
					blockState);
				undoHandler.saveBox(posMax, new BlockPos(posMin.getX(), posMin.getY(),
					posMax.getZ() - Math.min(thickness, selection.widthZ() - 1)));
				fillHandler.fill(posMax, new BlockPos(posMin.getX(), posMin.getY(),
					posMax.getZ() - Math.min(thickness, selection.widthZ() - 1)), blockState);
				undoHandler.saveBox(posMax,
					new BlockPos(posMax.getX() - Math.min(thickness, selection.widthX() - 1),
						posMin.getY(), posMin.getZ()));
				fillHandler.fill(posMax,
					new BlockPos(posMax.getX() - Math.min(thickness, selection.widthX() - 1),
						posMin.getY(), posMin.getZ()), blockState);
				undoHandler.saveBox(posMax, new BlockPos(posMin.getX(),
					posMax.getY() - Math.min(thickness, selection.widthY() - 1), posMin.getZ()));
				fillHandler.fill(posMax, new BlockPos(posMin.getX(),
						posMax.getY() - Math.min(thickness, selection.widthY() - 1), posMin.getZ()),
					blockState);
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
		if (args.length == 1) {
			return getListOfStringsMatchingLastWord(args, Block.REGISTRY.getKeys());
		} else {
			return Collections.emptyList();
		}
	}
}
