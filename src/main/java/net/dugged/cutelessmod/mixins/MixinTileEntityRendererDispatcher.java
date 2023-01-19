package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = TileEntityRendererDispatcher.class, priority = 999)
public abstract class MixinTileEntityRendererDispatcher {
	@Redirect(method = "render(Lnet/minecraft/tileentity/TileEntity;FI)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/tileentity/TileEntity;getDistanceSq(DDD)D"))
	private double alwaysRenderTileEntities(final TileEntity te, final double x, final double y, final double z) {
		return Configuration.alwaysRenderTileEntities ? 0D : te.getDistanceSq(x, y, z);
	}
}
