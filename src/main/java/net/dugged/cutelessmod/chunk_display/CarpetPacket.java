package net.dugged.cutelessmod.chunk_display;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class CarpetPacket implements IMessage {
	public static final int GUI_ALL_DATA = 0;
	public static final int RULE_REQUEST = 1;
	public static final int VILLAGE_MARKERS = 2;
	public static final int BOUNDINGBOX_MARKERS = 3;
	public static final int TICKRATE_CHANGES = 4;
	public static final int CHUNK_LOGGER = 5;
	public static final int PISTON_UPDATES = 6;
	public static final int RANDOMTICK_DISPLAY = 7;
	public static final int CUSTOM_RECIPES = 8;
	public int type;
	public PacketBuffer data;

	public CarpetPacket() {
	}

	public CarpetPacket(int type, PacketBuffer buf) {
		this.type = type;
		this.data = buf;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(type);
		buf.writeBytes(data);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		PacketBuffer buffer = PacketSplitter.receive(CarpetPluginChannel.CARPET_CHANNEL_NAME, new PacketBuffer(buf));
		if (buffer != null) {
			type = buffer.readInt();
			data = buffer;
		}
	}
}
