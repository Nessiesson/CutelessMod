package net.dugged.cutelessmod.clientcommands.worldedit;

import static net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection.Position.A;
import static net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection.Position.B;

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


public class CommandSet extends ClientCommand {

	@Override
	public String getName() {
		return "set";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation(
			"text.cutelessmod.clientcommands.worldEdit.set.usage").getUnformattedText();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
		throws CommandException {
		if (WorldEdit.hasCurrentSelection()) {
			WorldEditSelection selection = WorldEdit.getCurrentSelection();
			if (args.length > 0 && args.length <= 2) {
				Block block = getBlockByText(sender, args[0]);
				IBlockState blockState = block.getDefaultState();
				if (args.length == 2) {
					blockState = convertArgToBlockState(block, args[1]);
				}
				HandlerFill fillHandler = (HandlerFill) ClientCommandHandler.instance.createHandler(
					HandlerFill.class, sender.getEntityWorld(), selection);
				HandlerUndo undoHandler = (HandlerUndo) ClientCommandHandler.instance.createHandler(
					HandlerUndo.class, sender.getEntityWorld(), selection);
				undoHandler.setHandler(fillHandler);
				undoHandler.saveBox(selection.getPos(A), selection.getPos(B));
				fillHandler.fill(selection.getPos(A), selection.getPos(B), blockState);
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
