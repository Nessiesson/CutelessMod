package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.CutelessMod;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.resources.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngameMenu.class)
public abstract class MixinGuiIngameMenu extends GuiScreen {
	@Inject(method = "updateScreen", at = @At("HEAD"))
	public void addReloadButton(final CallbackInfo ci) {
		if (!this.mc.isIntegratedServerRunning()) {
			CutelessMod.currentServer = this.mc.getCurrentServerData();
			if (GuiScreen.isShiftKeyDown()) {
				(this.buttonList.get(0)).displayString = I18n.format("text.cutelessmod.relog");
			} else
				(this.buttonList.get(0)).displayString = I18n.format("menu.disconnect");
		}
	}

	@Inject(method = "actionPerformed", at = @At("RETURN"))
	public void handleRelogButton(final GuiButton button, final CallbackInfo ci) {
		if (!this.mc.isIntegratedServerRunning() && GuiScreen.isShiftKeyDown() && button.id == 1) {
			this.mc.displayGuiScreen(new GuiConnecting(this, this.mc, CutelessMod.currentServer));
		}
	}
}
