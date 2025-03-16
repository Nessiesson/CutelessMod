package net.dugged.cutelessmod.clientcommands;

import net.dugged.cutelessmod.clientcommands.ClientCommandHandler.PlayerPos;
import net.dugged.cutelessmod.clientcommands.worldedit.WorldEdit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;

public class CommandBack extends ClientCommand {

	@Override
	public String getName() {
		return "back";
	}

	@Override
	public String getUsage(final ICommandSender sender) {
		return getName();
	}

	@Override
	public void execute(final MinecraftServer server, final ICommandSender sender,
		final String[] args) throws CommandException {
		final Minecraft mc = Minecraft.getMinecraft();
		final PlayerPos pos = ClientCommandHandler.getInstance().lastPlayerPos;
		if (pos.position != null) {
			if (mc.player.dimension == pos.dimension) {
				((EntityPlayerSP) sender).sendChatMessage(
					"/tp " + pos.position.getX() + " " + pos.position.getY() + " "
						+ pos.position.getZ());
				mc.player.sendMessage(
					new TextComponentTranslation("text.cutelessmod.clientcommands.back.tp",
						pos.position.getX(), pos.position.getY(), pos.position.getZ()));
				ClientCommandHandler.getInstance().lastPlayerPos.update(WorldEdit.playerPos(),
					mc.player.dimension);
			} else {
				throw new CommandException("text.cutelessmod.clientcommands.back.wrongDimension");
			}
		} else {
			throw new CommandException("text.cutelessmod.clientcommands.back.noPosition");
		}
	}
}
