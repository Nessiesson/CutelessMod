package net.dugged.cutelessmod.chunk_display;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;

import java.util.HashMap;
import java.util.Map;

public class PacketSplitter {
	public static final int DEFAULT_MAX_RECEIVE_SIZE = Integer.MAX_VALUE;

	private static final Map<String, ReadingSession> readingSessions = new HashMap<>();

	// Theres never more than a few bytes send at once, so splitting upon sending is not implemented
	// Splittings packets in here while still using the native forge channels turns out to be cancer

	public static PacketBuffer receive(String channel, PacketBuffer data) {
		return receive(channel, data, DEFAULT_MAX_RECEIVE_SIZE);
	}

	public static PacketBuffer receive(String channel, PacketBuffer data, int maxLength) {
		return readingSessions.computeIfAbsent(channel, ReadingSession::new).receive(data, maxLength);
	}

	private static class ReadingSession {
		private final String key;
		private int expectedSize = -1;
		private PacketBuffer received;

		private ReadingSession(String key) {
			this.key = key;
		}

		private PacketBuffer receive(PacketBuffer data, int maxLength) {
			if (expectedSize < 0) {
				expectedSize = data.readVarInt();
				if (expectedSize > maxLength) throw new IllegalArgumentException("Payload too large");
				received = new PacketBuffer(Unpooled.buffer(expectedSize));
			}
			received.writeBytes(data.readBytes(data.readableBytes()));
			if (received.writerIndex() >= expectedSize) {
				readingSessions.remove(key);
				return received;
			}
			return null;
		}
	}
}