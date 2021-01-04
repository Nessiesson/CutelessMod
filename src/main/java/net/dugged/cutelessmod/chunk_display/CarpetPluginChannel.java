package net.dugged.cutelessmod.chunk_display;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class CarpetPluginChannel {
	public static final String CARPET_CHANNEL_NAME = "carpet:client";
	public static SimpleNetworkWrapper CARPET_CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(CARPET_CHANNEL_NAME);

	public CarpetPluginChannel() {
		CARPET_CHANNEL.registerMessage(CarpetPacketHandler.class, CarpetPacket.class, 0, Side.CLIENT);
	}
}
