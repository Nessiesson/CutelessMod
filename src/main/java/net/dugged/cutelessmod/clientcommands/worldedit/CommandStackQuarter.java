package net.dugged.cutelessmod.clientcommands.worldedit;

import net.dugged.cutelessmod.clientcommands.ClientCommand;
import net.dugged.cutelessmod.clientcommands.ClientCommandHandler;
import net.dugged.cutelessmod.clientcommands.HandlerSetBlock;
import net.dugged.cutelessmod.clientcommands.HandlerUndo;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
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
import java.util.List;
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

	private void stackQuarter(World world) {
		final int tollerance = 15;
		final Minecraft mc = Minecraft.getMinecraft();
		final float yaw = MathHelper.wrapDegrees(mc.getRenderViewEntity().rotationYaw);
		final float pitch = MathHelper.wrapDegrees(mc.getRenderViewEntity().rotationPitch);
		final int widthX = WorldEdit.widthX();
		final int widthY = WorldEdit.widthY();
		final int widthZ = WorldEdit.widthZ();
		Map<BlockPos, IBlockState> blockList = new HashMap<>();
		for (int x = 0; x < WorldEdit.widthX(); x++) {
			for (int y = 0; y < WorldEdit.widthY(); y++) {
				for (int z = 0; z < WorldEdit.widthZ(); z++) {
					IBlockState blockState = world.getBlockState(WorldEdit.minPos().add(x, y, z));

					// Horizontal diagonals
					// SW -X +Z
					if (yaw >= 45 - tollerance && yaw <= 45 + tollerance && pitch >= -tollerance && pitch <= tollerance) {
						blockList.put(WorldEdit.minPos().add(-x - 1, y, z), WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.X));
						blockList.put(WorldEdit.minPos().add(x, y, widthZ - z - 1 + widthZ), WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.Z));
						blockList.put(WorldEdit.minPos().add(-x - 1, y, widthZ - z - 1 + widthZ), WorldEdit.flipBlockstate(WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.X), EnumFacing.Axis.Z));
						// NW -X -Z
					} else if (yaw >= 135 - tollerance && yaw <= 135 + tollerance && pitch >= -tollerance && pitch <= tollerance) {
						blockList.put(WorldEdit.minPos().add(-x - 1, y, z), WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.X));
						blockList.put(WorldEdit.minPos().add(x, y, -z - 1), WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.Z));
						blockList.put(WorldEdit.minPos().add(-x - 1, y, -z - 1), WorldEdit.flipBlockstate(WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.X), EnumFacing.Axis.Z));
						// SE +X +Z
					} else if (yaw >= -45 - tollerance && yaw <= -45 + tollerance && pitch >= -tollerance && pitch <= tollerance) {
						blockList.put(WorldEdit.minPos().add(widthX - x - 1 + widthX, y, z), WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.X));
						blockList.put(WorldEdit.minPos().add(x, y, widthZ - z - 1 + widthZ), WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.Z));
						blockList.put(WorldEdit.minPos().add(widthX - x - 1 + widthX, y, widthZ - z - 1 + widthZ), WorldEdit.flipBlockstate(WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.X), EnumFacing.Axis.Z));
						// NE +X -Z
					} else if (yaw >= -135 - tollerance && yaw <= -135 + tollerance && pitch >= -tollerance && pitch <= tollerance) {
						blockList.put(WorldEdit.minPos().add(widthX - x - 1 + widthX, y, z), WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.X));
						blockList.put(WorldEdit.minPos().add(x, y, -z - 1), WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.Z));
						blockList.put(WorldEdit.minPos().add(widthX - x - 1 + widthX, y, -z - 1), WorldEdit.flipBlockstate(WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.X), EnumFacing.Axis.Z));

						// Vertical diagonals
					} else if (yaw >= -tollerance && yaw <= tollerance) {
						// S +Z +Y
						if (pitch >= 45 - tollerance && pitch <= 45 + tollerance) {
							blockList.put(WorldEdit.minPos().add(x, y, widthZ - z - 1 + widthZ), WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.Z));
							blockList.put(WorldEdit.minPos().add(x, -y - 1, z), WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.Y));
							blockList.put(WorldEdit.minPos().add(x, -y - 1, widthZ - z - 1 + widthZ), WorldEdit.flipBlockstate(WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.Z), EnumFacing.Axis.Y));
							// S +Z -Y
						} else if (pitch >= -45 - tollerance && pitch <= -45 + tollerance) {
							blockList.put(WorldEdit.minPos().add(x, y, widthZ - z - 1 + widthZ), WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.Z));
							blockList.put(WorldEdit.minPos().add(x, widthY - y - 1 + widthY, z), WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.Y));
							blockList.put(WorldEdit.minPos().add(x, widthY - y - 1 + widthY, widthZ - z - 1 + widthZ), WorldEdit.flipBlockstate(WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.Z), EnumFacing.Axis.Y));
						}
					} else if (yaw >= 90 - tollerance && yaw <= 90 + tollerance) {
						// W -X +Y
						if (pitch >= 45 - tollerance && pitch <= 45 + tollerance) {
							blockList.put(WorldEdit.minPos().add(-x - 1, y, z), WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.X));
							blockList.put(WorldEdit.minPos().add(x, -y - 1, z), WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.X));
							blockList.put(WorldEdit.minPos().add(-x - 1, -y - 1, z), WorldEdit.flipBlockstate(WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.X), EnumFacing.Axis.Y));
							// W -X -Y
						} else if (pitch >= -45 - tollerance && pitch <= -45 + tollerance) {
							blockList.put(WorldEdit.minPos().add(-x - 1, y, z), WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.X));
							blockList.put(WorldEdit.minPos().add(x, widthY - y - 1 + widthY, z), WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.Y));
							blockList.put(WorldEdit.minPos().add(-x - 1, widthY - y - 1 + widthY, z), WorldEdit.flipBlockstate(WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.X), EnumFacing.Axis.Y));
						}
					} else if (yaw >= -90 - tollerance && yaw <= -90 + tollerance) {
						// E +X +Y
						if (pitch >= 45 - tollerance && pitch <= 45 + tollerance) {
							blockList.put(WorldEdit.minPos().add(widthX - x - 1 + widthX, y, z), WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.X));
							blockList.put(WorldEdit.minPos().add(x, -y - 1, z), WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.Y));
							blockList.put(WorldEdit.minPos().add(widthX - x - 1 + widthX, -y - 1, z), WorldEdit.flipBlockstate(WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.X), EnumFacing.Axis.Y));
							// E +X -Y
						} else if (pitch >= -45 - tollerance && pitch <= -45 + tollerance) {
							blockList.put(WorldEdit.minPos().add(widthX - x - 1 + widthX, y, z), WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.X));
							blockList.put(WorldEdit.minPos().add(x, widthY - y - 1 + widthY, z), WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.Y));
							blockList.put(WorldEdit.minPos().add(widthX - x - 1 + widthX, widthY - y - 1 + widthY, z), WorldEdit.flipBlockstate(WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.X), EnumFacing.Axis.Y));
						}
					} else if (yaw >= 180 - tollerance || yaw <= -180 + tollerance) {
						// N -Z +Y
						if (pitch >= 45 - tollerance && pitch <= 45 + tollerance) {
							blockList.put(WorldEdit.minPos().add(x, y, -z - 1), WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.Z));
							blockList.put(WorldEdit.minPos().add(x, -y - 1, z), WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.Y));
							blockList.put(WorldEdit.minPos().add(x, -y - 1, -z - 1), WorldEdit.flipBlockstate(WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.Z), EnumFacing.Axis.Y));
							// N -Z -Y
						} else if (pitch >= -45 - tollerance && pitch <= -45 + tollerance) {
							blockList.put(WorldEdit.minPos().add(x, y, -z - 1), WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.Z));
							blockList.put(WorldEdit.minPos().add(x, widthY - y - 1 + widthY, z), WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.Y));
							blockList.put(WorldEdit.minPos().add(x, widthY - y - 1 + widthY, -z - 1), WorldEdit.flipBlockstate(WorldEdit.flipBlockstate(blockState, EnumFacing.Axis.Z), EnumFacing.Axis.Y));
						}
					} else {
						WorldEdit.sendMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.stackquarter.invalidDirection"));
						return;
					}
				}
			}
		}
		HandlerSetBlock setBlockHandler = (HandlerSetBlock) ClientCommandHandler.instance.createHandler(HandlerSetBlock.class, world);
		HandlerUndo undoHandler = (HandlerUndo) ClientCommandHandler.instance.createHandler(HandlerUndo.class, world);
		undoHandler.setHandler(setBlockHandler);
		undoHandler.running = false;
		undoHandler.saveBlocks(new ArrayList<>(blockList.keySet()));
		setBlockHandler.setBlocks(blockList);
		undoHandler.running = true;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length == 0) {
			if (WorldEdit.hasSelection()) {
				World world = sender.getEntityWorld();
				Thread t = new Thread(() -> stackQuarter(world));
				t.start();
			} else {
				WorldEdit.sendMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.noAreaSelected"));
			}
		} else {
			WorldEdit.sendMessage(getUsage(sender));
		}
	}
}
