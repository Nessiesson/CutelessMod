package net.dugged.cutelessmod.clientcommands;

import net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HandlerSetBlock extends Handler {
	private static final int COMMANDS_EXECUTED_PER_TICK = 512;
	private static final int BLOCKS_PROCESSED_PER_TICK = 32768;
	public static boolean setblockPermission = false;
	private final List<BlockPos> blockPositions = new ArrayList<>();
	private final List<BlockPos> skippedPositions = new ArrayList<>();
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
		return (block instanceof BlockBush ||
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
				block instanceof BlockReed ||
				block instanceof BlockTripWireHook);
	}

	synchronized public void setBlock(final BlockPos pos, final IBlockState blockState) {
		if (!blockPositions.contains(pos)) {
			totalCount++;
			blocksToPlace.put(pos, blockState);
			blockPositions.add(blockPositions.size(), pos);
		}
	}

	synchronized public void setBlocks(Map<BlockPos, IBlockState> blockList) {
		totalCount += blockList.size();
		blocksToPlace.putAll(blockList);
		blockPositions.addAll(blockPositions.size(), blockList.keySet());
	}

	synchronized public void tick() {
		super.tick();
		if (blockPositions.size() > 0) {
			final int handlerCount = ClientCommandHandler.instance.countHandlerType(HandlerSetBlock.class);
			int commandsExecuted = 0;
			int counter = 0;
			while (counter <= BLOCKS_PROCESSED_PER_TICK && blockPositions.size() > 0 && commandsExecuted < (COMMANDS_EXECUTED_PER_TICK / handlerCount)) {
				final int i = blockPositions.size() - 1;
				final BlockPos pos = blockPositions.get(i);
				IBlockState blockState = blocksToPlace.get(pos);
				counter++;
				currentCount++;
				if (blockState != null) {
					if (placeLast(blockState.getBlock()) && !skippedPositions.contains(pos)) {
						blockPositions.add(0, pos);
						skippedPositions.add(skippedPositions.size(), pos);
					} else {
						if (sendSetBlockCommand(pos, blockState)) {
							commandsExecuted++;
						}
						blocksToPlace.remove(pos);
					}
				}
				blockPositions.remove(i);
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
