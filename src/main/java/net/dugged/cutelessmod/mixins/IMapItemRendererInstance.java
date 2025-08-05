package net.dugged.cutelessmod.mixins;

import net.minecraft.client.gui.MapItemRenderer;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MapItemRenderer.Instance.class)
public interface IMapItemRendererInstance {
	@Mutable
	@Accessor
	ResourceLocation getLocation();
}
