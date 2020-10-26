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
	private static final int BLOCKS_PLACED_PER_TICK = 2048;
	private static final int BLOCKS_PROCESSED_PER_TICK = 4096;

	public static boolean sendCommandfeedback = true;
	public static boolean logAdminCommands = true;
	public static boolean doTileDrops = true;
	public static boolean gamerulePermission = false;
	public static boolean setblockPermission = false;
	private final List<BlockPos> blockPositions = new ArrayList<>();
	private final Map<BlockPos, IBlockState> blocksToPlace = new HashMap<>();
	public boolean failed = false;
	public boolean sendAffectedBlocks = false;
	private int affectedBlocks = 0;
	private World world;

	public static void getGameruleStates() {
		if (mc.player != null && mc.player.connection != null) {
			setblockPermission = false;
			gamerulePermission = false;
			mc.player.connection.sendPacket(new CPacketTabComplete("/gamerul", null, false));
			mc.player.connection.sendPacket(new CPacketTabComplete("/setbloc", null, false));
			if (gamerulePermission) {
				mc.player.connection.sendPacket(new CPacketChatMessage("/gamerule doTileDrops"));
				mc.player.connection.sendPacket(new CPacketChatMessage("/gamerule sendCommandFeedback"));
				mc.player.connection.sendPacket(new CPacketChatMessage("/gamerule logAdminCommands"));
			}
		}
	}

	public void init(World worldToPlaceIn) {
		world = worldToPlaceIn;
		if (gamerulePermission) {
			if (sendCommandfeedback) {
				mc.player.connection.sendPacket(new CPacketChatMessage("/gamerule sendCommandFeedback false"));
			}
			if (logAdminCommands) {
				mc.player.connection.sendPacket(new CPacketChatMessage("/gamerule logAdminCommands false"));
			}
			if (doTileDrops) {
				mc.player.connection.sendPacket(new CPacketChatMessage("/gamerule doTileDrops false"));
			}
		}
	}

	public void setBlock(final BlockPos pos, final IBlockState blockState) {
		blockPositions.add(pos);
		blocksToPlace.put(pos, blockState);
	}

	public void setBlocks(Map<BlockPos, IBlockState> blockList) {
		blockPositions.addAll(blockList.keySet());
		blocksToPlace.putAll(blockList);
	}

	public void tick() {
		if (blockPositions.size() > 0) {
			final int handlerCount = ClientCommandHandler.instance.countHandlerType(HandlerSetBlock.class);
			//System.out.println("Blocks to place: " + blockPositions.size() + " currently placing: " + Math.min(blockPositions.size(), BLOCKS_PLACED_PER_TICK / handlerCount) + " per tick");
			int placedBlocks = 0;
			for (int i = 0; i < (BLOCKS_PROCESSED_PER_TICK / handlerCount); i++) {
				if (blockPositions.size() <= 0 || placedBlocks >= (BLOCKS_PLACED_PER_TICK / handlerCount)) {
					return;
				}
				final BlockPos pos = blockPositions.get(0);
				if (placeBlock(pos, blocksToPlace.get(pos))) {
					placedBlocks++;
				}
				blocksToPlace.remove(pos);
				blockPositions.remove(0);
			}
		} else {
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

	private boolean placeBlock(BlockPos pos, IBlockState blockState) {
		final String name = blockState.getBlock().getRegistryName().toString();
		final String metadata = Integer.toString(blockState.getBlock().getMetaFromState(blockState));
		if (setblockPermission && world.isBlockLoaded(pos) && pos.getY() >= 0 && pos.getY() < 256 && !world.getBlockState(pos).equals(blockState)) {
			world.sendPacketToServer(new CPacketChatMessage("/setblock " + pos.getX() + " " + pos.getY() + " " + pos.getZ() + " " + name + " " + metadata));
			affectedBlocks++;
			return true;
		} else {
			failed = true;
			return false;
		}
	}
}
