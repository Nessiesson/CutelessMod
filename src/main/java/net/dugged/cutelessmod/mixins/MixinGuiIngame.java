package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngame.class)
public abstract class MixinGuiIngame extends Gui {
	@Inject(method = "setOverlayMessage(Ljava/lang/String;Z)V", at = @At("HEAD"), cancellable = true)
	private void onIncomingMSPT(String message, final boolean animateColor, final CallbackInfo ci) {
		if (Configuration.showMSPTandTPSinTab) {
			ci.cancel();
			message = message.replaceAll("\u00A7[0-9a-fklmnor]", "");

			final int iMSPT = Integer.parseInt(message);
			final int iTPS = 1000 / Math.max(50, iMSPT);
			final ITextComponent base = new TextComponentString("");
			final ITextComponent tMSPT = new TextComponentString("MSPT: ");
			final ITextComponent tTPS = new TextComponentString("TPS: ");
			final ITextComponent MSPT = new TextComponentString(Integer.toString(iMSPT));
			final ITextComponent TPS = new TextComponentString(Integer.toString(iTPS));

			tMSPT.getStyle().setColor(TextFormatting.DARK_GRAY);
			tTPS.getStyle().setColor(TextFormatting.DARK_GRAY);
			MSPT.getStyle().setColor(this.cutelessmodReturnColourForMSPT(iMSPT));
			TPS.getStyle().setColor(TextFormatting.GRAY);

			base.appendSibling(tMSPT).appendSibling(MSPT).appendSibling(new TextComponentString(" ")).appendSibling(tTPS).appendSibling(TPS);
			Minecraft.getMinecraft().ingameGUI.getTabList().setFooter(base);
		}
	}

	@Unique
	private TextFormatting cutelessmodReturnColourForMSPT(final int mspt) {
		// not stolen from masa at all.
		if (mspt <= 40) {
			return TextFormatting.GREEN;
		} else if (mspt <= 45) {
			return TextFormatting.YELLOW;
		} else if (mspt <= 50) {
			return TextFormatting.GOLD;
		} else {
			return TextFormatting.RED;
		}
	}
}
