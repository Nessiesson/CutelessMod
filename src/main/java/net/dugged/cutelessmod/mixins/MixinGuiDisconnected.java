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

import java.util.List;

@Mixin(GuiDisconnected.class)
public class  MixinGuiDisconnected extends GuiScreen {
	@Shadow
	@Final
	private ITextComponent message;
	@Shadow
	@Final
	private GuiScreen parentScreen;

	@Unique
	private int cutelessmodSeconds;
	@Unique
	private long cutelessmodTicks = 0L;

	@Inject(method = "initGui", at = @At("RETURN"))
	private void addButton(final CallbackInfo ci) {
		final List<String> msg = this.fontRenderer.listFormattedStringToWidth(this.message.getFormattedText(), this.width - 50);
		final int textHeight = msg.size() * this.fontRenderer.FONT_HEIGHT;
		cutelessmodSeconds = Configuration.reconnectTimer;
		if (CutelessMod.currentServer != null) {
			if (this.message.getUnformattedText().toLowerCase().contains("stopjoin")) {
				this.buttonList.add(new GuiButton(1, this.width / 2 - 100, Math.min(this.height / 2 + textHeight / 2 + this.fontRenderer.FONT_HEIGHT, this.height - 30) + 30, I18n.format("Reconnect forbidden")));
				this.buttonList.get(1).enabled = false;
			} else {
				this.buttonList.add(new GuiButton(1, this.width / 2 - 100, Math.min(this.height / 2 + textHeight / 2 + this.fontRenderer.FONT_HEIGHT, this.height - 30) + 30, I18n.format("Reconnect (" + this.cutelessmodSeconds + ")")));
			}
		}
	}

	@Inject(method = "drawScreen", at = @At("HEAD"))
	public void updateStrings(final int mouseX, final int mouseY, final float partialTicks, final CallbackInfo ci) {
		if (CutelessMod.tickCounter > this.cutelessmodTicks&& this.buttonList.get(1).enabled) {
			this.cutelessmodTicks = CutelessMod.tickCounter + 20;
			if (this.cutelessmodSeconds <= 0 && this.buttonList.get(1).enabled) {
				if (CutelessMod.currentServer != null) {
					this.mc.displayGuiScreen(new GuiConnecting(this.parentScreen, this.mc, CutelessMod.currentServer));
				}

				return;
			}

			this.cutelessmodSeconds--;
			if (this.buttonList.size() > 1) {
				this.buttonList.get(1).displayString = "Rejoin (" + this.cutelessmodSeconds + ")";
			}
		}
	}

	@Inject(method = "actionPerformed", at = @At("HEAD"))
	protected void actionPerformed(final GuiButton button, final CallbackInfo ci) {
		if (button.id == 1 && CutelessMod.currentServer != null) {
			this.mc.displayGuiScreen(new GuiConnecting(this.parentScreen, this.mc, CutelessMod.currentServer));
		}
	}
}
