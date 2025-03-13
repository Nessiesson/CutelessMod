package net.dugged.cutelessmod.clientcommands;

import net.dugged.cutelessmod.clientcommands.worldedit.WorldEdit;
import net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HandlerUndo extends Handler {
	private static final int BLOCKS_PROCESSED_PER_TICK = 32768;
	private final List<Iterator<BlockPos>> iterators = new ArrayList<>();
	private final Map<BlockPos, IBlockState> blockList = new LinkedHashMap<>();
	public String command;
	public boolean message = true;
	private Handler handler = null;
	private boolean started = false;

	public HandlerUndo(World worldIn, WorldEditSelection selection) {
		super(worldIn, selection);
	}

	public void setHandler(Handler handlerExecutedAfterwards) {
		handler = handlerExecutedAfterwards;
		if (handler != null) {
			handler.running = false;
		}
	}

	public void saveBox(BlockPos pos1, BlockPos pos2) {
		totalCount += (Math.max(pos1.getX(), pos2.getX()) - Math.min(pos1.getX(), pos2.getX()) + 1) * (Math.max(pos1.getY(), pos2.getY()) - Math.min(pos1.getY(), pos2.getY()) + 1) * (Math.max(pos1.getZ(), pos2.getZ()) - Math.min(pos1.getZ(), pos2.getZ()) + 1);
		iterators.add(BlockPos.getAllInBox(pos1, pos2).iterator());
	}

	public void saveBlocks(List<BlockPos> listOfBlocks) {
		totalCount += listOfBlocks.size();
		iterators.add(listOfBlocks.iterator());
	}

	synchronized public void tick() {
		if (!WorldEdit.undo) {
			if (handler != null) {
				handler.running = true;
			}
			finished = true;
			return;
		}
		super.tick();
		if (!started) {
			started = true;
			if (message) {
				mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.undo.startedSaving"));
			}
		}
		int threshold = BLOCKS_PROCESSED_PER_TICK / ClientCommandHandler.instance.countHandlerType(HandlerUndo.class);
		int iterCount = 0;
		while (!iterators.isEmpty() && iterCount <= threshold) {
			Iterator<BlockPos> it = iterators.get(0);
			while (it.hasNext()) {
				BlockPos pos = it.next();
				last_execution = age;
				currentCount++;
				blockList.put(pos, world.getBlockState(pos));
				iterCount++;
				if (iterCount > threshold) {
					break;
				}
			}
			if (!it.hasNext()) {
				iterators.remove(0);
			}
			if (iterCount > threshold) {
				break;
			}
		}
		if (iterators.isEmpty()) {
			CommandUndo.undoHistory.add(0, blockList);
			if (command != null && !command.isEmpty()) {
				mc.player.connection.sendPacket(new CPacketChatMessage(command));
			}
			if (handler != null) {
				handler.running = true;
			}
			if (message) {
				mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.undo.savedUndo"));
			}
			finished = true;
		}
	}
}