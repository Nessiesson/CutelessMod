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
	private static final int COMMANDS_EXECUTED_PER_TICK = 64;
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
		totalCount += (int) ((bb.maxX - bb.minX + 1) * (bb.maxY - bb.minY + 1) * (bb.maxZ - bb.minZ + 1));
		destinations.add(pos3);
		sourceAreaMap.put(pos3, bb);
		iteratorPositions.put(pos3, getDefaultIteratorPos(bb));
	}

	synchronized public void tick() {
		super.tick();
		if (!destinations.isEmpty()) {
			int handlerCount = ClientCommandHandler.instance.countHandlerType(HandlerClone.class);
			int threshold = COMMANDS_EXECUTED_PER_TICK / handlerCount;
			int commandsExecuted = 0;
			while (!destinations.isEmpty() && commandsExecuted < threshold) {
				BlockPos destinationPosition = destinations.get(destinations.size() - 1);
				BlockPos iteratorPosition = iteratorPositions.get(destinationPosition);
				AxisAlignedBB bb = sourceAreaMap.get(destinationPosition);
				int minX = (int) bb.minX, minY = (int) bb.minY, minZ = (int) bb.minZ;
				int maxX = (int) bb.maxX, maxY = (int) bb.maxY, maxZ = (int) bb.maxZ;
				switch (facing) {
					case SOUTH:
						for (int x = minX; x <= maxX; x += CUBE_LENGTH) {
							for (int y = minY; y <= maxY; y += CUBE_LENGTH) {
								for (int z = maxZ; z >= minZ; z -= CUBE_LENGTH) {
									if (iteratorPosition == null) {
										destinations.remove(destinations.size() - 1);
										return;
									}
									if (x == minX && y == minY && z == maxZ) {
										x = iteratorPosition.getX();
										y = iteratorPosition.getY();
										z = iteratorPosition.getZ();
									}
									BlockPos pos1 = new BlockPos(x, y, z);
									BlockPos pos2 = new BlockPos(Math.min(x + CUBE_LENGTH - 1, maxX), Math.min(y + CUBE_LENGTH - 1, maxY), Math.max(z - CUBE_LENGTH + 1, minZ));
									if (commandsExecuted >= threshold) {
										iteratorPositions.put(destinationPosition, pos1);
										return;
									}
									currentCount += (pos2.getX() - pos1.getX() + 1) * (pos2.getY() - pos1.getY() + 1) * (pos2.getZ() - pos1.getZ() + 1);
									if (sendCloneCommand(pos1, pos2, destinationPosition.add(pos1.getX() - minX, pos1.getY() - minY, maxZ - minZ - (maxZ - pos2.getZ())))) {
										commandsExecuted++;
									}
								}
							}
						}
						break;
					case UP:
						for (int z = minZ; z <= maxZ; z += CUBE_LENGTH) {
							for (int x = minX; x <= maxX; x += CUBE_LENGTH) {
								for (int y = maxY; y >= minY; y -= CUBE_LENGTH) {
									if (iteratorPosition == null) {
										destinations.remove(destinations.size() - 1);
										return;
									}
									if (x == minX && y == maxY && z == minZ) {
										x = iteratorPosition.getX();
										y = iteratorPosition.getY();
										z = iteratorPosition.getZ();
									}
									BlockPos pos1 = new BlockPos(x, y, z);
									BlockPos pos2 = new BlockPos(Math.min(x + CUBE_LENGTH - 1, maxX), Math.max(y - CUBE_LENGTH + 1, minY), Math.min(z + CUBE_LENGTH - 1, maxZ));
									if (commandsExecuted >= threshold) {
										iteratorPositions.put(destinationPosition, pos1);
										return;
									}
									currentCount += (pos2.getX() - pos1.getX() + 1) * (pos2.getY() - pos1.getY() + 1) * (pos2.getZ() - pos1.getZ() + 1);
									if (sendCloneCommand(pos1, pos2, destinationPosition.add(pos1.getX() - minX, maxY - minY - (maxY - pos2.getY()), pos1.getZ() - minZ))) {
										commandsExecuted++;
									}
								}
							}
						}
						break;
					case EAST:
						for (int z = minZ; z <= maxZ; z += CUBE_LENGTH) {
							for (int y = minY; y <= maxY; y += CUBE_LENGTH) {
								for (int x = maxX; x >= minX; x -= CUBE_LENGTH) {
									if (iteratorPosition == null) {
										destinations.remove(destinations.size() - 1);
										return;
									}
									if (x == maxX && y == minY && z == minZ) {
										x = iteratorPosition.getX();
										y = iteratorPosition.getY();
										z = iteratorPosition.getZ();
									}
									BlockPos pos1 = new BlockPos(x, y, z);
									BlockPos pos2 = new BlockPos(Math.max(x - CUBE_LENGTH + 1, minX), Math.min(y + CUBE_LENGTH - 1, maxY), Math.min(z + CUBE_LENGTH - 1, maxZ));
									if (commandsExecuted >= threshold) {
										iteratorPositions.put(destinationPosition, pos1);
										return;
									}
									currentCount += (pos2.getX() - pos1.getX() + 1) * (pos2.getY() - pos1.getY() + 1) * (pos2.getZ() - pos1.getZ() + 1);
									if (sendCloneCommand(pos1, pos2, destinationPosition.add(maxX - minX - (maxX - pos2.getX()), pos1.getY() - minY, pos1.getZ() - minZ))) {
										commandsExecuted++;
									}
								}
							}
						}
						break;
					default:
						for (int x = minX; x <= maxX; x += CUBE_LENGTH) {
							for (int y = minY; y <= maxY; y += CUBE_LENGTH) {
								for (int z = minZ; z <= maxZ; z += CUBE_LENGTH) {
									if (iteratorPosition == null) {
										destinations.remove(destinations.size() - 1);
										return;
									}
									if (x == minX && y == minY && z == minZ) {
										x = iteratorPosition.getX();
										y = iteratorPosition.getY();
										z = iteratorPosition.getZ();
									}
									BlockPos pos1 = new BlockPos(x, y, z);
									BlockPos pos2 = new BlockPos(Math.min(x + CUBE_LENGTH - 1, maxX), Math.min(y + CUBE_LENGTH - 1, maxY), Math.min(z + CUBE_LENGTH - 1, maxZ));
									if (commandsExecuted >= threshold) {
										iteratorPositions.put(destinationPosition, pos1);
										return;
									}
									currentCount += (pos2.getX() - pos1.getX() + 1) * (pos2.getY() - pos1.getY() + 1) * (pos2.getZ() - pos1.getZ() + 1);
									if (sendCloneCommand(pos1, pos2, destinationPosition.add(pos1.getX() - minX, pos1.getY() - minY, pos1.getZ() - minZ))) {
										commandsExecuted++;
									}
								}
							}
						}
				}
				if (destinations.size() == 1 && moveSelectionAfterwards) {
					selection.setPos(A, destinationPosition);
					selection.setPos(B, destinationPosition.add(maxX - minX, maxY - minY, maxZ - minZ));
				}
				sourceAreaMap.remove(destinationPosition);
				iteratorPositions.remove(destinationPosition);
				destinations.remove(destinations.size() - 1);
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