package net.dugged.cutelessmod.clientcommands.worldedit;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.dugged.cutelessmod.clientcommands.ClientCommand;
import net.dugged.cutelessmod.clientcommands.UndoManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class CommandUndo extends ClientCommand {

	private static final Minecraft mc = Minecraft.getMinecraft();

	@Override
	public String getName() {
		return "undo";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return getName();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
		throws CommandException {
		if (sender instanceof EntityPlayerSP) {
			if (mc.player.isCreative() || mc.player.isSpectator()) {
				World world = sender.getEntityWorld();
				if (args.length >= 1) {
					if (args[0].equalsIgnoreCase("toggle")) {
						UndoManager.savingUndo = !UndoManager.savingUndo;
						if (UndoManager.savingUndo) {
							WorldEdit.sendMessage(new TextComponentTranslation(
								"text.cutelessmod.clientcommands.undo.enabledUndo"));
						} else {
							WorldEdit.sendMessage(new TextComponentTranslation(
								"text.cutelessmod.clientcommands.undo.disabledUndo"));
						}
					} else {
						UndoManager.getInstance().undoLast(world);
					}
				} else {
					UndoManager.getInstance().undoLast(world);
				}
			} else {
				throw new CommandException("text.cutelessmod.clientcommands.wrongGamemode");
			}
		}
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
		String[] args, @Nullable net.minecraft.util.math.BlockPos pos) {
		if (args.length == 1) {
			return getListOfStringsMatchingLastWord(args, "toggle", "redo");
		} else {
			return Collections.emptyList();
		}
	}
}