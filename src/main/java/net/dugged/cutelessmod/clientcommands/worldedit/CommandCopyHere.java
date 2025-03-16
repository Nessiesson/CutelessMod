package net.dugged.cutelessmod.clientcommands.worldedit;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.dugged.cutelessmod.clientcommands.ClientCommand;
import net.dugged.cutelessmod.clientcommands.TaskClone;
import net.dugged.cutelessmod.clientcommands.TaskManager;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

public class CommandCopyHere extends ClientCommand {

	@Override
	public String getName() {
		return "copyhere";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation(
			"text.cutelessmod.clientcommands.worldEdit.copyhere.usage").getUnformattedText();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
		throws CommandException {
		if (WorldEdit.hasCurrentSelection()) {
			if (args.length == 0 || args.length == 1) {
				WorldEditSelection selection = WorldEdit.getCurrentSelection();
				boolean moveSelection = false;
				if (args.length == 1) {
					moveSelection = parseBoolean(args[0]);
				}
				TaskClone.Mode mode = moveSelection ? TaskClone.Mode.MOVE : TaskClone.Mode.FORCE;
				TaskClone task = new TaskClone(selection.minPos(), selection.maxPos(),
					WorldEdit.playerPos(), sender.getEntityWorld(), mode);
				Thread t = new Thread(() -> TaskManager.getInstance().addTask(task));
				t.start();
				TaskManager.getInstance().threads.add(t);
			} else {
				WorldEdit.sendMessage(new TextComponentTranslation(
					"text.cutelessmod.clientcommands.worldEdit.copyhere.usage"));
			}
		} else {
			WorldEdit.sendMessage(new TextComponentTranslation(
				"text.cutelessmod.clientcommands.worldEdit.noAreaSelected"));
		}
	}

	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
		String[] args, @Nullable BlockPos pos) {
		if (args.length == 1) {
			return getListOfStringsMatchingLastWord(args, "true", "false");
		} else {
			return Collections.emptyList();
		}
	}
}