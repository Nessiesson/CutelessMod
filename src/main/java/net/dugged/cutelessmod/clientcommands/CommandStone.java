package net.dugged.cutelessmod.clientcommands;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandStone extends CommandBase {
	@Override
	public String getName() {
		return "stone";
	}

	@Override
	public String getUsage(final ICommandSender sender) {
		return getName();
	}

	@Override
	public void execute(final MinecraftServer server, final ICommandSender sender, final String[] args) {
		if (sender instanceof EntityPlayerSP) {
			((EntityPlayerSP) sender).sendChatMessage("/setblock ~ ~-1 ~ stone");
		}
	}
}
