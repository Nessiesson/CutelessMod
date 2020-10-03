package net.dugged.cutelessmod.clientcommands;

import net.minecraft.block.state.IBlockState;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HandlerSaveBlockBox extends Handler {
	private static final int BLOCKS_PROCESSED_PER_TICK = 4096;

	public String command = null;
	private final List<Iterator> iterators = new ArrayList<>();
	private Map<BlockPos, IBlockState> blockList;
	private boolean message = false;
	private World world;

	public void init(final World worldToRecord, Map<BlockPos, IBlockState> list, final String commandToExecuteAfterwards, final boolean sendFinishMessage) {
		world = worldToRecord;
		blockList = list;
		if (!commandToExecuteAfterwards.isEmpty()) {
			command = commandToExecuteAfterwards;
		}
		message = sendFinishMessage;
	}

	public void saveBox(BlockPos pos1, BlockPos pos2) {
		iterators.add(BlockPos.getAllInBox(pos1, pos2).iterator());
	}

	public void tick() {
		iterators.removeIf(iterator -> !iterator.hasNext());
		if (iterators.size() > 0) {
			int iterCount = 0;
			Iterator iterator = iterators.get(0);
			while (iterator.hasNext() && iterCount <= BLOCKS_PROCESSED_PER_TICK / ClientCommandHandler.instance.countHandlerType(HandlerSaveBlockBox.class)) {
				final BlockPos pos = (BlockPos) iterator.next();
				blockList.put(pos, world.getBlockState(pos));
			}
		} else {
			if (command != null && !command.isEmpty()) {
				mc.player.connection.sendPacket(new CPacketChatMessage(command));
			}
			if (message) {
				mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.undo.savedUndo"));
			}
			finished = true;
		}
	}
}
