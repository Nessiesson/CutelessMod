package net.dugged.cutelessmod.clientcommands;

import java.util.Set;
import net.minecraft.util.math.ChunkPos;

public abstract class TaskChunk extends AbstractTask {

	public abstract int processChunk(ChunkPos pos, int maxPackets);

	public abstract int getTotalOperations();

	public abstract int getProcessedOperations();

	public abstract Set<ChunkPos> getPendingChunks();
}
