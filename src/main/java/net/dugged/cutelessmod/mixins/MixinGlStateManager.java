package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.minecraft.client.renderer.GlStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(GlStateManager.class)
public class MixinGlStateManager {
    @ModifyVariable(method = "setFogDensity", at = @At("HEAD"), argsOnly = true)
    private static float adjustFogDensity(float fogDensity) {
        // In vanilla code, this method is only called with fog density = 2F when in lava.
        return fogDensity == 2F && Configuration.showClearLava ? 0F : fogDensity;
    }
}
