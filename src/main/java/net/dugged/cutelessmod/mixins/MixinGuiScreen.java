package net.dugged.cutelessmod.mixins;

import com.google.common.base.Splitter;
import net.dugged.cutelessmod.Configuration;
import net.dugged.cutelessmod.DesktopApi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.URI;

@SuppressWarnings("RedundantCast")
@Mixin(GuiScreen.class)
public abstract class MixinGuiScreen {
	@Shadow
	public abstract void sendChatMessage(String msg, boolean addToChat);

	@Inject(method = "handleInput", at = {
			@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;handleKeyboardInput()V"),
			@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;handleMouseInput()V")},
			cancellable = true)
	private void mc31222(CallbackInfo ci) {
		if ((GuiScreen) (Object) this != Minecraft.getMinecraft().currentScreen) {
			ci.cancel();
		}
	}

	@Inject(method = "openWebLink", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;)V", remap = false))
	private void fixRobisShit(URI url, CallbackInfo ci) {
		DesktopApi.browse(url);
	}

	/**
	 * @author nessie
	 * @reason Using @Overwrite is the simplest way to do this.
	 */
	@Overwrite
	public void sendChatMessage(String msg) {
		for (String message : Splitter.fixedLength(256).split(msg)) {
			this.sendChatMessage(message, true);
		}
	}

	@Inject(method = "drawWorldBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;drawGradientRect(IIIIII)V"), cancellable = true)
	private void onDrawBackground(int tint, CallbackInfo ci) {
		if (!Configuration.showGuiBackGround) {
			ci.cancel();
		}
	}
}
