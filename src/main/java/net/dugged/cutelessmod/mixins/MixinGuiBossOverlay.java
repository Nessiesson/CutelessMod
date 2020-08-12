package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.minecraft.client.gui.GuiBossOverlay;
import net.minecraft.client.gui.ScaledResolution;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuiBossOverlay.class)
public abstract class MixinGuiBossOverlay {
	@Redirect(method = "renderBossHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/ScaledResolution;getScaledHeight()I"))
	private int onlyOneBossBar(ScaledResolution scaledResolution) {
		return Configuration.showOneBossBar ? 36 : scaledResolution.getScaledHeight();
	}
}
