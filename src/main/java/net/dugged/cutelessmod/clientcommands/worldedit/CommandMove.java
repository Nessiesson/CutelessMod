package net.dugged.cutelessmod.clientcommands.worldedit;

import net.dugged.cutelessmod.clientcommands.ClientCommandHandler;
import net.dugged.cutelessmod.clientcommands.HandlerClone;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;

public class CommandMove extends CommandBase {
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
			HandlerClone handler = (HandlerClone) ClientCommandHandler.instance.createHandler(HandlerClone.class, sender.getEntityWorld());
			handler.isWorldEditHandler = true;
			handler.moveBlocks = true;
			if (args.length == 0) {
				handler.clone(WorldEdit.minPos(), WorldEdit.maxPos(), WorldEdit.playerPos());
			} else if (args.length == 1) {
				int blocksToMove = parseInt(args[0]);
				handler.clone(WorldEdit.minPos(), WorldEdit.maxPos(), WorldEdit.offsetLookingDirection(WorldEdit.minPos(), blocksToMove));
			} else {
				WorldEdit.sendMessage(getUsage(sender));
			}
		} else {
			WorldEdit.sendMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.noAreaSelected"));
		}
	}
}
