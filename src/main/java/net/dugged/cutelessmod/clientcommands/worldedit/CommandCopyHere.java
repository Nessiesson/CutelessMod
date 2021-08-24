package net.dugged.cutelessmod.clientcommands.worldedit;

import net.dugged.cutelessmod.clientcommands.ClientCommand;
import net.dugged.cutelessmod.clientcommands.ClientCommandHandler;
import net.dugged.cutelessmod.clientcommands.HandlerClone;
import net.dugged.cutelessmod.clientcommands.HandlerUndo;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandCopyHere extends ClientCommand {
	@Override
	public String getName() {
		return "copyhere";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.copyhere.usage").getUnformattedText();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (WorldEdit.hasCurrentSelection()) {
			if (args.length == 0 || args.length == 1) {
				WorldEditSelection selection = WorldEdit.getCurrentSelection();
				boolean moveSelection = false;
				if (args.length == 1) {
					moveSelection = parseBoolean(args[0]);
				}
				HandlerClone cloneHandler = (HandlerClone) ClientCommandHandler.instance.createHandler(HandlerClone.class, sender.getEntityWorld(), selection);
				cloneHandler.moveSelectionAfterwards = moveSelection;
				HandlerUndo undoHandler = (HandlerUndo) ClientCommandHandler.instance.createHandler(HandlerUndo.class, sender.getEntityWorld(), selection);
				undoHandler.setHandler(cloneHandler);
				undoHandler.saveBox(selection.minPos(), selection.maxPos());
				undoHandler.saveBox(WorldEdit.playerPos(), WorldEdit.playerPos().add(selection.widthX(), selection.widthY(), selection.widthZ()));
				cloneHandler.clone(selection.minPos(), selection.maxPos(), WorldEdit.playerPos());
			} else {
				WorldEdit.sendMessage(getUsage(sender));
			}
		} else {
			WorldEdit.sendMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.noAreaSelected"));
		}
	}

	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
		if (args.length == 1) {
			return getListOfStringsMatchingLastWord(args, "true", "false");
		} else {
			return Collections.emptyList();
		}
	}
}
