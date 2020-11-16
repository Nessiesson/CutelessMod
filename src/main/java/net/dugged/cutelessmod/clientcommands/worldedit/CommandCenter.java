package net.dugged.cutelessmod.clientcommands.worldedit;

import net.dugged.cutelessmod.clientcommands.ClientCommandHandler;
import net.dugged.cutelessmod.clientcommands.HandlerSetBlock;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandCenter extends CommandBase {
	@Override
	public String getName() {
		return "center";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.center.usage").getUnformattedText();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (WorldEdit.hasSelection()) {
			if (args.length >= 0 && args.length <= 2) {
				HandlerSetBlock handler = (HandlerSetBlock) ClientCommandHandler.instance.createHandler(HandlerSetBlock.class, sender.getEntityWorld());
				handler.isWorldEditHandler = true;
				Block block = Blocks.GLOWSTONE;
				if (args.length > 0) {
					block = CommandBase.getBlockByText(sender, args[0]);
				}
				IBlockState blockstate = block.getDefaultState();
				if (args.length >= 2) {
					blockstate = convertArgToBlockState(block, args[1]);
				}
				BlockPos center = new BlockPos(WorldEdit.getMinPos().getX() + WorldEdit.widthX() / 2, WorldEdit.getMinPos().getY() + WorldEdit.widthY() / 2, WorldEdit.getMinPos().getZ() + WorldEdit.widthZ() / 2);
				handler.setBlock(center, blockstate);
				boolean x = WorldEdit.widthX() % 2 == 0;
				boolean y = WorldEdit.widthY() % 2 == 0;
				boolean z = WorldEdit.widthZ() % 2 == 0;
				if (x) {
					handler.setBlock(center.west(), blockstate);
				}
				if (y) {
					handler.setBlock(center.down(), blockstate);
				}
				if (z) {
					handler.setBlock(center.north(), blockstate);
				}
				if (x && y) {
					handler.setBlock(center.west().down(), blockstate);
				}
				if (y && z) {
					handler.setBlock(center.down().north(), blockstate);
				}
				if (x && z) {
					handler.setBlock(center.west().north(), blockstate);
				}
				if (x && y && z) {
					handler.setBlock(center.west().down().north(), blockstate);
				}

			} else {
				WorldEdit.sendMessage(getUsage(sender));
			}
		} else {
			WorldEdit.sendMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.noAreaSelected"));
		}
	}

	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
		if (args.length == 1) {
			return getListOfStringsMatchingLastWord(args, Block.REGISTRY.getKeys());
		} else {
			return Collections.emptyList();
		}
	}
}
