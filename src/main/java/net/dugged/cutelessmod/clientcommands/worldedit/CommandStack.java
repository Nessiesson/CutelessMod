package net.dugged.cutelessmod.clientcommands.worldedit;

import static net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection.Position.A;
import static net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection.Position.B;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.dugged.cutelessmod.clientcommands.ClientCommand;
import net.dugged.cutelessmod.clientcommands.TaskClone;
import net.dugged.cutelessmod.clientcommands.TaskManager;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

public class CommandStack extends ClientCommand {

	@Override
	public String getName() {
		return "stack";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation(
			"text.cutelessmod.clientcommands.worldEdit.stack.usage").getUnformattedText();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
		throws CommandException {
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
				TaskClone.Mode mode = masked ? TaskClone.Mode.MASKED : TaskClone.Mode.FORCE;
				final EnumFacing facing = WorldEdit.getLookingDirection();
				BlockPos minPos = selection.minPos();
				BlockPos maxPos = selection.maxPos();
				BlockPos lastCloneOrigin = null;

				for (int i = 1; i <= count; i++) {
					BlockPos dest;
					switch (facing.getAxis()) {
						case Y:
							dest = WorldEdit.offsetLookingDirection(minPos,
								i * (blocksOffset + selection.widthY()));
							break;
						case Z:
							dest = WorldEdit.offsetLookingDirection(minPos,
								i * (blocksOffset + selection.widthZ()));
							break;
						default:
							dest = WorldEdit.offsetLookingDirection(minPos,
								i * (blocksOffset + selection.widthX()));
							break;
					}
					lastCloneOrigin = dest;
					TaskClone taskClone = new TaskClone(minPos, maxPos, dest,
						sender.getEntityWorld(), mode);
					TaskManager.getInstance().addTask(taskClone);
				}

				if (moveSelection && lastCloneOrigin != null) {
					BlockPos newPosA =
						(facing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE) ? minPos
							: maxPos;
					BlockPos newPosB = lastCloneOrigin.add(
						maxPos.getX() - minPos.getX(),
						maxPos.getY() - minPos.getY(),
						maxPos.getZ() - minPos.getZ());
					selection.setPos(A, newPosA);
					selection.setPos(B, newPosB);
					selection.setPos(A, new BlockPos(
						selection.getPos(A).getX(),
						Math.max(Math.min(selection.getPos(A).getY(), 255), 0),
						selection.getPos(A).getZ()));
					selection.setPos(B, new BlockPos(
						selection.getPos(B).getX(),
						Math.max(Math.min(selection.getPos(B).getY(), 255), 0),
						selection.getPos(B).getZ()));
				}
			} else {
				WorldEdit.sendMessage(getUsage(sender));
			}
		} else {
			WorldEdit.sendMessage(new TextComponentTranslation(
				"text.cutelessmod.clientcommands.worldEdit.noAreaSelected"));
		}
	}


	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender,
		String[] args, @Nullable BlockPos pos) {
		if (args.length == 3 || args.length == 4) {
			return getListOfStringsMatchingLastWord(args, "true", "false");
		} else {
			return Collections.emptyList();
		}
	}
}
