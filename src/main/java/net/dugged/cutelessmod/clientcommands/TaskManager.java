package net.dugged.cutelessmod.clientcommands;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.util.math.ChunkPos;

public class TaskManager {

	private static final TaskManager instance = new TaskManager();
	private static final int OPERATION_RANGE = 4;
	public static boolean gamerulePermission = false;
	public final List<Thread> threads = new LinkedList<>();
	private final List<TaskChunk> tasks = new LinkedList<>();
	private final Set<ChunkPos> currentlyProcessing = new HashSet<>();
	private boolean processing = false;

	public static TaskManager getInstance() {
		return instance;
	}

	public void addTask(TaskChunk task) {
		tasks.add(task);
	}

	public List<TaskChunk> getTasks() {
		return Collections.unmodifiableList(tasks);
	}

	public float getProgress() {
		int total = 0;
		int processed = 0;
		for (TaskChunk task : tasks) {
			total += task.getTotalOperations();
			processed += task.getProcessedOperations();
		}
		return total == 0 ? 0 : (float) processed / total;
	}

	public boolean isWaitingForPlayer() {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.player == null) {
			return false;
		}
		ChunkPos playerChunk = new ChunkPos(mc.player.getPosition());
		int range = 2;
		for (TaskChunk task : tasks) {
			for (ChunkPos cp : new HashSet<>(task.getPendingChunks())) {
				if (Math.abs(cp.x - playerChunk.x) > range
					|| Math.abs(cp.z - playerChunk.z) > range) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isActive() {
		return !tasks.isEmpty();
	}

	public Set<ChunkPos> getCurrentlyProcessingChunks() {
		return currentlyProcessing;
	}

	public void tick() {
		currentlyProcessing.clear();
		int packetsThisTick = 0;
		Minecraft mc = Minecraft.getMinecraft();
		ChunkPos playerChunk = new ChunkPos(mc.player.getPosition());
		Iterator<TaskChunk> it = tasks.iterator();
		if (!processing && (it.hasNext() && packetsThisTick < ClientCommandHandler.PACKET_LIMIT)) {
			processing = true;
			if (gamerulePermission) {
				mc.player.connection.sendPacket(
					new CPacketChatMessage("/gamerule sendCommandFeedback false"));
				mc.player.connection.sendPacket(
					new CPacketChatMessage("/gamerule logAdminCommands false"));
				mc.player.connection.sendPacket(
					new CPacketChatMessage("/gamerule doTileDrops false"));
			}
		} else if (processing && !(it.hasNext()
			&& packetsThisTick < ClientCommandHandler.PACKET_LIMIT)) {
			processing = false;
			if (gamerulePermission) {
				mc.player.connection.sendPacket(
					new CPacketChatMessage("/gamerule doTileDrops true"));
				mc.player.connection.sendPacket(
					new CPacketChatMessage("/gamerule logAdminCommands true"));
				mc.player.connection.sendPacket(
					new CPacketChatMessage("/gamerule sendCommandFeedback true"));
			}
		}
		while (it.hasNext() && packetsThisTick < ClientCommandHandler.PACKET_LIMIT) {
			TaskChunk task = it.next();
			for (ChunkPos cp : new HashSet<>(task.getPendingChunks())) {
				if (Math.abs(cp.x - playerChunk.x) <= OPERATION_RANGE
					&& Math.abs(cp.z - playerChunk.z) <= OPERATION_RANGE) {
					int processed = task.processChunk(cp,
						ClientCommandHandler.PACKET_LIMIT - packetsThisTick);
					if (processed > 0) {
						currentlyProcessing.add(cp);
					}
					packetsThisTick += processed;
					if (packetsThisTick >= ClientCommandHandler.PACKET_LIMIT) {
						break;
					}
				}
			}
			if (task.isComplete()) {
				it.remove();
			}
		}
		threads.removeIf(thread -> thread.isInterrupted() || !thread.isAlive());
	}
}