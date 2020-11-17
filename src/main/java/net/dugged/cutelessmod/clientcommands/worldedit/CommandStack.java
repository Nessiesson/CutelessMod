package net.dugged.cutelessmod.clientcommands.worldedit;

import net.dugged.cutelessmod.clientcommands.ClientCommandHandler;
import net.dugged.cutelessmod.clientcommands.HandlerClone;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;

public class CommandStack extends CommandBase {
	@Override
	public String getName() {
		return "stack";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.stack.usage").getUnformattedText();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (WorldEdit.hasSelection()) {
			if (args.length == 1 || args.length == 2) {
				HandlerClone handler = (HandlerClone) ClientCommandHandler.instance.createHandler(HandlerClone.class, sender.getEntityWorld());
				handler.isWorldEditHandler = true;
				handler.moveBlocks = false;
				handler.moveSelectionAfterwards = false;
				int count = parseInt(args[0]);
				int blocksOffset = 0;
				if (args.length == 2) {
					blocksOffset = parseInt(args[1]);
				}
				for (int i = 1; i <= count; i++) {
					switch (WorldEdit.getLookingDirection().getAxis()) {
						case X:
							handler.clone(WorldEdit.minPos(), WorldEdit.maxPos(), WorldEdit.offsetLookingDirection(WorldEdit.minPos(), i * (blocksOffset + WorldEdit.widthX())));
							break;
						case Z:
							handler.clone(WorldEdit.minPos(), WorldEdit.maxPos(), WorldEdit.offsetLookingDirection(WorldEdit.minPos(), i * (blocksOffset + WorldEdit.widthZ())));
							break;
						case Y:
							handler.clone(WorldEdit.minPos(), WorldEdit.maxPos(), WorldEdit.offsetLookingDirection(WorldEdit.minPos(), i * (blocksOffset + WorldEdit.widthY())));
							break;
					}
				}
			} else {

				WorldEdit.sendMessage(getUsage(sender));
			}
		} else {
			WorldEdit.sendMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.noAreaSelected"));
		}
	}
}
