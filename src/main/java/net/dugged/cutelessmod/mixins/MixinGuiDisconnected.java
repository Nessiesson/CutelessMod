package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.dugged.cutelessmod.CutelessMod;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GuiDisconnected.class)
public class MixinGuiDisconnected extends GuiScreen {
	@Shadow
	@Final
	private ITextComponent message;
	@Shadow
	@Final
	private GuiScreen parentScreen;

	private int seconds;
	private long ticks = 0;

	@Inject(method = "initGui", at = @At("RETURN"))
	private void addButton(CallbackInfo ci) {
		List<String> multilineMessage = this.fontRenderer.listFormattedStringToWidth(this.message.getFormattedText(), this.width - 50);
		int textHeight = multilineMessage.size() * this.fontRenderer.FONT_HEIGHT;
		seconds = Configuration.reconnectTimer;
		if (CutelessMod.currentServer != null) {
			this.buttonList.add(new GuiButton(1, this.width / 2 - 100, Math.min(this.height / 2 + textHeight / 2 + this.fontRenderer.FONT_HEIGHT, this.height - 30) + 30, I18n.format("Reconnect (" + this.seconds + ")")));
		}
	}

	@Inject(method = "drawScreen", at = @At("HEAD"))
	public void updateStrings(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
		if (CutelessMod.tickCounter > ticks) {
			ticks = CutelessMod.tickCounter + 20;
			if (seconds <= 0) {
				if (CutelessMod.currentServer != null) {
					this.mc.displayGuiScreen(new GuiConnecting(this.parentScreen, this.mc, CutelessMod.currentServer));
				}
				return;
			}
			seconds--;
			if (this.buttonList.size() > 1) {
				this.buttonList.get(1).displayString = "Rejoin (" + this.seconds + ")";
			}
		}
	}

	@Inject(method = "actionPerformed", at = @At("HEAD"))
	protected void actionPerformed(GuiButton button, CallbackInfo ci) {
		if (button.id == 1 && CutelessMod.currentServer != null) {
			this.mc.displayGuiScreen(new GuiConnecting(this.parentScreen, this.mc, CutelessMod.currentServer));
		}
	}
}
