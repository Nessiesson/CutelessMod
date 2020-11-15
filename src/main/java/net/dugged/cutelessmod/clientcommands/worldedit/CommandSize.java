package net.dugged.cutelessmod.clientcommands.worldedit;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;

public class CommandSize extends CommandBase {
	@Override
	public String getName() {
		return "size";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.set.usage").getUnformattedText();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (WorldEdit.hasSelection()) {
			WorldEdit.sendMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.size", WorldEdit.posA.getX(), WorldEdit.posA.getY(), WorldEdit.posA.getZ(), WorldEdit.posB.getX(), WorldEdit.posB.getY(), WorldEdit.posB.getZ(), WorldEdit.widthX(), WorldEdit.widthY(), WorldEdit.widthZ(), WorldEdit.volume()));
		} else {
			WorldEdit.sendMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.noAreaSelected"));
		}
	}
}
