package net.dugged.cutelessmod.clientcommands.worldedit;

import net.dugged.cutelessmod.clientcommands.ClientCommandHandler;
import net.dugged.cutelessmod.clientcommands.HandlerClone;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

public class CommandStack extends CommandBase {
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
		if (WorldEdit.hasSelection()) {
			if (args.length >= 1 && args.length <= 3) {
				HandlerClone handler = (HandlerClone) ClientCommandHandler.instance.createHandler(HandlerClone.class, sender.getEntityWorld());
				handler.isWorldEditHandler = true;
				handler.moveBlocks = false;
				handler.moveSelectionAfterwards = false;
				int count = parseInt(args[0]);
				boolean moveSelection = false;
				int blocksOffset = 0;
				if (args.length >= 2) {
					blocksOffset = parseInt(args[1]);
				}
				if (args.length == 3) {
					moveSelection = parseBoolean(args[2]);
				}
				EnumFacing facing = WorldEdit.getLookingDirection();
				BlockPos minPos = WorldEdit.minPos();
				BlockPos maxPos = WorldEdit.maxPos();
				BlockPos endPos;
				if (facing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE) {
					endPos = maxPos;
				} else {
					endPos = minPos;
				}
				for (int i = 1; i <= count; i++) {
					switch (facing.getAxis()) {
						case X:
							handler.clone(minPos, maxPos, WorldEdit.offsetLookingDirection(minPos, i * (blocksOffset + WorldEdit.widthX())));
							endPos = WorldEdit.offsetLookingDirection(endPos, blocksOffset + WorldEdit.widthX());
							break;
						case Y:
							handler.clone(minPos, maxPos, WorldEdit.offsetLookingDirection(minPos, i * (blocksOffset + WorldEdit.widthY())));
							endPos = WorldEdit.offsetLookingDirection(endPos, blocksOffset + WorldEdit.widthY());
							break;
						case Z:
							handler.clone(minPos, maxPos, WorldEdit.offsetLookingDirection(minPos, i * (blocksOffset + WorldEdit.widthZ())));
							endPos = WorldEdit.offsetLookingDirection(endPos, blocksOffset + WorldEdit.widthZ());
							break;
					}
				}
				if (moveSelection) {
					if (facing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE) {
						WorldEdit.posA = minPos;
					} else {
						WorldEdit.posA = maxPos;
					}
					WorldEdit.posB = endPos;
					WorldEdit.posA = new BlockPos(WorldEdit.posA.getX(), Math.max(Math.min(WorldEdit.posA.getY(), 255), 0), WorldEdit.posA.getZ());
					WorldEdit.posB = new BlockPos(WorldEdit.posB.getX(), Math.max(Math.min(WorldEdit.posB.getY(), 255), 0), WorldEdit.posB.getZ());
				}
			} else {
				WorldEdit.sendMessage(getUsage(sender));
			}
		} else {
			WorldEdit.sendMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.noAreaSelected"));
		}
	}
}
