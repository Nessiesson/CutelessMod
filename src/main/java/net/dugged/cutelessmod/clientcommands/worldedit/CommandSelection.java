package net.dugged.cutelessmod.clientcommands.worldedit;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandSelection extends CommandBase {
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
			WorldEdit.posA = null;
			WorldEdit.posB = null;
			WorldEdit.sendMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.selection.cleared"));
		} else if ((args.length == 2 | args.length == 3) && (args[0].toLowerCase().matches("expand") || args[0].toLowerCase().matches("move"))) {
			if (WorldEdit.hasSelection()) {
				int amount = 1;
				if (args.length == 3) {
					amount = parseInt(args[2]);
				}
				System.out.println(args);
				if (args[1].toLowerCase().matches("up") || args[1].toLowerCase().matches("u")) {
					if (args[0].toLowerCase().matches("expand")) {
						if (WorldEdit.posA.getY() > WorldEdit.posB.getY()) {
							WorldEdit.posA = WorldEdit.posA.up(amount);
						} else {
							WorldEdit.posB = WorldEdit.posB.up(amount);
						}
					} else {
						WorldEdit.posA = WorldEdit.posA.up(amount);
						WorldEdit.posB = WorldEdit.posB.up(amount);
					}
				} else if (args[1].toLowerCase().matches("down") || args[1].toLowerCase().matches("d")) {
					if (args[0].toLowerCase().matches("expand")) {
						if (WorldEdit.posA.getY() < WorldEdit.posB.getY()) {
							WorldEdit.posA = WorldEdit.posA.down(amount);
						} else {
							WorldEdit.posB = WorldEdit.posB.down(amount);
						}
					} else {
						WorldEdit.posA = WorldEdit.posA.down(amount);
						WorldEdit.posB = WorldEdit.posB.down(amount);
					}
				} else if (args[1].toLowerCase().matches("north") || args[1].toLowerCase().matches("n")) {
					if (args[0].toLowerCase().matches("expand")) {
						if (WorldEdit.posA.getZ() < WorldEdit.posB.getZ()) {
							WorldEdit.posA = WorldEdit.posA.north(amount);
						} else {
							WorldEdit.posB = WorldEdit.posB.north(amount);
						}
					} else {
						WorldEdit.posA = WorldEdit.posA.north(amount);
						WorldEdit.posB = WorldEdit.posB.north(amount);
					}
				} else if (args[1].toLowerCase().matches("east") || args[1].toLowerCase().matches("e")) {
					if (args[0].toLowerCase().matches("expand")) {
						if (WorldEdit.posA.getX() > WorldEdit.posB.getX()) {
							WorldEdit.posA = WorldEdit.posA.east(amount);
						} else {
							WorldEdit.posB = WorldEdit.posB.east(amount);
						}
					} else {
						WorldEdit.posA = WorldEdit.posA.east(amount);
						WorldEdit.posB = WorldEdit.posB.east(amount);
					}
				} else if (args[1].toLowerCase().matches("south") || args[1].toLowerCase().matches("s")) {
					if (args[0].toLowerCase().matches("expand")) {
						if (WorldEdit.posA.getZ() > WorldEdit.posB.getZ()) {
							WorldEdit.posA = WorldEdit.posA.south(amount);
						} else {
							WorldEdit.posB = WorldEdit.posB.south(amount);
						}
					} else {
						WorldEdit.posA = WorldEdit.posA.south(amount);
						WorldEdit.posB = WorldEdit.posB.south(amount);
					}
				} else if (args[1].toLowerCase().matches("west") || args[1].toLowerCase().matches("w")) {
					if (args[0].toLowerCase().matches("expand")) {
						if (WorldEdit.posA.getX() < WorldEdit.posB.getX()) {
							WorldEdit.posA = WorldEdit.posA.west(amount);
						} else {
							WorldEdit.posB = WorldEdit.posB.west(amount);
						}
					} else {
						WorldEdit.posA = WorldEdit.posA.west(amount);
						WorldEdit.posB = WorldEdit.posB.west(amount);
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
