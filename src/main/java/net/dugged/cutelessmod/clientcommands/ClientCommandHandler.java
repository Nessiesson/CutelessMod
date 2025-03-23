package net.dugged.cutelessmod.clientcommands;

import static net.minecraft.util.text.TextFormatting.GRAY;
import static net.minecraft.util.text.TextFormatting.RESET;

import java.util.List;
import java.util.stream.IntStream;
import net.dugged.cutelessmod.clientcommands.worldedit.BrushIceSpike;
import net.dugged.cutelessmod.clientcommands.worldedit.BrushPerimeterWall;
import net.dugged.cutelessmod.clientcommands.worldedit.BrushPlaceTop;
import net.dugged.cutelessmod.clientcommands.worldedit.BrushRemoveColumn;
import net.dugged.cutelessmod.clientcommands.worldedit.CommandBrush;
import net.dugged.cutelessmod.clientcommands.worldedit.CommandCancel;
import net.dugged.cutelessmod.clientcommands.worldedit.CommandCenter;
import net.dugged.cutelessmod.clientcommands.worldedit.CommandCone;
import net.dugged.cutelessmod.clientcommands.worldedit.CommandCopyHere;
import net.dugged.cutelessmod.clientcommands.worldedit.CommandCount;
import net.dugged.cutelessmod.clientcommands.worldedit.CommandCyl;
import net.dugged.cutelessmod.clientcommands.worldedit.CommandDiagLine;
import net.dugged.cutelessmod.clientcommands.worldedit.CommandDrain;
import net.dugged.cutelessmod.clientcommands.worldedit.CommandErode;
import net.dugged.cutelessmod.clientcommands.worldedit.CommandFillInventories;
import net.dugged.cutelessmod.clientcommands.worldedit.CommandFixSlabs;
import net.dugged.cutelessmod.clientcommands.worldedit.CommandFlip;
import net.dugged.cutelessmod.clientcommands.worldedit.CommandFloodFill;
import net.dugged.cutelessmod.clientcommands.worldedit.CommandHCyl;
import net.dugged.cutelessmod.clientcommands.worldedit.CommandHSphere;
import net.dugged.cutelessmod.clientcommands.worldedit.CommandHollow;
import net.dugged.cutelessmod.clientcommands.worldedit.CommandLine;
import net.dugged.cutelessmod.clientcommands.worldedit.CommandMove;
import net.dugged.cutelessmod.clientcommands.worldedit.CommandOutlineFill;
import net.dugged.cutelessmod.clientcommands.worldedit.CommandPerimeterVolume;
import net.dugged.cutelessmod.clientcommands.worldedit.CommandPolygon;
import net.dugged.cutelessmod.clientcommands.worldedit.CommandPos;
import net.dugged.cutelessmod.clientcommands.worldedit.CommandRandomize;
import net.dugged.cutelessmod.clientcommands.worldedit.CommandReplace;
import net.dugged.cutelessmod.clientcommands.worldedit.CommandRunBrush;
import net.dugged.cutelessmod.clientcommands.worldedit.CommandSelection;
import net.dugged.cutelessmod.clientcommands.worldedit.CommandSet;
import net.dugged.cutelessmod.clientcommands.worldedit.CommandSize;
import net.dugged.cutelessmod.clientcommands.worldedit.CommandSphere;
import net.dugged.cutelessmod.clientcommands.worldedit.CommandStack;
import net.dugged.cutelessmod.clientcommands.worldedit.CommandStackDiagonal;
import net.dugged.cutelessmod.clientcommands.worldedit.CommandStackQuarter;
import net.dugged.cutelessmod.clientcommands.worldedit.CommandSwap;
import net.dugged.cutelessmod.clientcommands.worldedit.CommandUndo;
import net.dugged.cutelessmod.clientcommands.worldedit.CommandUpscale;
import net.dugged.cutelessmod.clientcommands.worldedit.CommandWalls;
import net.dugged.cutelessmod.clientcommands.worldedit.CommandWoolify;
import net.dugged.cutelessmod.clientcommands.worldedit.WorldEdit;
import net.dugged.cutelessmod.clientcommands.worldedit.WorldEditRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class ClientCommandHandler extends CommandHandler {

	private static final ClientCommandHandler instance = new ClientCommandHandler();
	public static int PACKET_LIMIT = 5000;
	private final Minecraft mc = Minecraft.getMinecraft();
	public PlayerPos lastPlayerPos = new PlayerPos(null, 0);
	public String[] latestAutoComplete = null;
	private long tick = 0;

	public static ClientCommandHandler getInstance() {
		return instance;
	}

	public static void updatePermissions() {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.player != null && mc.player.connection != null) {
			mc.player.connection.sendPacket(new CPacketTabComplete("/setbloc", null, false));
			mc.player.connection.sendPacket(new CPacketTabComplete("/fil", null, false));
			mc.player.connection.sendPacket(new CPacketTabComplete("/clon", null, false));
			mc.player.connection.sendPacket(new CPacketTabComplete("/replaceite", null, false));
			mc.player.connection.sendPacket(new CPacketTabComplete("/gamerul", null, false));
		}
	}

	public void init() {
		instance.registerCommand(new CommandPing());
		instance.registerCommand(new CommandUndo());
		instance.registerCommand(new CommandRepeatLast());
		instance.registerCommand(new CommandStone());
		instance.registerCommand(new CommandSet());
		instance.registerCommand(new CommandWalls());
		instance.registerCommand(new CommandHollow());
		instance.registerCommand(new CommandCenter());
		instance.registerCommand(new CommandPos());
		instance.registerCommand(new CommandSize());
		instance.registerCommand(new CommandCyl());
		instance.registerCommand(new CommandHCyl());
		instance.registerCommand(new CommandHSphere());
		instance.registerCommand(new CommandSphere());
		instance.registerCommand(new CommandMove());
		instance.registerCommand(new CommandFlip());
		instance.registerCommand(new CommandStack());
		instance.registerCommand(new CommandLine());
		instance.registerCommand(new CommandPolygon());
		instance.registerCommand(new CommandSelection());
		instance.registerCommand(new CommandDrain());
		instance.registerCommand(new CommandFloodFill());
		instance.registerCommand(new CommandOutlineFill());
		instance.registerCommand(new CommandUpscale());
		instance.registerCommand(new CommandCount());
		instance.registerCommand(new CommandFixSlabs());
		instance.registerCommand(new CommandRandomize());
		instance.registerCommand(new CommandReplace());
		instance.registerCommand(new CommandStackQuarter());
		instance.registerCommand(new CommandCancel());
		instance.registerCommand(new CommandBack());
		instance.registerCommand(new CommandCopyHere());
		instance.registerCommand(new CommandFillInventories());
		instance.registerCommand(new CommandPerimeterVolume());
		instance.registerCommand(new CommandBrush());
		instance.registerCommand(new CommandRunBrush());
		instance.registerCommand(new CommandCone());
		instance.registerCommand(new CommandWoolify());
		instance.registerCommand(new CommandErode());
		instance.registerCommand(new CommandStackDiagonal());
		instance.registerCommand(new CommandSwap());
		instance.registerCommand(new CommandDiagLine());

		WorldEdit.brushes.add(new BrushIceSpike());
		WorldEdit.brushes.add(new BrushRemoveColumn());
		WorldEdit.brushes.add(new BrushPerimeterWall());
		WorldEdit.brushes.add(new BrushPlaceTop());
	}

	@Override
	public int executeCommand(ICommandSender sender, String message) {
		message = message.trim();

		if (message.startsWith("/")) {
			message = message.substring(1);
		} else {
			return 0;
		}

		final String[] temp = message.split(" ");
		final String[] args = new String[temp.length - 1];
		final String commandName = temp[0];
		System.arraycopy(temp, 1, args, 0, args.length);
		final ICommand command = getCommands().get(commandName);
		if (command instanceof ClientCommand && ((ClientCommand) command).creativeOnly && !(
			mc.player.isCreative() || mc.player.isSpectator())) {
			final TextComponentTranslation error = new TextComponentTranslation(
				"text.cutelessmod.clientcommands.wrongGamemode");
			error.getStyle().setColor(TextFormatting.RED);
			sender.sendMessage(error);
			return -1;
		}
		if (command == null) {
			return 0;
		}

		try {
			this.tryExecute(sender, args, command, message);
		} catch (Throwable t) {
			final TextComponentTranslation error = new TextComponentTranslation(
				"commands.generic.exception");
			error.getStyle().setColor(TextFormatting.RED);
			sender.sendMessage(error);
		}
		return -1;
	}

	public void autoComplete(String leftOfCursor) {
		this.latestAutoComplete = null;
		if (leftOfCursor.charAt(0) == '/') {
			leftOfCursor = leftOfCursor.substring(1);
			if (mc.currentScreen instanceof GuiChat) {
				final List<String> commands = this.getTabCompletions(mc.player, leftOfCursor,
					mc.player.getPosition());
				if (!commands.isEmpty()) {
					if (leftOfCursor.indexOf(' ') == -1) {
						IntStream.range(0, commands.size())
							.forEach(s -> commands.set(s, GRAY + "/" + commands.get(s) + RESET));
					} else {
						IntStream.range(0, commands.size())
							.forEach(s -> commands.set(s, GRAY + commands.get(s) + RESET));
					}
					this.latestAutoComplete = commands.toArray(new String[0]);
				}
			}
		}
	}

	@Override
	protected MinecraftServer getServer() {
		return mc.getIntegratedServer();
	}

	public void tick() {
		if (mc.world == null) {
			return;
		}
		if (tick % 36000 == 0 && !mc.ingameGUI.getChatGUI().getChatOpen()) {
			updatePermissions();
		}
		TaskManager.getInstance().tick();
		WorldEditRenderer.update();
		tick++;
	}

	public static class PlayerPos {

		public BlockPos position;
		public int dimension;

		PlayerPos(BlockPos pos, int dim) {
			update(pos, dim);
		}

		public void update(BlockPos pos, int dim) {
			dimension = dim;
			position = pos;
		}
	}
}