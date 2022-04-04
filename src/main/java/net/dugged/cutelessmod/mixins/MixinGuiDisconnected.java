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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiDisconnected.class)
public abstract class MixinGuiDisconnected extends GuiScreen {
	@Shadow
	@Final
	private ITextComponent message;
	@Shadow
	@Final
	private GuiScreen parentScreen;

	@Unique
	private int countdownSeconds;
	@Unique
	private long ticks = 0L;

	@Inject(method = "initGui", at = @At("RETURN"))
	private void addButton(final CallbackInfo ci) {
		final int textHeight = this.fontRenderer.listFormattedStringToWidth(this.message.getFormattedText(), this.width - 50).size() * this.fontRenderer.FONT_HEIGHT;
		countdownSeconds = Configuration.reconnectTimer;
		if (CutelessMod.currentServer != null) {
			if (this.message.getUnformattedText().toLowerCase().contains("stopjoin")) {
				this.buttonList.add(new GuiButton(1, this.width / 2 - 100, Math.min(this.height / 2 + textHeight / 2 + this.fontRenderer.FONT_HEIGHT, this.height - 30) + 30, I18n.format("Reconnect forbidden")));
				this.buttonList.get(1).enabled = false;
			} else {
				this.buttonList.add(new GuiButton(1, this.width / 2 - 100, Math.min(this.height / 2 + textHeight / 2 + this.fontRenderer.FONT_HEIGHT, this.height - 30) + 30, I18n.format("Reconnect (" + this.countdownSeconds + ")")));
			}
		}
	}

	@Inject(method = "drawScreen", at = @At("HEAD"))
	public void updateStrings(final int mouseX, final int mouseY, final float partialTicks, final CallbackInfo ci) {
		try {
			if (CutelessMod.tickCounter > this.ticks && this.buttonList.get(1).enabled) {
				this.ticks = CutelessMod.tickCounter + 20;
				if (this.countdownSeconds <= 0 && this.buttonList.get(1).enabled) {
					if (CutelessMod.currentServer != null) {
						this.mc.displayGuiScreen(new GuiConnecting(this.parentScreen, this.mc, CutelessMod.currentServer));
					}
					return;
				}
				this.countdownSeconds--;
				this.buttonList.get(1).displayString = "Rejoin (" + this.countdownSeconds + ")";
			}
		} catch (final Exception ignored) {}
	}

	@Inject(method = "actionPerformed", at = @At("HEAD"))
	protected void actionPerformed(final GuiButton button, final CallbackInfo ci) {
		if (button.id == 1 && CutelessMod.currentServer != null) {
			this.mc.displayGuiScreen(new GuiConnecting(this.parentScreen, this.mc, CutelessMod.currentServer));
		}
	}
}
