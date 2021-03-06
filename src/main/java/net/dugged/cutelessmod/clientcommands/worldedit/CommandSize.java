package net.dugged.cutelessmod.clientcommands.worldedit;

import net.dugged.cutelessmod.clientcommands.ClientCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;

public class CommandSize extends ClientCommand {
	@Override
	public String getName() {
		return "size";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.size.usage").getUnformattedText();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length == 0) {
			if (WorldEdit.hasSelection()) {
				WorldEdit.sendMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.size.response", WorldEdit.posA.getX(), WorldEdit.posA.getY(), WorldEdit.posA.getZ(), WorldEdit.posB.getX(), WorldEdit.posB.getY(), WorldEdit.posB.getZ(), WorldEdit.widthX(), WorldEdit.widthY(), WorldEdit.widthZ(), WorldEdit.volume()));
			} else {
				WorldEdit.sendMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.noAreaSelected"));
			}
		} else {
			WorldEdit.sendMessage(getUsage(sender));
		}
	}
}
