package net.dugged.cutelessmod.clientcommands;

import net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ArrayDeque;
import java.util.Set;

public class HandlerSetBlock extends Handler {
	private static final int COMMANDS_EXECUTED_PER_TICK = 512;
	private static final int BLOCKS_PROCESSED_PER_TICK = 32768;
	public static boolean setblockPermission = false;

	private final Deque<BlockPos> blockPositions = new ArrayDeque<>();
	private final Set<BlockPos> blockPositionSet = new HashSet<>();

	private final Map<BlockPos, Integer> skippedPositions = new LinkedHashMap<>();
	private final Map<BlockPos, IBlockState> blocksToPlace = new LinkedHashMap<>();

	public HandlerSetBlock(World worldIn, WorldEditSelection selection) {
		super(worldIn, selection);
	}

	public static void getCommandPermission() {
		if (mc.player != null && mc.player.connection != null) {
			setblockPermission = false;
			mc.player.connection.sendPacket(new CPacketTabComplete("/setbloc", null, false));
		}
	}

	private static boolean placeLast(Block block) {
		return (block instanceof BlockBed ||
			block instanceof BlockBush ||
			block instanceof BlockFlowerPot ||
			block instanceof BlockFire ||
			block instanceof BlockButton ||
			block instanceof BlockSign ||
			block instanceof BlockChorusFlower ||
			block instanceof BlockCake ||
			block instanceof BlockCarpet ||
			block instanceof BlockRailBase ||
			block instanceof BlockEndRod ||
			block instanceof BlockLever ||
			block instanceof BlockRedstoneWire ||
			block instanceof BlockCactus ||
			block instanceof BlockVine ||
			block instanceof BlockSnow ||
			block instanceof BlockTorch ||
			block instanceof BlockLadder ||
			block instanceof BlockBanner ||
			block instanceof BlockDoor ||
			block instanceof BlockRedstoneDiode ||
			block instanceof BlockBasePressurePlate ||
			block instanceof BlockPistonMoving ||
			block instanceof BlockPistonExtension ||
			block instanceof BlockReed ||
			block instanceof BlockTripWireHook);
	}

	synchronized public void setBlock(final BlockPos pos, final IBlockState blockState) {
		if (blockPositionSet.add(pos)) {
			totalCount++;
			blocksToPlace.put(pos, blockState);
			blockPositions.addLast(pos);
		}
	}

	synchronized public void setBlocks(Map<BlockPos, IBlockState> blockList) {
		for (Map.Entry<BlockPos, IBlockState> entry : blockList.entrySet()) {
			BlockPos pos = entry.getKey();
			if (blockPositionSet.add(pos)) {
				totalCount++;
				blocksToPlace.put(pos, entry.getValue());
				blockPositions.addLast(pos);
			}
		}
	}

	synchronized public void tick() {
		super.tick();
		if (!blockPositions.isEmpty()) {
			final int handlerCount = ClientCommandHandler.instance.countHandlerType(HandlerSetBlock.class);
			int commandsExecuted = 0;
			int counter = 0;
			while (counter <= BLOCKS_PROCESSED_PER_TICK && !blockPositions.isEmpty() && commandsExecuted < (COMMANDS_EXECUTED_PER_TICK / handlerCount)) {
				BlockPos pos = blockPositions.pollLast();
				blockPositionSet.remove(pos);
				IBlockState blockState = blocksToPlace.get(pos);
				counter++;
				currentCount++;
				if (blockState != null) {
					int j = skippedPositions.getOrDefault(pos, 0);
					if (placeLast(blockState.getBlock()) && j <= 5) {
						if (j > 0 && sendSetBlockCommand(pos, blockState)) {
							commandsExecuted++;
						}
						blockPositions.addFirst(pos);
						blockPositionSet.add(pos);
						skippedPositions.put(pos, j + 1);
					} else {
						if (sendSetBlockCommand(pos, blockState)) {
							commandsExecuted++;
						}
						blocksToPlace.remove(pos);
						if (j > 0) {
							skippedPositions.remove(pos);
						}
					}
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

	private boolean sendSetBlockCommand(BlockPos pos, IBlockState blockState) {
		final String name = blockState.getBlock().getRegistryName().toString();
		final String metadata = Integer.toString(blockState.getBlock().getMetaFromState(blockState));
		if (setblockPermission && world.isBlockLoaded(pos) && pos.getY() >= 0 && pos.getY() < 256 && !world.getBlockState(pos).equals(blockState)) {
			last_execution = age;
			world.sendPacketToServer(new CPacketChatMessage("/setblock " + pos.getX() + " " + pos.getY() + " " + pos.getZ() + " " + name + " " + metadata));
			affectedBlocks++;
			return true;
		} else {
			return false;
		}
	}
}