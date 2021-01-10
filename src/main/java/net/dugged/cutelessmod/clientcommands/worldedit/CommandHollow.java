package net.dugged.cutelessmod.clientcommands.worldedit;

import net.dugged.cutelessmod.clientcommands.ClientCommand;
import net.dugged.cutelessmod.clientcommands.ClientCommandHandler;
import net.dugged.cutelessmod.clientcommands.HandlerFill;
import net.dugged.cutelessmod.clientcommands.HandlerUndo;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandHollow extends ClientCommand {
	@Override
	public String getName() {
		return "hollow";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.hollow.usage").getUnformattedText();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (WorldEdit.hasSelection()) {
			if (args.length > 0 && args.length <= 3) {
				HandlerFill fillHandler = (HandlerFill) ClientCommandHandler.instance.createHandler(HandlerFill.class, sender.getEntityWorld());
				HandlerUndo undoHandler = (HandlerUndo) ClientCommandHandler.instance.createHandler(HandlerUndo.class, sender.getEntityWorld());
				undoHandler.setHandler(fillHandler);
				Block block = getBlockByText(sender, args[0]);
				IBlockState blockstate = block.getDefaultState();
				int thickness = 0;
				if (args.length >= 2) {
					blockstate = convertArgToBlockState(block, args[1]);
				}
				if (args.length >= 3) {
					thickness = parseInt(args[2]) - 1;
					if (thickness <= 0) {
						thickness = 0;
					}
				}
				BlockPos posMin = new BlockPos(Math.min(WorldEdit.posA.getX(), WorldEdit.posB.getX()), Math.min(WorldEdit.posA.getY(), WorldEdit.posB.getY()), Math.min(WorldEdit.posA.getZ(), WorldEdit.posB.getZ()));
				BlockPos posMax = new BlockPos(Math.max(WorldEdit.posA.getX(), WorldEdit.posB.getX()), Math.max(WorldEdit.posA.getY(), WorldEdit.posB.getY()), Math.max(WorldEdit.posA.getZ(), WorldEdit.posB.getZ()));
				undoHandler.saveBox(posMin, new BlockPos(posMax.getX(), posMax.getY(), posMin.getZ() + Math.min(thickness, WorldEdit.widthZ() - 1)));
				fillHandler.fill(posMin, new BlockPos(posMax.getX(), posMax.getY(), posMin.getZ() + Math.min(thickness, WorldEdit.widthZ() - 1)), blockstate);
				undoHandler.saveBox(posMin, new BlockPos(posMin.getX() + Math.min(thickness, WorldEdit.widthX() - 1), posMax.getY(), posMax.getZ()));
				fillHandler.fill(posMin, new BlockPos(posMin.getX() + Math.min(thickness, WorldEdit.widthX() - 1), posMax.getY(), posMax.getZ()), blockstate);
				undoHandler.saveBox(posMin, new BlockPos(posMax.getX(), posMin.getY() + Math.min(thickness, WorldEdit.widthY() - 1), posMax.getZ()));
				fillHandler.fill(posMin, new BlockPos(posMax.getX(), posMin.getY() + Math.min(thickness, WorldEdit.widthY() - 1), posMax.getZ()), blockstate);
				undoHandler.saveBox(posMax, new BlockPos(posMin.getX(), posMin.getY(), posMax.getZ() - Math.min(thickness, WorldEdit.widthZ() - 1)));
				fillHandler.fill(posMax, new BlockPos(posMin.getX(), posMin.getY(), posMax.getZ() - Math.min(thickness, WorldEdit.widthZ() - 1)), blockstate);
				undoHandler.saveBox(posMax, new BlockPos(posMax.getX() - Math.min(thickness, WorldEdit.widthX() - 1), posMin.getY(), posMin.getZ()));
				fillHandler.fill(posMax, new BlockPos(posMax.getX() - Math.min(thickness, WorldEdit.widthX() - 1), posMin.getY(), posMin.getZ()), blockstate);
				undoHandler.saveBox(posMax, new BlockPos(posMin.getX(), posMax.getY() - Math.min(thickness, WorldEdit.widthY() - 1), posMin.getZ()));
				fillHandler.fill(posMax, new BlockPos(posMin.getX(), posMax.getY() - Math.min(thickness, WorldEdit.widthY() - 1), posMin.getZ()), blockstate);
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
