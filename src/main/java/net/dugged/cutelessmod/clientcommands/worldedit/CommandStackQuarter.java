package net.dugged.cutelessmod.clientcommands.worldedit;

import net.dugged.cutelessmod.clientcommands.ClientCommand;
import net.dugged.cutelessmod.clientcommands.ClientCommandHandler;
import net.dugged.cutelessmod.clientcommands.HandlerSetBlock;
import net.dugged.cutelessmod.clientcommands.HandlerUndo;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommandStackQuarter extends ClientCommand {

	@Override
	public String getName() {
		return "stackquarter";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.stackquarter.usage").getUnformattedText();
	}

	private void stackQuarter(World world, WorldEditSelection selection, final int spacing) {
		final int tolerance = 15;
		final float yaw = MathHelper.wrapDegrees(mc.getRenderViewEntity().rotationYaw);
		final float pitch = MathHelper.wrapDegrees(mc.getRenderViewEntity().rotationPitch);
		final int widthX = selection.widthX();
		final int widthY = selection.widthY();
		final int widthZ = selection.widthZ();
		Map<BlockPos, IBlockState> blockList = new HashMap<>();
		for (int x = 0; x < selection.widthX(); x++) {
			for (int y = 0; y < selection.widthY(); y++) {
				for (int z = 0; z < selection.widthZ(); z++) {
					IBlockState blockState = world.getBlockState(selection.minPos().add(x, y, z));

					// Horizontal diagonals
					// SW -X +Z
					if (yaw >= 45 - tolerance && yaw <= 45 + tolerance && pitch >= -tolerance && pitch <= tolerance) {
						blockList.put(selection.minPos().add(-x - 1 - spacing, y, z), WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.X));
						blockList.put(selection.minPos().add(x, y, widthZ - z - 1 + widthZ + spacing), WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.Z));
						blockList.put(selection.minPos().add(-x - 1 - spacing, y, widthZ - z - 1 + widthZ + spacing), WorldEdit.flipBlockstate(WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.X), EnumFacing.Axis.Z));
						// NW -X -Z
					} else if (yaw >= 135 - tolerance && yaw <= 135 + tolerance && pitch >= -tolerance && pitch <= tolerance) {
						blockList.put(selection.minPos().add(-x - 1 - spacing, y, z), WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.X));
						blockList.put(selection.minPos().add(x, y, -z - 1 - spacing), WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.Z));
						blockList.put(selection.minPos().add(-x - 1 - spacing, y, -z - 1 - spacing), WorldEdit.flipBlockstate(WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.X), EnumFacing.Axis.Z));
						// SE +X +Z
					} else if (yaw >= -45 - tolerance && yaw <= -45 + tolerance && pitch >= -tolerance && pitch <= tolerance) {
						blockList.put(selection.minPos().add(widthX - x - 1 + widthX + spacing, y, z), WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.X));
						blockList.put(selection.minPos().add(x, y, widthZ - z - 1 + widthZ + spacing), WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.Z));
						blockList.put(selection.minPos().add(widthX - x - 1 + widthX + spacing, y, widthZ - z - 1 + widthZ + spacing), WorldEdit.flipBlockstate(WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.X), EnumFacing.Axis.Z));
						// NE +X -Z
					} else if (yaw >= -135 - tolerance && yaw <= -135 + tolerance && pitch >= -tolerance && pitch <= tolerance) {
						blockList.put(selection.minPos().add(widthX - x - 1 + widthX + spacing, y, z), WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.X));
						blockList.put(selection.minPos().add(x, y, -z - 1 + spacing), WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.Z));
						blockList.put(selection.minPos().add(widthX - x - 1 + widthX + spacing, y, -z - 1 - spacing), WorldEdit.flipBlockstate(WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.X), EnumFacing.Axis.Z));

						// Vertical diagonals
					} else if (yaw >= -tolerance && yaw <= tolerance) {
						// S +Z -Y
						if (pitch >= 45 - tolerance && pitch <= 45 + tolerance) {
							blockList.put(selection.minPos().add(x, y, widthZ - z - 1 + widthZ + spacing), WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.Z));
							blockList.put(selection.minPos().add(x, -y - 1 - spacing, z), WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.Y));
							blockList.put(selection.minPos().add(x, -y - 1 - spacing, widthZ - z - 1 + widthZ + spacing), WorldEdit.flipBlockstate(WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.Z), EnumFacing.Axis.Y));
							// S +Z +Y
						} else if (pitch >= -45 - tolerance && pitch <= -45 + tolerance) {
							blockList.put(selection.minPos().add(x, y, widthZ - z - 1 + widthZ + spacing), WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.Z));
							blockList.put(selection.minPos().add(x, widthY - y - 1 + widthY + spacing, z), WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.Y));
							blockList.put(selection.minPos().add(x, widthY - y - 1 + widthY + spacing, widthZ - z - 1 + widthZ + spacing), WorldEdit.flipBlockstate(WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.Z), EnumFacing.Axis.Y));
						}
					} else if (yaw >= 90 - tolerance && yaw <= 90 + tolerance) {
						// W -X -Y
						if (pitch >= 45 - tolerance && pitch <= 45 + tolerance) {
							blockList.put(selection.minPos().add(-x - 1 - spacing, y, z), WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.X));
							blockList.put(selection.minPos().add(x, -y - 1 - spacing, z), WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.X));
							blockList.put(selection.minPos().add(-x - 1 - spacing, -y - 1 - spacing, z), WorldEdit.flipBlockstate(WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.X), EnumFacing.Axis.Y));
							// W -X +Y
						} else if (pitch >= -45 - tolerance && pitch <= -45 + tolerance) {
							blockList.put(selection.minPos().add(-x - 1 - spacing, y, z), WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.X));
							blockList.put(selection.minPos().add(x, widthY - y - 1 + widthY + spacing, z), WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.Y));
							blockList.put(selection.minPos().add(-x - 1 - spacing, widthY - y - 1 + widthY + spacing, z), WorldEdit.flipBlockstate(WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.X), EnumFacing.Axis.Y));
						}
					} else if (yaw >= -90 - tolerance && yaw <= -90 + tolerance) {
						// E +X -Y
						if (pitch >= 45 - tolerance && pitch <= 45 + tolerance) {
							blockList.put(selection.minPos().add(widthX - x - 1 + widthX + spacing, y, z), WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.X));
							blockList.put(selection.minPos().add(x, -y - 1 - spacing, z), WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.Y));
							blockList.put(selection.minPos().add(widthX - x - 1 + widthX + spacing, -y - 1 - spacing, z), WorldEdit.flipBlockstate(WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.X), EnumFacing.Axis.Y));
							// E +X +Y
						} else if (pitch >= -45 - tolerance && pitch <= -45 + tolerance) {
							blockList.put(selection.minPos().add(widthX - x - 1 + widthX + spacing, y, z), WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.X));
							blockList.put(selection.minPos().add(x, widthY - y - 1 + widthY + spacing, z), WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.Y));
							blockList.put(selection.minPos().add(widthX - x - 1 + widthX + spacing, widthY - y - 1 + widthY + spacing, z), WorldEdit.flipBlockstate(WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.X), EnumFacing.Axis.Y));
						}
					} else if (yaw >= 180 - tolerance || yaw <= -180 + tolerance) {
						// N -Z -Y
						if (pitch >= 45 - tolerance && pitch <= 45 + tolerance) {
							blockList.put(selection.minPos().add(x, y, -z - 1 - spacing), WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.Z));
							blockList.put(selection.minPos().add(x, -y - 1 - spacing, z), WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.Y));
							blockList.put(selection.minPos().add(x, -y - 1 - spacing, -z - 1 - spacing), WorldEdit.flipBlockstate(WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.Z), EnumFacing.Axis.Y));
							// N -Z +Y
						} else if (pitch >= -45 - tolerance && pitch <= -45 + tolerance) {
							blockList.put(selection.minPos().add(x, y, -z - 1 - spacing), WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.Z));
							blockList.put(selection.minPos().add(x, widthY - y - 1 + widthY + spacing, z), WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.Y));
							blockList.put(selection.minPos().add(x, widthY - y - 1 + widthY + spacing, -z - 1 - spacing), WorldEdit.flipBlockstate(WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.Z), EnumFacing.Axis.Y));
						}
					} else {
						WorldEdit.sendMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.stackquarter.invalidDirection"));
						return;
					}
				}
			}
		}
		HandlerSetBlock setBlockHandler = (HandlerSetBlock) ClientCommandHandler.instance.createHandler(HandlerSetBlock.class, world, selection);
		HandlerUndo undoHandler = (HandlerUndo) ClientCommandHandler.instance.createHandler(HandlerUndo.class, world, selection);
		undoHandler.setHandler(setBlockHandler);
		undoHandler.running = false;
		undoHandler.saveBlocks(new ArrayList<>(blockList.keySet()));
		setBlockHandler.setBlocks(blockList);
		undoHandler.running = true;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length == 0 || args.length == 1) {
			int spacing;
			if (args.length == 1) {
				spacing = parseInt(args[0]);
			} else {
				spacing = 0;
			}
			if (WorldEdit.hasCurrentSelection()) {
				WorldEditSelection selection = WorldEdit.getCurrentSelection();
				World world = sender.getEntityWorld();
				Thread t = new Thread(() -> stackQuarter(world, selection, spacing));
				t.start();
			} else {
				WorldEdit.sendMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.noAreaSelected"));
			}
		} else {
			WorldEdit.sendMessage(getUsage(sender));
		}
	}
}
