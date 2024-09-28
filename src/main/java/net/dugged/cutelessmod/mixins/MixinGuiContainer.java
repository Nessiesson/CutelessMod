package net.dugged.cutelessmod.mixins;

import net.minecraft.client.gui.inventory.GuiContainer;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiContainer.class)
public abstract class MixinGuiContainer {

	@Inject(method = "initGui", at = @At("HEAD"))
	private void enableRepeatedEvents(CallbackInfo ci) {
		Keyboard.enableRepeatEvents(true);
	}

	@Inject(method = "onGuiClosed", at = @At("HEAD"))
	private void disabledRepeatedEvents(CallbackInfo ci) {
		Keyboard.enableRepeatEvents(false);
	}
}
