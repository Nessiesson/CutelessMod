package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.CutelessMod;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngame.class)
public abstract class MixinGuiIngame extends Gui {
	@Inject(method = "setOverlayMessage(Ljava/lang/String;Z)V", at = @At("HEAD"), cancellable = true)
	private void parseDuggedMSPT(String message, final boolean animateColor, final CallbackInfo ci) {
		final String s = message.replaceAll("(\u00A7a|\u00A7r)", "");
		if (message.matches("\u00A7a\\d*\u00A7r") && StringUtils.isNumeric(s)) {
			CutelessMod.overlayTimer = 60;
			CutelessMod.mspt = Integer.parseInt(s);
			ci.cancel();
		}
	}
}
