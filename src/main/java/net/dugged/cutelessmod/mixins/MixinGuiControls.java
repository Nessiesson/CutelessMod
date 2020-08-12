package net.dugged.cutelessmod.mixins;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiControls.class)
public abstract class MixinGuiControls {
	@Inject(method = "actionPerformed", at = @At("HEAD"), cancellable = true)
	private void resetKeybindsWhenHoldingShiftOnly(GuiButton button, CallbackInfo ci) {
		if (button.id == 201 && !GuiScreen.isShiftKeyDown()) {
			ci.cancel();
		}
	}
}
