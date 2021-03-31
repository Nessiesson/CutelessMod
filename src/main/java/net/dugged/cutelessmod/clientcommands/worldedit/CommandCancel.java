package net.dugged.cutelessmod.clientcommands.worldedit;

import net.dugged.cutelessmod.clientcommands.ClientCommand;
import net.dugged.cutelessmod.clientcommands.ClientCommandHandler;
import net.dugged.cutelessmod.clientcommands.Handler;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;

public class CommandCancel extends ClientCommand {
	@Override
	public String getName() {
		return "cancel";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.cancel.usage").getUnformattedText();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length == 0) {
			for (Handler handler : ClientCommandHandler.instance.handlers) {
				handler.finished = true;
			}
			WorldEdit.sendMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.cancel.cancelledAll"));
		} else {
			WorldEdit.sendMessage(getUsage(sender));
		}
	}
}
