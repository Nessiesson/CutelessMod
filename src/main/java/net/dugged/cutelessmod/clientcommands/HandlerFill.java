package net.dugged.cutelessmod.clientcommands;

import net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HandlerFill extends Handler {
	private static final int COMMANDS_EXECUTED_PER_TICK = 64; // Minimum 2
	private static final int FILL_LIMIT = 32768;
	private static final int CUBE_LENGTH = (int) Math.pow(FILL_LIMIT, 1.0 / 3.0);

	public static boolean fillPermission = false;
	private final List<AxisAlignedBB> areas = new ArrayList<>();
	private final Map<AxisAlignedBB, IBlockState> blockStateMap = new LinkedHashMap<>();
	private final Map<AxisAlignedBB, BlockPos> iteratorPositions = new LinkedHashMap<>();

	public HandlerFill(World worldIn, WorldEditSelection selection) {
		super(worldIn, selection);
	}

	public static void getCommandPermission() {
		if (mc.player != null && mc.player.connection != null) {
			fillPermission = false;
			mc.player.connection.sendPacket(new CPacketTabComplete("/fil", null, false));
		}
	}

	public void fill(BlockPos pos1, BlockPos pos2, IBlockState blockStateToPlace) {
		AxisAlignedBB bb = new AxisAlignedBB(pos1, pos2);
		totalCount += (bb.maxX - bb.minX + 1) * (bb.maxY - bb.minY + 1) * (bb.maxZ - bb.minZ + 1);
		areas.add(bb);
		blockStateMap.put(bb, blockStateToPlace);
		iteratorPositions.put(bb, new BlockPos(bb.minX, bb.minY, bb.minZ));
	}

	synchronized public void tick() {
		super.tick();
		if (areas.size() > 0) {
			final int handlerCount = ClientCommandHandler.instance.countHandlerType(HandlerFill.class);
			int commandsExecuted = 0;
			while (areas.size() > 0 && commandsExecuted < (COMMANDS_EXECUTED_PER_TICK / handlerCount)) {
				AxisAlignedBB bb = areas.get(areas.size() - 1);
				BlockPos iteratorPosition = iteratorPositions.get(bb);
				IBlockState blockState = blockStateMap.get(bb);
				for (int x = (int) bb.minX; x < bb.maxX + 1; x += CUBE_LENGTH) {
					for (int y = (int) bb.minY; y < bb.maxY + 1; y += CUBE_LENGTH) {
						for (int z = (int) bb.minZ; z < bb.maxZ + 1; z += CUBE_LENGTH) {
							if (iteratorPosition == null) {
								areas.remove(areas.size() - 1);
								return;
							}
							if (x == (int) bb.minX && y == (int) bb.minY && z == (int) bb.minZ) {
								x = iteratorPosition.getX();
								y = iteratorPosition.getY();
								z = iteratorPosition.getZ();
							}
							BlockPos pos1 = new BlockPos(x, y, z);
							BlockPos pos2 = new BlockPos(Math.min(x + CUBE_LENGTH - 1, bb.maxX), Math.min(y + CUBE_LENGTH - 1, bb.maxY), Math.min(z + CUBE_LENGTH - 1, bb.maxZ));
							if (commandsExecuted >= (COMMANDS_EXECUTED_PER_TICK / handlerCount)) {
								iteratorPositions.put(bb, pos1);
								return;
							}
							currentCount += (Math.max(pos1.getX(), pos2.getX()) - Math.min(pos1.getX(), pos2.getX()) + 1) * (Math.max(pos1.getY(), pos2.getY()) - Math.min(pos1.getY(), pos2.getY()) + 1) * (Math.max(pos1.getZ(), pos2.getZ()) - Math.min(pos1.getZ(), pos2.getZ()) + 1);
							if (sendFillCommand(pos1, pos2, blockState)) {
								commandsExecuted++;
							}
						}
					}
				}
				iteratorPositions.remove(bb);
				blockStateMap.remove(bb);
				areas.remove(areas.size() - 1);
			}
		} else if (age > 5) {
			finish();
		}
	}

	public void finish() {
		if (sendAffectedBlocks) {
			mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("commands.fill.success", affectedBlocks));
		}
		super.finish();
	}

	private boolean sendFillCommand(BlockPos pos1, BlockPos pos2, IBlockState blockState) {
		final String name = blockState.getBlock().getRegistryName().toString();
		final String metadata = Integer.toString(blockState.getBlock().getMetaFromState(blockState));
		if (fillPermission && world.isBlockLoaded(pos1) && world.isBlockLoaded(pos2) && Math.min(pos1.getY(), pos2.getY()) >= 0 && Math.max(pos1.getY(), pos2.getY()) < 256) {
			last_execution = age;
			world.sendPacketToServer(new CPacketChatMessage("/fill " + pos1.getX() + " " + pos1.getY() + " " + pos1.getZ() + " " + pos2.getX() + " " + pos2.getY() + " " + pos2.getZ() + " " + name + " " + metadata));
			affectedBlocks += (Math.max(pos1.getX(), pos2.getX()) - Math.min(pos1.getX(), pos2.getX()) + 1) * (Math.max(pos1.getY(), pos2.getY()) - Math.min(pos1.getY(), pos2.getY()) + 1) * (Math.max(pos1.getZ(), pos2.getZ()) - Math.min(pos1.getZ(), pos2.getZ()) + 1);
			return true;
		} else {
			return false;
		}
	}
}
