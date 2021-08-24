package net.dugged.cutelessmod.clientcommands;

import net.dugged.cutelessmod.clientcommands.worldedit.WorldEdit;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CommandUndo extends ClientCommand {

	private static final Minecraft mc = Minecraft.getMinecraft();
	public static List<Map<BlockPos, IBlockState>> undoHistory = new ArrayList<>();

	private static BlockPos parseBlockPos(String[] args, int startIndex) throws NumberInvalidException {
		final BlockPos blockPos = mc.player.getPosition();
		return new BlockPos(parseDouble(blockPos.getX(), args[startIndex], -30000000, 30000000, false), parseDouble(blockPos.getY(), args[startIndex + 1], 0, 256, false), parseDouble(blockPos.getZ(), args[startIndex + 2], -30000000, 30000000, false));
	}

	public static boolean saveHistory(String msg) {
		if (!WorldEdit.undo) {
			return false;
		}
		final World world = mc.player.world;
		if (world != null) {
			final String[] temp = msg.split(" ");
			final String[] args = new String[temp.length - 1];
			System.arraycopy(temp, 1, args, 0, args.length);
			try {
				if (msg.startsWith("/fill") && args.length >= 7) {
					HandlerUndo undoHandler = (HandlerUndo) ClientCommandHandler.instance.createHandler(HandlerUndo.class, world, null);
					undoHandler.command = msg;
					undoHandler.isWorldEditHandler = false;
					undoHandler.message = true;
					undoHandler.saveBox(parseBlockPos(args, 0), parseBlockPos(args, 3));
					return true;
				} else if (msg.startsWith("/clone") && args.length >= 9) {
					final BlockPos pos1 = parseBlockPos(args, 0);
					final BlockPos pos2 = parseBlockPos(args, 3);
					final BlockPos pos3 = parseBlockPos(args, 6);
					final BlockPos pos4 = pos3.add(Math.max(pos1.getX(), pos2.getX()) - Math.min(pos1.getX(), pos2.getX()), Math.max(pos1.getY(), pos2.getY()) - Math.min(pos1.getY(), pos2.getY()), Math.max(pos1.getZ(), pos2.getZ()) - Math.min(pos1.getZ(), pos2.getZ()));
					HandlerUndo undoHandler = (HandlerUndo) ClientCommandHandler.instance.createHandler(HandlerUndo.class, world, null);
					undoHandler.command = msg;
					undoHandler.isWorldEditHandler = false;
					undoHandler.message = true;
					undoHandler.saveBox(parseBlockPos(args, 0), parseBlockPos(args, 3));
					undoHandler.saveBox(pos1, pos2);
					undoHandler.saveBox(pos3, pos4);
					return true;
				} else {
					return false;
				}
			} catch (CommandException e) {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public String getName() {
		return "undo";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return getName();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (sender instanceof EntityPlayerSP) {
			if (mc.player.isCreative() || mc.player.isSpectator()) {
				if (args.length == 1 && args[0].equalsIgnoreCase("toggle")) {
					WorldEdit.undo = !WorldEdit.undo;
					if (WorldEdit.undo) {
						WorldEdit.sendMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.undo.enabledUndo"));
					} else {
						WorldEdit.sendMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.undo.disabledUndo"));
					}
				} else {
					if (undoHistory.size() > 0) {
						int historyIndex = 0;
						if (args.length >= 1) {
							historyIndex = parseInt(args[0], 0, 100);
						}
						if (undoHistory.size() - 1 >= historyIndex) {
							World world = sender.getEntityWorld();
							HandlerSetBlock setBlockHandler = (HandlerSetBlock) ClientCommandHandler.instance.createHandler(HandlerSetBlock.class, world, null);
							setBlockHandler.setBlocks(undoHistory.get(historyIndex));
						} else {
							throw new CommandException("text.cutelessmod.clientcommands.undo.invalidIndex");
						}
					} else {
						throw new CommandException("text.cutelessmod.clientcommands.undo.noHistoryAvaliable");
					}
				}
			} else {
				throw new CommandException("text.cutelessmod.clientcommands.wrongGamemode");
			}
		}
	}

	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
		if (args.length == 1) {
			return getListOfStringsMatchingLastWord(args, "toggle");
		} else {
			return Collections.emptyList();
		}
	}
}