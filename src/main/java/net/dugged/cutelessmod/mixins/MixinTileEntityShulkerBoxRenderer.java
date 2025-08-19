package net.dugged.cutelessmod.mixins;

import net.minecraft.client.renderer.tileentity.TileEntityShulkerBoxRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TileEntityShulkerBoxRenderer.class)
public abstract class MixinTileEntityShulkerBoxRenderer {
	@Redirect(method = "render(Lnet/minecraft/tileentity/TileEntityShulkerBox;DDDFIF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;scale(FFF)V", ordinal = 2))
	private void cutelessmod$rescaleShulkerBox(final float x, final float y, final float z) {
		// noop
	}
}
