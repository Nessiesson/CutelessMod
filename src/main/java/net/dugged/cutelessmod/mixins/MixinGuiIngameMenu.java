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

import java.util.ArrayList;

@Mixin(GuiIngameMenu.class)
public abstract class MixinGuiIngameMenu extends GuiScreen {
	@Inject(method = "updateScreen", at = @At("HEAD"))
	public void addReloadButton(final CallbackInfo ci) {
		if (!this.mc.isIntegratedServerRunning()) {
			CutelessMod.currentServer = this.mc.getCurrentServerData();
			if (GuiScreen.isShiftKeyDown()) {
				(this.buttonList.get(0)).displayString = I18n.format("menu.relog");
			} else
				(this.buttonList.get(0)).displayString = I18n.format("menu.disconnect");
		}
	}

	@Inject(method = "actionPerformed", at = @At("HEAD"))
	public void saveChat(final GuiButton button, final CallbackInfo ci) {
		final String currentServer = CutelessMod.currentServer != null ? CutelessMod.currentServer.serverIP : "SINGLEPLAYER";
		CutelessMod.tabCompleteHistory.put(currentServer, new ArrayList<>(this.mc.ingameGUI.getChatGUI().getSentMessages()));
		CutelessMod.chatHistory.put(currentServer, new ArrayList<>(((IGuiNewChat) this.mc.ingameGUI.getChatGUI()).getChatLines()));
	}

	@Inject(method = "actionPerformed", at = @At("RETURN"))
	public void handleRelogButton(final GuiButton button, final CallbackInfo ci) {
		if (!this.mc.isIntegratedServerRunning() && GuiScreen.isShiftKeyDown() && button.id == 1) {
			this.mc.displayGuiScreen(new GuiConnecting(this, this.mc, CutelessMod.currentServer));
		}
	}
}
