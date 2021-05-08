package net.dugged.cutelessmod.clientcommands;

import net.dugged.cutelessmod.CutelessMod;
import net.dugged.cutelessmod.clientcommands.worldedit.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

import static net.minecraft.util.text.TextFormatting.GRAY;
import static net.minecraft.util.text.TextFormatting.RESET;

public class ClientCommandHandler extends CommandHandler {
	public static final ClientCommandHandler instance = new ClientCommandHandler();
	public static int PACKET_LIMIT = 5000;
	private final Minecraft mc = Minecraft.getMinecraft();
	public CopyOnWriteArrayList<Handler> handlers = new CopyOnWriteArrayList<>();
	public Position lastPosition = new Position(null, 0);
	public String[] latestAutoComplete = null;
	private long tick = 0;

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
		if (command instanceof ClientCommand && ((ClientCommand) command).creativeOnly && !(mc.player.isCreative() || mc.player.isSpectator())) {
			final TextComponentTranslation error = new TextComponentTranslation("text.cutelessmod.clientcommands.wrongGamemode");
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
		return mc.getIntegratedServer();
	}

	synchronized public Handler createHandler(final Class<? extends Handler> type, World worldIn) {
		try {
			Class[] constructors = {World.class};
			final Handler handler = type.getDeclaredConstructor(constructors).newInstance(worldIn);
			instance.handlers.add(handler);
			return handler;
		} catch (Exception e) {
			return new Handler(worldIn);
		}
	}

	public int countHandlerType(final Class<? extends Handler> type) {
		return (int) handlers.stream().filter(type::isInstance).count();
	}

	public float getProgress() {
		float progress = 0;
		if (handlers.size() > 0) {
			for (Handler handler : handlers) {
				if (handler.isWorldEditHandler && handler.running) {
					progress += handler.getProgress();
				}
			}
			float percent = progress / handlers.stream().filter(hand -> hand.isWorldEditHandler && hand.running).count();
			if (percent > 0) {
				return percent;
			} else {
				return -1;
			}
		} else return 0;
	}

	public boolean otherHandlersRunning(Handler excluding) {
		return handlers.stream().filter(hand -> hand != excluding && hand.running).count() > 0;
	}

	public void tick() {
		if (handlers.size() > 0 && Arrays.stream(CutelessMod.receivedPackets).sum() <= PACKET_LIMIT && Arrays.stream(CutelessMod.sendPackets).sum() <= PACKET_LIMIT) {
			if (mc.player == null) {
				handlers.clear();
			}
			handlers.removeIf(handler -> handler.finished);
			for (Handler handler : handlers) {
				if (handler.running) {
					handler.tick();
				}
			}
		}
		if (tick % 36000 == 0 && !mc.ingameGUI.getChatGUI().getChatOpen()) {
			HandlerSetBlock.getGameruleStates();
			HandlerFill.getGameruleStates();
			HandlerClone.getGameruleStates();
		}
		tick++;
	}

	public class Position {
		public BlockPos position;
		public int dimension;

		Position(BlockPos pos, int dim) {
			update(pos, dim);
		}

		public void update(BlockPos pos, int dim) {
			dimension = dim;
			position = pos;
		}
	}
}