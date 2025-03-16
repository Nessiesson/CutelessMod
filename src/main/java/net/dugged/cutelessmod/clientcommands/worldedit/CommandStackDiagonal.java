package net.dugged.cutelessmod.clientcommands.worldedit;

import static net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection.Position.A;
import static net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection.Position.B;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.dugged.cutelessmod.clientcommands.ClientCommand;
import net.dugged.cutelessmod.clientcommands.ClientCommandHandler;
import net.dugged.cutelessmod.clientcommands.HandlerClone;
import net.dugged.cutelessmod.clientcommands.HandlerUndo;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

public class CommandStackDiagonal extends ClientCommand {

	@Override
	public String getName() {
		return "stackdiagonal";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation(
			"text.cutelessmod.clientcommands.worldEdit.stackdiagonal.usage").getUnformattedText();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
		throws CommandException {
		if (WorldEdit.hasCurrentSelection()) {
			WorldEditSelection selection = WorldEdit.getCurrentSelection();
			if (args.length >= 4 && args.length <= 6) {
				int count = parseInt(args[0]);
				int xOffset = parseInt(args[1]);
				int yOffset = parseInt(args[2]);
				int zOffset = parseInt(args[3]);
				boolean masked = args.length >= 5 && parseBoolean(args[4]);
				boolean moveSelection = args.length == 6 && parseBoolean(args[5]);
				HandlerClone cloneHandler = (HandlerClone) ClientCommandHandler.instance.createHandler(
					HandlerClone.class, sender.getEntityWorld(), selection);
				cloneHandler.masked = masked;
				cloneHandler.moveSelectionAfterwards = false;
				HandlerUndo undoHandler = (HandlerUndo) ClientCommandHandler.instance.createHandler(
					HandlerUndo.class, sender.getEntityWorld(), selection);
				undoHandler.setHandler(cloneHandler);
				BlockPos minPos = selection.minPos();
				BlockPos maxPos = selection.maxPos();
				BlockPos endPos = maxPos;
				for (int i = 1; i <= count; i++) {
					undoHandler.saveBox(minPos, maxPos);
					BlockPos dest = minPos.add(i * xOffset, i * yOffset, i * zOffset);
					undoHandler.saveBox(dest,
						dest.add(selection.widthX(), selection.widthY(), selection.widthZ()));
					cloneHandler.clone(minPos, maxPos, dest);
					endPos = dest.add(selection.widthX(), selection.widthY(), selection.widthZ());
				}
				if (moveSelection) {
					selection.setPos(A,
						new BlockPos(Math.min(minPos.getX(), endPos.getX() - selection.widthX()),
							Math.min(minPos.getY(), endPos.getY() - selection.widthY()),
							Math.min(minPos.getZ(), endPos.getZ() - selection.widthZ())));
					selection.setPos(B, new BlockPos(Math.max(maxPos.getX(), endPos.getX()),
						Math.max(maxPos.getY(), endPos.getY()),
						Math.max(maxPos.getZ(), endPos.getZ())));
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
		if (args.length == 5 || args.length == 6) {
			return getListOfStringsMatchingLastWord(args, "true", "false");
		}
		return Collections.emptyList();
	}
}