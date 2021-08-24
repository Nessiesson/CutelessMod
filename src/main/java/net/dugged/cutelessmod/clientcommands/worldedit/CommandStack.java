package net.dugged.cutelessmod.clientcommands.worldedit;

import net.dugged.cutelessmod.clientcommands.ClientCommand;
import net.dugged.cutelessmod.clientcommands.ClientCommandHandler;
import net.dugged.cutelessmod.clientcommands.HandlerClone;
import net.dugged.cutelessmod.clientcommands.HandlerUndo;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import static net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection.Position.A;
import static net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection.Position.B;

public class CommandStack extends ClientCommand {
	@Override
	public String getName() {
		return "stack";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.stack.usage").getUnformattedText();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (WorldEdit.hasCurrentSelection()) {
			WorldEditSelection selection = WorldEdit.getCurrentSelection();
			if (args.length >= 1 && args.length <= 4) {
				int count = parseInt(args[0]);
				boolean masked = false;
				boolean moveSelection = false;
				int blocksOffset = 0;
				if (args.length >= 2) {
					blocksOffset = parseInt(args[1]);
				}
				if (args.length >= 3) {
					masked = parseBoolean(args[2]);
				}
				if (args.length == 4) {
					moveSelection = parseBoolean(args[3]);
				}
				HandlerClone cloneHandler = (HandlerClone) ClientCommandHandler.instance.createHandler(HandlerClone.class, sender.getEntityWorld(), selection);
				cloneHandler.masked = masked;
				cloneHandler.moveSelectionAfterwards = false;
				HandlerUndo undoHandler = (HandlerUndo) ClientCommandHandler.instance.createHandler(HandlerUndo.class, sender.getEntityWorld(), selection);
				undoHandler.setHandler(cloneHandler);
				final EnumFacing facing = WorldEdit.getLookingDirection();
				BlockPos minPos = selection.minPos();
				BlockPos maxPos = selection.maxPos();
				BlockPos endPos;
				if (facing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE) {
					endPos = maxPos;
				} else {
					endPos = minPos;
				}
				for (int i = 1; i <= count; i++) {
					undoHandler.saveBox(minPos, maxPos);
					BlockPos pos1;
					switch (facing.getAxis()) {
						case X:
							pos1 = WorldEdit.offsetLookingDirection(minPos, i * (blocksOffset + selection.widthX()));
							undoHandler.saveBox(pos1, pos1.add(selection.widthX(), selection.widthY(), selection.widthZ()));
							cloneHandler.clone(minPos, maxPos, pos1);
							endPos = WorldEdit.offsetLookingDirection(endPos, blocksOffset + selection.widthX());
							break;
						case Y:
							pos1 = WorldEdit.offsetLookingDirection(minPos, i * (blocksOffset + selection.widthY()));
							undoHandler.saveBox(pos1, pos1.add(selection.widthX(), selection.widthY(), selection.widthZ()));
							cloneHandler.clone(minPos, maxPos, pos1);
							endPos = WorldEdit.offsetLookingDirection(endPos, blocksOffset + selection.widthY());
							break;
						case Z:
							pos1 = WorldEdit.offsetLookingDirection(minPos, i * (blocksOffset + selection.widthZ()));
							undoHandler.saveBox(pos1, pos1.add(selection.widthX(), selection.widthY(), selection.widthZ()));
							cloneHandler.clone(minPos, maxPos, pos1);
							endPos = WorldEdit.offsetLookingDirection(endPos, blocksOffset + selection.widthZ());
							break;
					}
				}
				if (moveSelection) {
					if (facing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE) {
						selection.setPos(A, minPos);
					} else {
						selection.setPos(A, maxPos);
					}
					selection.setPos(B, endPos);
					selection.setPos(A, new BlockPos(selection.getPos(A).getX(), Math.max(Math.min(selection.getPos(A).getY(), 255), 0), selection.getPos(A).getZ()));
					selection.setPos(B, new BlockPos(selection.getPos(B).getX(), Math.max(Math.min(selection.getPos(B).getY(), 255), 0), selection.getPos(B).getZ()));
				}
			} else {
				WorldEdit.sendMessage(getUsage(sender));
			}
		} else {
			WorldEdit.sendMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.noAreaSelected"));
		}
	}

	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
		if (args.length == 3 || args.length == 4) {
			return getListOfStringsMatchingLastWord(args, "true", "false");
		} else {
			return Collections.emptyList();
		}
	}
}
