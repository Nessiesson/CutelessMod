package net.dugged.cutelessmod.clientcommands;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static net.minecraft.util.text.TextFormatting.GRAY;
import static net.minecraft.util.text.TextFormatting.RESET;

public class ClientCommandHandler extends CommandHandler {
	public static final ClientCommandHandler instance = new ClientCommandHandler();
	public List<Handler> handlers = new ArrayList<>();

	public String[] latestAutoComplete = null;
	private long tick = 0;

	@Override
	public int executeCommand(ICommandSender sender, String message) {
		message = message.trim();

		if (message.startsWith("/")) {
			message = message.substring(1);
		}

		final String[] temp = message.split(" ");
		final String[] args = new String[temp.length - 1];
		final String commandName = temp[0];
		System.arraycopy(temp, 1, args, 0, args.length);
		final ICommand command = getCommands().get(commandName);
		if (command == null) {
			return 0;
		}

		try {
			this.tryExecute(sender, args, command, message);
		} catch (Throwable t) {
			final TextComponentTranslation error = new TextComponentTranslation("commands.generic.exception");
			error.getStyle().setColor(TextFormatting.RED);
			sender.sendMessage(error);
		}
		return -1;
	}

	public void autoComplete(String leftOfCursor) {
		this.latestAutoComplete = null;

		if (leftOfCursor.charAt(0) == '/') {
			leftOfCursor = leftOfCursor.substring(1);

			final Minecraft mc = Minecraft.getMinecraft();
			if (mc.currentScreen instanceof GuiChat) {
				final List<String> commands = this.getTabCompletions(mc.player, leftOfCursor, mc.player.getPosition());
				if (!commands.isEmpty()) {
					if (leftOfCursor.indexOf(' ') == -1) {
						IntStream.range(0, commands.size()).forEach(s -> commands.set(s, GRAY + "/" + commands.get(s) + RESET));
					} else {
						IntStream.range(0, commands.size()).forEach(s -> commands.set(s, GRAY + commands.get(s) + RESET));
					}

					this.latestAutoComplete = commands.toArray(new String[0]);
				}
			}
		}
	}

	@Override
	protected MinecraftServer getServer() {
		return Minecraft.getMinecraft().getIntegratedServer();
	}

	public Handler createHandler(final Class<? extends Handler> type) {
		try {
			final Handler handler = type.newInstance();
			instance.handlers.add(handler);
			return handler;
		} catch (Exception e) {
			return new Handler();
		}
	}

	public int countHandlerType(final Class<? extends Handler> type) {
		return (int) handlers.stream().filter(type::isInstance).count();
	}

	@SubscribeEvent
	public void onLoadWorld(final WorldEvent.Load event) {
		HandlerSetBlock.getGameruleStates();
	}

	public void tick() {
		if (handlers.size() > 0) {
			if (Minecraft.getMinecraft().player == null) {
				handlers.clear();
			}
			handlers.removeIf(handler -> handler.finished);
			for (Handler handler : handlers) {
				handler.tick();
			}
		}
		if (tick % 600 == 0) {
			HandlerSetBlock.getGameruleStates();
		}
		tick++;
	}
}