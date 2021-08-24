package net.dugged.cutelessmod.clientcommands;

import net.dugged.cutelessmod.clientcommands.worldedit.WorldEdit;
import net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection.Position.A;
import static net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection.Position.B;

public class HandlerClone extends Handler {
	private static final int COMMANDS_EXECUTED_PER_TICK = 64; // Minimum 2
	private static final int FILL_LIMIT = 32768;
	private static final int CUBE_LENGTH = (int) Math.cbrt(FILL_LIMIT);

	public static boolean clonePermission = false;
	private final List<BlockPos> destinations = new ArrayList<>();
	private final Map<BlockPos, AxisAlignedBB> sourceAreaMap = new LinkedHashMap<>();
	private final Map<BlockPos, BlockPos> iteratorPositions = new LinkedHashMap<>();
	public boolean masked = false;
	public boolean moveBlocks = false;
	public EnumFacing facing = WorldEdit.getLookingDirection();
	public boolean moveSelectionAfterwards = true;

	public HandlerClone(World worldIn, WorldEditSelection selection) {
		super(worldIn, selection);
	}

	public static void getCommandPermission() {
		if (mc.player != null && mc.player.connection != null) {
			clonePermission = false;
			mc.player.connection.sendPacket(new CPacketTabComplete("/clon", null, false));
		}
	}

	synchronized public void clone(BlockPos pos1, BlockPos pos2, BlockPos pos3) {
		AxisAlignedBB bb = new AxisAlignedBB(pos1.getX(), Math.max(0, pos1.getY()), pos1.getZ(), pos2.getX(), Math.min(pos2.getY(), 255), pos2.getZ());
		totalCount += (bb.maxX - bb.minX + 1) * (bb.maxY - bb.minY + 1) * (bb.maxZ - bb.minZ + 1);
		destinations.add(pos3);
		sourceAreaMap.put(pos3, bb);
		iteratorPositions.put(pos3, getDefaultIteratorPos(bb));
	}

	synchronized public void tick() {
		super.tick();
		if (destinations.size() > 0) {
			final int handlerCount = ClientCommandHandler.instance.countHandlerType(HandlerClone.class);
			int commandsExecuted = 0;
			while (destinations.size() > 0 && commandsExecuted < (COMMANDS_EXECUTED_PER_TICK / handlerCount)) {
				BlockPos destinationPosition = destinations.get(destinations.size() - 1);
				BlockPos iteratorPosition = iteratorPositions.get(destinationPosition);
				AxisAlignedBB bb = sourceAreaMap.get(destinationPosition);
				switch (facing) {
					case SOUTH:
						for (int x = (int) bb.minX; x <= bb.maxX; x += CUBE_LENGTH) {
							for (int y = (int) bb.minY; y <= bb.maxY; y += CUBE_LENGTH) {
								for (int z = (int) bb.maxZ; z >= bb.minZ; z -= CUBE_LENGTH) {
									if (iteratorPosition == null) {
										destinations.remove(destinations.size() - 1);
										return;
									}
									if (x == (int) bb.minX && y == (int) bb.minY && z == (int) bb.maxZ) {
										x = iteratorPosition.getX();
										y = iteratorPosition.getY();
										z = iteratorPosition.getZ();
									}
									BlockPos pos1 = new BlockPos(x, y, z);
									BlockPos pos2 = new BlockPos(Math.min(x + CUBE_LENGTH - 1, bb.maxX), Math.min(y + CUBE_LENGTH - 1, bb.maxY), Math.max(z - CUBE_LENGTH + 1, bb.minZ));
									if (commandsExecuted >= (COMMANDS_EXECUTED_PER_TICK / handlerCount)) {
										iteratorPositions.put(destinationPosition, pos1);
										return;
									}
									currentCount += (Math.max(pos1.getX(), pos2.getX()) - Math.min(pos1.getX(), pos2.getX()) + 1) * (Math.max(pos1.getY(), pos2.getY()) - Math.min(pos1.getY(), pos2.getY()) + 1) * (Math.max(pos1.getZ(), pos2.getZ()) - Math.min(pos1.getZ(), pos2.getZ()) + 1);
									if (sendCloneCommand(pos1, pos2, destinationPosition.add(pos1.getX() - bb.minX, pos1.getY() - bb.minY, bb.maxZ - bb.minZ - (bb.maxZ - pos2.getZ())))) {
										commandsExecuted++;
									}
								}
							}
						}
						break;
					case UP:
						for (int z = (int) bb.minZ; z <= bb.maxZ; z += CUBE_LENGTH) {
							for (int x = (int) bb.minX; x <= bb.maxX; x += CUBE_LENGTH) {
								for (int y = (int) bb.maxY; y >= bb.minY; y -= CUBE_LENGTH) {
									if (iteratorPosition == null) {
										destinations.remove(destinations.size() - 1);
										return;
									}
									if (x == (int) bb.minX && y == (int) bb.maxY && z == (int) bb.minZ) {
										x = iteratorPosition.getX();
										y = iteratorPosition.getY();
										z = iteratorPosition.getZ();
									}
									BlockPos pos1 = new BlockPos(x, y, z);
									BlockPos pos2 = new BlockPos(Math.min(x + CUBE_LENGTH - 1, bb.maxX), Math.max(y - CUBE_LENGTH + 1, bb.minY), Math.min(z + CUBE_LENGTH - 1, bb.maxZ));
									if (commandsExecuted >= (COMMANDS_EXECUTED_PER_TICK / handlerCount)) {
										iteratorPositions.put(destinationPosition, pos1);
										return;
									}
									currentCount += (Math.max(pos1.getX(), pos2.getX()) - Math.min(pos1.getX(), pos2.getX()) + 1) * (Math.max(pos1.getY(), pos2.getY()) - Math.min(pos1.getY(), pos2.getY()) + 1) * (Math.max(pos1.getZ(), pos2.getZ()) - Math.min(pos1.getZ(), pos2.getZ()) + 1);
									if (sendCloneCommand(pos1, pos2, destinationPosition.add(pos1.getX() - bb.minX, bb.maxY - bb.minY - (bb.maxY - pos2.getY()), pos1.getZ() - bb.minZ))) {
										commandsExecuted++;
									}
								}
							}
						}
						break;
					case EAST:
						for (int z = (int) bb.minZ; z <= bb.maxZ; z += CUBE_LENGTH) {
							for (int y = (int) bb.minY; y <= bb.maxY; y += CUBE_LENGTH) {
								for (int x = (int) bb.maxX; x >= bb.minX; x -= CUBE_LENGTH) {
									if (iteratorPosition == null) {
										destinations.remove(destinations.size() - 1);
										return;
									}
									if (x == (int) bb.maxX && y == (int) bb.minY && z == (int) bb.minZ) {
										x = iteratorPosition.getX();
										y = iteratorPosition.getY();
										z = iteratorPosition.getZ();
									}
									BlockPos pos1 = new BlockPos(x, y, z);
									BlockPos pos2 = new BlockPos(Math.max(x - CUBE_LENGTH + 1, bb.minX), Math.min(y + CUBE_LENGTH - 1, bb.maxY), Math.min(z + CUBE_LENGTH - 1, bb.maxZ));
									if (commandsExecuted >= (COMMANDS_EXECUTED_PER_TICK / handlerCount)) {
										iteratorPositions.put(destinationPosition, pos1);
										return;
									}
									currentCount += (Math.max(pos1.getX(), pos2.getX()) - Math.min(pos1.getX(), pos2.getX()) + 1) * (Math.max(pos1.getY(), pos2.getY()) - Math.min(pos1.getY(), pos2.getY()) + 1) * (Math.max(pos1.getZ(), pos2.getZ()) - Math.min(pos1.getZ(), pos2.getZ()) + 1);
									if (sendCloneCommand(pos1, pos2, destinationPosition.add(bb.maxX - bb.minX - (bb.maxX - pos2.getX()), pos1.getY() - bb.minY, pos1.getZ() - bb.minZ))) {
										commandsExecuted++;
									}
								}
							}
						}
						break;
					default:
						for (int x = (int) bb.minX; x <= bb.maxX; x += CUBE_LENGTH) {
							for (int y = (int) bb.minY; y <= bb.maxY; y += CUBE_LENGTH) {
								for (int z = (int) bb.minZ; z <= bb.maxZ; z += CUBE_LENGTH) {
									if (iteratorPosition == null) {
										destinations.remove(destinations.size() - 1);
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
										iteratorPositions.put(destinationPosition, pos1);
										return;
									}
									currentCount += (Math.max(pos1.getX(), pos2.getX()) - Math.min(pos1.getX(), pos2.getX()) + 1) * (Math.max(pos1.getY(), pos2.getY()) - Math.min(pos1.getY(), pos2.getY()) + 1) * (Math.max(pos1.getZ(), pos2.getZ()) - Math.min(pos1.getZ(), pos2.getZ()) + 1);
									if (sendCloneCommand(pos1, pos2, destinationPosition.add(pos1.getX() - bb.minX, pos1.getY() - bb.minY, pos1.getZ() - bb.minZ))) {
										commandsExecuted++;
									}
								}
							}
						}
				}
				if (destinations.size() == 1 && moveSelectionAfterwards) {
					selection.setPos(A, destinationPosition);
					selection.setPos(B, destinationPosition.add(bb.maxX - bb.minX, bb.maxY - bb.minY, bb.maxZ - bb.minZ));
				}
				sourceAreaMap.remove(bb);
				if (!sourceAreaMap.containsKey(bb)) {
					iteratorPositions.remove(bb);
					destinations.remove(destinations.size() - 1);
				}
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

	private boolean sendCloneCommand(BlockPos pos1, BlockPos pos2, BlockPos pos3) {
		if (clonePermission && world.isBlockLoaded(pos1) && world.isBlockLoaded(pos2) && world.isBlockLoaded(pos3) && Math.min(pos1.getY(), Math.min(pos2.getY(), pos3.getY())) >= 0 && Math.max(pos1.getY(), Math.max(pos2.getY(), pos3.getY())) < 256) {
			last_execution = age;
			String modifiers = " replace";
			if (masked) {
				modifiers = " masked";
			}
			if (moveBlocks) {
				modifiers += " move";
			} else {
				modifiers += " force";
			}
			world.sendPacketToServer(new CPacketChatMessage("/clone " + pos1.getX() + " " + pos1.getY() + " " + pos1.getZ() + " " + pos2.getX() + " " + pos2.getY() + " " + pos2.getZ() + " " + pos3.getX() + " " + pos3.getY() + " " + pos3.getZ() + modifiers));
			affectedBlocks += (long) (Math.max(pos1.getX(), pos2.getX()) - Math.min(pos1.getX(), pos2.getX()) + 1) * (Math.max(pos1.getY(), pos2.getY()) - Math.min(pos1.getY(), pos2.getY()) + 1) * (Math.max(pos1.getZ(), pos2.getZ()) - Math.min(pos1.getZ(), pos2.getZ()) + 1);
			return true;
		} else {
			return false;
		}
	}

	private BlockPos getDefaultIteratorPos(AxisAlignedBB bb) {
		switch (facing) {
			case SOUTH:
				return new BlockPos(bb.minX, bb.minY, bb.maxZ);
			case UP:
				return new BlockPos(bb.minX, bb.maxY, bb.minZ);
			case EAST:
				return new BlockPos(bb.maxX, bb.minY, bb.minZ);
			default:
				return new BlockPos(bb.minX, bb.minY, bb.minZ);
		}
	}
}
