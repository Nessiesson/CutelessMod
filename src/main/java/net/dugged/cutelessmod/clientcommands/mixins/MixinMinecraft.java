package net.dugged.cutelessmod.clientcommands.mixins;

import net.dugged.cutelessmod.clientcommands.Handler;
import net.dugged.cutelessmod.clientcommands.HandlerClone;
import net.dugged.cutelessmod.clientcommands.HandlerFill;
import net.dugged.cutelessmod.clientcommands.HandlerSetBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
	@Inject(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At(value = "RETURN"))
	public void onWorldLoad(WorldClient worldClientIn, String loadingMessage, CallbackInfo ci) {
		Handler.getGameruleStates();
		HandlerSetBlock.getGameruleStates();
		HandlerFill.getGameruleStates();
		HandlerClone.getGameruleStates();
	}
}
