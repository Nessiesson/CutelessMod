package net.dugged.cutelessmod.mixins;

import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net/minecraft/client/gui/GuiLanguage$List")
public abstract class MixinGuiLanguage_List {
	@Redirect(method = "elementClicked(IZII)V", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/client/FMLClientHandler;refreshResources([Lnet/minecraftforge/client/resource/IResourceType;)V", remap = false))
	private void onlyRefreshLanguage(final FMLClientHandler client, final IResourceType[] inclusion) {
		client.getClient().getLanguageManager().onResourceManagerReload(client.getClient().getResourceManager());
	}
}
