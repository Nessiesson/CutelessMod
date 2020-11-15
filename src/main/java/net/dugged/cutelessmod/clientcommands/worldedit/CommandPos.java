package net.dugged.cutelessmod.clientcommands.worldedit;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;

public class CommandPos extends CommandBase {
	@Override
	public String getName() {
		return "pos";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.pos.usage").getUnformattedText();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length == 1) {
			if (parseInt(args[0]) == 0) {
				if (WorldEdit.posA == null || !WorldEdit.posA.equals(WorldEdit.playerPos())) {
					WorldEdit.posA = WorldEdit.playerPos();
				} else {
					WorldEdit.posA = null;
				}
			} else if (parseInt(args[0]) == 1) {
				if (WorldEdit.posB == null || !WorldEdit.posB.equals(WorldEdit.playerPos())) {
					WorldEdit.posB = WorldEdit.playerPos();
				} else {
					WorldEdit.posB = null;
				}
			} else {
				WorldEdit.sendMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.pos.invalidPosition"));
			}
		} else {
			WorldEdit.sendMessage(getUsage(sender));
		}
	}
}
