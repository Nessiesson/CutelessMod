package net.dugged.cutelessmod.clientcommands;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class UndoManager {

	private static final UndoManager INSTANCE = new UndoManager();
	private static final int MAX_HISTORY = 10;
	public static boolean isUndoing = false;
	public static boolean savingUndo = true;
	private final Deque<UndoSnapshot> undoHistory = new LinkedList<>();

	public static UndoManager getInstance() {
		return INSTANCE;
	}

	private static BlockPos parseBlockPos(String[] args, int startIndex,
		net.minecraft.client.entity.EntityPlayerSP player) throws NumberFormatException {
		BlockPos base = player.getPosition();
		double x = Double.parseDouble(args[startIndex]);
		double y = Double.parseDouble(args[startIndex + 1]);
		double z = Double.parseDouble(args[startIndex + 2]);
		return new BlockPos(base.getX() + x, Math.max(0, Math.min(base.getY() + y, 255)),
			base.getZ() + z);
	}

	public void pushSnapshot(UndoSnapshot snapshot) {
		if (savingUndo && !isUndoing) {
			undoHistory.push(snapshot);
			if (undoHistory.size() > MAX_HISTORY) {
				undoHistory.removeLast();
			}
		}
	}

	public void undoLast(World world) {
		if (!undoHistory.isEmpty()) {
			isUndoing = true;
			UndoSnapshot snapshot = undoHistory.pop();
			TaskSetBlock task = new TaskSetBlock(snapshot.getSnapshot(), world);
			TaskManager.getInstance().addTask(task);
			isUndoing = false;
		}
	}

	public boolean saveHistory(String msg, World world,
		net.minecraft.client.entity.EntityPlayerSP player) {
		if (!savingUndo) {
			return false;
		}
		String[] temp = msg.split(" ");
		if (temp.length < 4) {
			return false;
		}
		String[] args = new String[temp.length - 1];
		System.arraycopy(temp, 1, args, 0, args.length);
		try {
			if (msg.startsWith("/fill") && args.length >= 7) {
				BlockPos pos1 = parseBlockPos(args, 0, player);
				BlockPos pos2 = parseBlockPos(args, 3, player);
				Map<BlockPos, IBlockState> snapshot = UndoRecorder.recordRegion(pos1, pos2, world);
				pushSnapshot(new UndoSnapshot(snapshot));
				return true;
			} else if (msg.startsWith("/clone") && args.length >= 9) {
				BlockPos pos1 = parseBlockPos(args, 0, player);
				BlockPos pos2 = parseBlockPos(args, 3, player);
				BlockPos pos3 = parseBlockPos(args, 6, player);
				BlockPos pos4 = pos3.add(
					Math.max(pos1.getX(), pos2.getX()) - Math.min(pos1.getX(), pos2.getX()),
					Math.max(pos1.getY(), pos2.getY()) - Math.min(pos1.getY(), pos2.getY()),
					Math.max(pos1.getZ(), pos2.getZ()) - Math.min(pos1.getZ(), pos2.getZ())
				);
				Map<BlockPos, IBlockState> snapshot1 = UndoRecorder.recordRegion(pos1, pos2, world);
				Map<BlockPos, IBlockState> snapshot2 = UndoRecorder.recordRegion(pos3, pos4, world);
				snapshot1.putAll(snapshot2);
				pushSnapshot(new UndoSnapshot(snapshot1));
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
}