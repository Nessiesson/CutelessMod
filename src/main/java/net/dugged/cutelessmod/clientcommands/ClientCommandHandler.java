package net.dugged.cutelessmod.clientcommands;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import java.util.List;
import java.util.stream.IntStream;

import static net.minecraft.util.text.TextFormatting.GRAY;
import static net.minecraft.util.text.TextFormatting.RESET;

public class ClientCommandHandler extends CommandHandler {

	private static final ClientCommandHandler instance = new ClientCommandHandler();
	private final Minecraft mc = Minecraft.getMinecraft();
	public PlayerPos lastPlayerPos = new PlayerPos(null, 0);
	public String[] latestAutoComplete = null;

	public static ClientCommandHandler getInstance() {
		return instance;
	}

	public void init() {
		instance.registerCommand(new CommandPing());
		instance.registerCommand(new CommandRepeatLast());
		instance.registerCommand(new CommandStone());
		instance.registerCommand(new CommandBack());
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