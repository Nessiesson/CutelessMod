package net.dugged.cutelessmod.mixins;

import net.minecraft.client.gui.MapItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.world.storage.MapData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(MapItemRenderer.class)
public interface IMapItemRenderer {
	@Mutable
	@Accessor
	TextureManager getTextureManager();

	@Invoker("getMapRendererInstance")
	MapItemRenderer.Instance getInstance(MapData data);
}
