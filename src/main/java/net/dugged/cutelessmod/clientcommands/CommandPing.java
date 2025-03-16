package net.dugged.cutelessmod.clientcommands;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;

public class CommandPing extends ClientCommand {

	public CommandPing() {
		creativeOnly = false;
	}

	@Override
	public String getName() {
		return "ping";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return getName();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
		if (sender instanceof EntityPlayerSP) {
			Minecraft mc = Minecraft.getMinecraft();
			int ping = mc.player.connection.getPlayerInfo(mc.getSession().getUsername())
				.getResponseTime();
			sender.sendMessage(
				new TextComponentTranslation("text.cutelessmod.clientcommands.ping", ping));
		}
	}
}
