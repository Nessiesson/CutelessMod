package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.dugged.cutelessmod.CutelessMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemCompass;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(GuiIngame.class)
public abstract class MixinGuiIngame extends Gui {

	private long lastTick = 0;

	@Inject(method = "setOverlayMessage(Ljava/lang/String;Z)V", at = @At("HEAD"), cancellable = true)
	private void parseDuggedMSPT(String message, final boolean animateColor, final CallbackInfo ci) {
		final String s = message.replaceAll("(\u00A7a|\u00A7r)", "");
		if (message.matches("\u00A7a\\d*\u00A7r") && StringUtils.isNumeric(s)) {
			CutelessMod.overlayTimer = 60;
			CutelessMod.mspt = Integer.parseInt(s);
			ci.cancel();
		}
	}

	@Inject(method = "updateTick", at = @At("RETURN"))
	public void saveChat(CallbackInfo ci) {
		final Minecraft mc = Minecraft.getMinecraft();
		// insane performance benefit by only calling every 10 ticks
		if (CutelessMod.tickCounter > lastTick && mc.player != null) {
			lastTick = CutelessMod.tickCounter + 10;
			final String currentServer = CutelessMod.currentServer != null ? CutelessMod.currentServer.serverIP : "SINGLEPLAYER";
			final GuiNewChat chat = mc.ingameGUI.getChatGUI();
			CutelessMod.tabCompleteHistory.put(currentServer, new ArrayList<>(chat.getSentMessages()));
			CutelessMod.chatHistory.put(currentServer, new ArrayList<>(((IGuiNewChat) chat).getChatLines()));
		}
	}

	@Inject(method = "renderHotbar", at = @At("RETURN"))
	public void renderCompassMenu(ScaledResolution scaledResolution, float partialTicks, CallbackInfo ci) {
		final Minecraft mc = Minecraft.getMinecraft();
		if (Configuration.worldeditCompass && mc.player.isCreative() && mc.player.getHeldItemMainhand().getItem() instanceof ItemCompass) {
			CutelessMod.guiCompass.renderTooltip(scaledResolution);
			CutelessMod.guiCompass.renderSelectedItem(scaledResolution);
		} else if (CutelessMod.guiCompass.isMenuActive()) {
			CutelessMod.guiCompass.exit();
		}
	}
}
