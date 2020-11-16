package net.dugged.cutelessmod.clientcommands;

import net.minecraft.block.state.IBlockState;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HandlerSetBlock extends Handler {
	private static final int COMMANDS_EXECUTED_PER_TICK = 1048;
	private static final int BLOCKS_PROCESSED_PER_TICK = 4096;
	public static boolean setblockPermission = false;
	private final List<BlockPos> blockPositions = new ArrayList<>();
	private final Map<BlockPos, IBlockState> blocksToPlace = new HashMap<>();

	public HandlerSetBlock(World worldIn) {
		super(worldIn);
	}

	public static void getGameruleStates() {
		if (mc.player != null && mc.player.connection != null) {
			setblockPermission = false;
			mc.player.connection.sendPacket(new CPacketTabComplete("/setbloc", null, false));
		}
	}

	synchronized public void setBlock(final BlockPos pos, final IBlockState blockState) {
		if (!blockPositions.contains(pos)) {
			blockPositions.add(pos);
			blocksToPlace.put(pos, blockState);
		}
	}

	synchronized public void setBlocks(Map<BlockPos, IBlockState> blockList) {
		blockPositions.addAll(blockList.keySet());
		blocksToPlace.putAll(blockList);
	}

	synchronized public void tick() {
		super.tick();
		if (blockPositions.size() > 0) {
			final int handlerCount = ClientCommandHandler.instance.countHandlerType(HandlerSetBlock.class);
			int commandsExecuted = 0;
			int counter = 0;
			while (counter <= BLOCKS_PROCESSED_PER_TICK && blockPositions.size() > 0 && commandsExecuted < (COMMANDS_EXECUTED_PER_TICK / handlerCount)) {
				final BlockPos pos = blockPositions.get(0);
				IBlockState blockToPlace = blocksToPlace.get(pos);
				if (blockToPlace != null && sendSetBlockCommand(pos, blockToPlace)) {
					commandsExecuted++;
				}
				blocksToPlace.remove(pos);
				blockPositions.remove(0);
				counter++;
			}
		} else if (age > 100) {
			if (gamerulePermission) {
				if (doTileDrops) {
					mc.player.connection.sendPacket(new CPacketChatMessage("/gamerule doTileDrops true"));
				}
				if (logAdminCommands) {
					mc.player.connection.sendPacket(new CPacketChatMessage("/gamerule logAdminCommands true"));
				}
				if (sendCommandfeedback) {
					mc.player.connection.sendPacket(new CPacketChatMessage("/gamerule sendCommandFeedback true"));
				}
				if (sendAffectedBlocks) {
					mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("commands.fill.success", affectedBlocks));
				}
			}
			getGameruleStates();
			finished = true;
		}
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
