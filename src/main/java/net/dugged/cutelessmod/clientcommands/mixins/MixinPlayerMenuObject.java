package net.dugged.cutelessmod.clientcommands.mixins;

import net.dugged.cutelessmod.clientcommands.ClientCommandHandler;
import net.dugged.cutelessmod.clientcommands.worldedit.WorldEdit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.spectator.PlayerMenuObject;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerMenuObject.class)
public abstract class MixinPlayerMenuObject {

	@Inject(method = "selectItem", at = @At(value = "INVOKE"))
	private void onSendChat(SpectatorMenu menu, CallbackInfo ci) {
		ClientCommandHandler.instance.lastPosition.update(WorldEdit.playerPos(),
			Minecraft.getMinecraft().player.dimension);
	}
}
