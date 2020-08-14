package net.dugged.cutelessmod.mixins;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGameOver.class)
public abstract class MixinGuiGameOver extends GuiScreen {
	@Inject(method = "updateScreen", at = @At("RETURN"))
	private void removeRespawnDelay(final CallbackInfo ci) {
		for (final GuiButton button : this.buttonList) {
			button.enabled = true;
		}
	}
}
