package net.dugged.cutelessmod.clientcommands;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.dugged.cutelessmod.CutelessMod;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandRepeatLast extends ClientCommand {

	@Override
	public String getName() {
		return "repeatlast";
	}

	@Override
	public List<String> getAliases() {
		return ImmutableList.of(";");
	}

	@Override
	public String getUsage(final ICommandSender sender) {
		return getName();
	}

	@Override
	public void execute(final MinecraftServer server, final ICommandSender sender,
		final String[] args) {
		if (sender instanceof EntityPlayerSP) {
			((EntityPlayerSP) sender).sendChatMessage(CutelessMod.lastCommand);
		}
	}
}
