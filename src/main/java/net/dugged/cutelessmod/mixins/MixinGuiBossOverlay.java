package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.dugged.cutelessmod.ItemCounter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiBossOverlay;
import net.minecraft.client.gui.ScaledResolution;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiBossOverlay.class)
public abstract class MixinGuiBossOverlay {
	@Redirect(method = "renderBossHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/ScaledResolution;getScaledHeight()I"))
	private int onlyOneBossBar(final ScaledResolution resolution) {
		return Configuration.showOneBossBar ? 36 : resolution.getScaledHeight();
	}

	@Inject(method = "renderBossHealth", at = @At(value = "HEAD"))
	public void renderItemCounterGui(CallbackInfo ci) {
		final Minecraft mc = Minecraft.getMinecraft();
		if (!mc.gameSettings.showDebugInfo) {
			ItemCounter.renderGui(mc.getRenderPartialTicks());
		}
	}
}
