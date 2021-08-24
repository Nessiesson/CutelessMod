package net.dugged.cutelessmod.clientcommands.worldedit;

import net.dugged.cutelessmod.clientcommands.ClientCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import static net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection.Position.A;
import static net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection.Position.B;

public class CommandSelection extends ClientCommand {

	public CommandSelection() {
		creativeOnly = false;
	}

	@Override
	public String getName() {
		return "selection";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.selection.usage").getUnformattedText();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length == 1 && (args[0].toLowerCase().matches("clear") || args[0].toLowerCase().matches("c"))) {
			WorldEdit.clearAllSelections();
			WorldEdit.sendMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.selection.cleared"));
		} else if ((args.length == 2 | args.length == 3) && (args[0].toLowerCase().matches("expand") || args[0].toLowerCase().matches("move"))) {
			if (WorldEdit.hasCurrentSelection()) {
				WorldEditSelection selection = WorldEdit.getCurrentSelection();
				int amount = 1;
				if (args.length == 3) {
					amount = parseInt(args[2]);
				}
				if (args[1].toLowerCase().matches("up") || args[1].toLowerCase().matches("u")) {
					if (args[0].toLowerCase().matches("expand")) {
						if (selection.getPos(A).getY() > selection.getPos(B).getY()) {
							selection.setPos(A, selection.getPos(A).up(amount));
						} else {
							selection.setPos(B, selection.getPos(B).up(amount));
						}
					} else {
						selection.setPos(A, selection.getPos(A).up(amount));
						selection.setPos(B, selection.getPos(B).up(amount));
					}
				} else if (args[1].toLowerCase().matches("down") || args[1].toLowerCase().matches("d")) {
					if (args[0].toLowerCase().matches("expand")) {
						if (selection.getPos(A).getY() < selection.getPos(B).getY()) {
							selection.setPos(A, selection.getPos(A).down(amount));
						} else {
							selection.setPos(B, selection.getPos(B).down(amount));
						}
					} else {
						selection.setPos(A, selection.getPos(A).down(amount));
						selection.setPos(B, selection.getPos(B).down(amount));
					}
				} else if (args[1].toLowerCase().matches("north") || args[1].toLowerCase().matches("n")) {
					if (args[0].toLowerCase().matches("expand")) {
						if (selection.getPos(A).getZ() < selection.getPos(B).getZ()) {
							selection.setPos(A, selection.getPos(A).north(amount));
						} else {
							selection.setPos(B, selection.getPos(B).north(amount));
						}
					} else {
						selection.setPos(A, selection.getPos(A).north(amount));
						selection.setPos(B, selection.getPos(B).north(amount));
					}
				} else if (args[1].toLowerCase().matches("east") || args[1].toLowerCase().matches("e")) {
					if (args[0].toLowerCase().matches("expand")) {
						if (selection.getPos(A).getX() > selection.getPos(B).getX()) {
							selection.setPos(A, selection.getPos(A).east(amount));
						} else {
							selection.setPos(B, selection.getPos(B).east(amount));
						}
					} else {
						selection.setPos(A, selection.getPos(A).east(amount));
						selection.setPos(B, selection.getPos(B).east(amount));
					}
				} else if (args[1].toLowerCase().matches("south") || args[1].toLowerCase().matches("s")) {
					if (args[0].toLowerCase().matches("expand")) {
						if (selection.getPos(A).getZ() > selection.getPos(B).getZ()) {
							selection.setPos(A, selection.getPos(A).south(amount));
						} else {
							selection.setPos(B, selection.getPos(B).south(amount));
						}
					} else {
						selection.setPos(A, selection.getPos(A).south(amount));
						selection.setPos(B, selection.getPos(B).south(amount));
					}
				} else if (args[1].toLowerCase().matches("west") || args[1].toLowerCase().matches("w")) {
					if (args[0].toLowerCase().matches("expand")) {
						if (selection.getPos(A).getX() < selection.getPos(B).getX()) {
							selection.setPos(A, selection.getPos(A).west(amount));
						} else {
							selection.setPos(B, selection.getPos(B).west(amount));
						}
					} else {
						selection.setPos(A, selection.getPos(A).west(amount));
						selection.setPos(B, selection.getPos(B).west(amount));
					}
				} else {
					WorldEdit.sendMessage(getUsage(sender));
				}
			} else {
				WorldEdit.sendMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.noAreaSelected"));
			}
		} else {
			WorldEdit.sendMessage(getUsage(sender));
		}
	}

	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
		if (args.length == 1) {
			return getListOfStringsMatchingLastWord(args, "expand", "move", "clear");
		} else if (args.length == 2) {
			return getListOfStringsMatchingLastWord(args, "up", "down", "north", "east", "south", "west");
		} else {
			return Collections.emptyList();
		}
	}
}
