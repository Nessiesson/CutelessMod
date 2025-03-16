package net.dugged.cutelessmod.clientcommands.worldedit;

import java.util.Iterator;
import net.dugged.cutelessmod.clientcommands.ClientCommand;
import net.dugged.cutelessmod.clientcommands.TaskChunk;
import net.dugged.cutelessmod.clientcommands.TaskManager;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;

public class CommandCancel extends ClientCommand {

	public CommandCancel() {
		creativeOnly = false;
	}

	@Override
	public String getName() {
		return "cancel";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation(
			"text.cutelessmod.clientcommands.worldEdit.cancel.usage").getUnformattedText();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
		throws CommandException {
		if (args.length == 0) {
			Iterator<TaskChunk> it = TaskManager.getInstance().getTasks().iterator();
			while (it.hasNext()) {
				it.remove();
			}
			for (Thread thread : TaskManager.getInstance().threads) {
				thread.interrupt();
			}
			WorldEdit.sendMessage(new TextComponentTranslation(
				"text.cutelessmod.clientcommands.worldEdit.cancel.cancelledAll"));
		} else {
			WorldEdit.sendMessage(getUsage(sender));
		}
	}
}
