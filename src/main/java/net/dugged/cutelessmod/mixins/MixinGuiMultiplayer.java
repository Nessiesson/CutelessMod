
package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.dugged.cutelessmod.CutelessMod;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.ServerSelectionList;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.ServerPinger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiMultiplayer.class)
public abstract class MixinGuiMultiplayer {
	@Shadow
	@Final
	private ServerPinger oldServerPinger;
	@Shadow
	private ServerList savedServerList;
	@Shadow
	private ServerSelectionList serverListSelector;
	@Unique
	private int cutelessmodTick = 0;

	@Inject(method = "updateScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ServerPinger;pingPendingNetworks()V"))
	private void dynamicListUpdates(CallbackInfo ci) {
		if (Configuration.dynamicServerListUpdates) {
			final int visibleSlots = (this.serverListSelector.bottom - this.serverListSelector.top) / this.serverListSelector.getSlotHeight();
			final int startIndex = this.serverListSelector.getAmountScrolled() / this.serverListSelector.getSlotHeight();
			if (++cutelessmodTick >= 300) {
				cutelessmodTick = 0;
				for (int j = startIndex; j <= visibleSlots; j++) {
					try {
						this.oldServerPinger.ping(this.savedServerList.getServerData(j));
					} catch (Exception ignored) {
					}
				}

				this.serverListSelector.updateOnlineServers(this.savedServerList);
			}
		}
	}

	@Inject(method = "connectToServer", at = @At(value = "HEAD"))
	private void updateServerData(ServerData server, CallbackInfo ci) {
		CutelessMod.currentServer = server;
	}
}