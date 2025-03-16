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
import net.minecraft.world.World;

public class CommandMove extends ClientCommand {

	@Override
	public String getName() {
		return "move";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation(
			"text.cutelessmod.clientcommands.worldEdit.move.usage").getUnformattedText();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
		throws CommandException {
		if (WorldEdit.hasCurrentSelection()) {
			WorldEditSelection selection = WorldEdit.getCurrentSelection();
			if (args.length == 0 || args.length == 1) {
				World world = sender.getEntityWorld();
				int distance = 0;
				if (args.length == 1) {
					distance = parseInt(args[0]);
				}
				HandlerClone cloneHandler = (HandlerClone) ClientCommandHandler.instance.createHandler(
					HandlerClone.class, world, selection);
				cloneHandler.moveBlocks = true;
				HandlerUndo undoHandler = (HandlerUndo) ClientCommandHandler.instance.createHandler(
					HandlerUndo.class, world, selection);
				undoHandler.setHandler(cloneHandler);
				if (distance > 0) {
					final BlockPos pos = WorldEdit.offsetLookingDirection(selection.minPos(),
						distance);
					undoHandler.saveBox(selection.minPos(), selection.maxPos());
					undoHandler.saveBox(pos,
						pos.add(selection.widthX(), selection.widthY(), selection.widthZ()));
					cloneHandler.clone(selection.minPos(), selection.maxPos(), pos);
				} else {
					undoHandler.saveBox(selection.minPos(), selection.maxPos());
					undoHandler.saveBox(WorldEdit.playerPos(), WorldEdit.playerPos()
						.add(selection.widthX(), selection.widthY(), selection.widthZ()));
					cloneHandler.clone(selection.minPos(), selection.maxPos(),
						WorldEdit.playerPos());
				}
			} else {
				WorldEdit.sendMessage(getUsage(sender));
			}
		} else {
			WorldEdit.sendMessage(new TextComponentTranslation(
				"text.cutelessmod.clientcommands.worldEdit.noAreaSelected"));
		}
	}
}
