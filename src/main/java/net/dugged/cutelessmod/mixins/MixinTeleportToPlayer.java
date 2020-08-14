package net.dugged.cutelessmod.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.spectator.categories.TeleportToPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.world.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TeleportToPlayer.class)
public abstract class MixinTeleportToPlayer {
	@Redirect(method = "<init>(Ljava/util/Collection;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/NetworkPlayerInfo;getGameType()Lnet/minecraft/world/GameType;"))
	private GameType mc125157(final NetworkPlayerInfo info) {
		return info.getGameProfile().getName().equals(Minecraft.getMinecraft().getSession().getUsername()) ? info.getGameType() : GameType.NOT_SET;
	}
}
