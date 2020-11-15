package net.dugged.cutelessmod.clientcommands.worldedit;

import net.dugged.cutelessmod.clientcommands.ClientCommandHandler;
import net.dugged.cutelessmod.clientcommands.HandlerFill;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;

public class CommandSet extends CommandBase {
	@Override
	public String getName() {
		return "set";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.set.usage").getUnformattedText();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (WorldEdit.hasSelection()) {
			if (args.length > 0 && args.length <= 2) {
				HandlerFill handler = (HandlerFill) ClientCommandHandler.instance.createHandler(HandlerFill.class, sender.getEntityWorld());
				Block block = CommandBase.getBlockByText(sender, args[0]);
				IBlockState blockstate = block.getDefaultState();
				if (args.length == 2) {
					blockstate = convertArgToBlockState(block, args[1]);
				}
				handler.fill(WorldEdit.posA, WorldEdit.posB, blockstate);
			} else {
				WorldEdit.sendMessage(getUsage(sender));
			}
		} else {
			WorldEdit.sendMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.noAreaSelected"));
		}
	}
}
