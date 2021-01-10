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

public class CommandMove extends ClientCommand {
	@Override
	public String getName() {
		return "move";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.move.usage").getUnformattedText();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (WorldEdit.hasSelection()) {
			HandlerClone cloneHandler = (HandlerClone) ClientCommandHandler.instance.createHandler(HandlerClone.class, sender.getEntityWorld());
			cloneHandler.moveBlocks = true;
			HandlerUndo undoHandler = (HandlerUndo) ClientCommandHandler.instance.createHandler(HandlerUndo.class, sender.getEntityWorld());
			undoHandler.setHandler(cloneHandler);
			if (args.length == 0) {
				undoHandler.saveBox(WorldEdit.minPos(), WorldEdit.maxPos());
				undoHandler.saveBox(WorldEdit.playerPos(), WorldEdit.playerPos().add(WorldEdit.widthX(), WorldEdit.widthY(), WorldEdit.widthZ()));
				cloneHandler.clone(WorldEdit.minPos(), WorldEdit.maxPos(), WorldEdit.playerPos());
			} else if (args.length == 1) {
				int blocksToMove = parseInt(args[0]);
				BlockPos pos = WorldEdit.offsetLookingDirection(WorldEdit.minPos(), blocksToMove);
				undoHandler.saveBox(WorldEdit.minPos(), WorldEdit.maxPos());
				undoHandler.saveBox(pos, pos.add(WorldEdit.widthX(), WorldEdit.widthY(), WorldEdit.widthZ()));
				cloneHandler.clone(WorldEdit.minPos(), WorldEdit.maxPos(), pos);
			} else {
				WorldEdit.sendMessage(getUsage(sender));
			}
		} else {
			WorldEdit.sendMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.noAreaSelected"));
		}
	}
}
