package net.dugged.cutelessmod.chunk_display;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CarpetPacketHandler implements IMessageHandler<CarpetPacket, IMessage> {
	@Override
	public IMessage onMessage(CarpetPacket message, MessageContext ctx) {
		int type = message.type;
		switch (type) {
			case CarpetPacket.CHUNK_LOGGER:
				Chunkdata.processPacket(message.data);
				break;
			case CarpetPacket.GUI_ALL_DATA:
			case CarpetPacket.RULE_REQUEST:
			case CarpetPacket.VILLAGE_MARKERS:
			case CarpetPacket.BOUNDINGBOX_MARKERS:
			case CarpetPacket.TICKRATE_CHANGES:
			case CarpetPacket.PISTON_UPDATES:
			case CarpetPacket.RANDOMTICK_DISPLAY:
			case CarpetPacket.CUSTOM_RECIPES:
				break;
		}
		return null;
	}
}
