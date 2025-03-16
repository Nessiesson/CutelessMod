package net.dugged.cutelessmod.clientcommands.mixins;

import net.dugged.cutelessmod.clientcommands.ClientCommandHandler;
import net.dugged.cutelessmod.clientcommands.CommandUndo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiScreen.class)
public abstract class MixinGuiScreen {

	@Inject(method = "sendChatMessage(Ljava/lang/String;Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;sendChatMessage(Ljava/lang/String;)V"), cancellable = true)
	private void addExecute(String msg, boolean addToChat, CallbackInfo ci) {
		if (ClientCommandHandler.instance.executeCommand(Minecraft.getMinecraft().player, msg)
			!= 0) {
			ci.cancel();
		}
		if (msg.startsWith("/") && CommandUndo.saveHistory(msg)) {
			ci.cancel();
		}
	}
}