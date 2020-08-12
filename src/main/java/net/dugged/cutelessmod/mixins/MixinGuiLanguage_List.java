package net.dugged.cutelessmod.mixins;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.Redirect;

//TODO: Mixin @Group stuff, event?
@Mixin(targets = "net/minecraft/client/gui/GuiLanguage$List")
public abstract class MixinGuiLanguage_List {
	@Group(name = "CutelessModMixinGuiLanguage_List", max = 1)
	@Redirect(method = "elementClicked(IZII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;refreshResources()V"))
	private void onlyRefreshLanguageVanilla(Minecraft mc) {
		mc.getLanguageManager().onResourceManagerReload(mc.getResourceManager());
	}

	@Group(name = "CutelessModMixinGuiLanguage_List", max = 1)
	@Redirect(method = "elementClicked(IZII)V", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/client/FMLClientHandler;refreshResources([Lnet/minecraftforge/client/resource/IResourceType;)V", remap = false))
	private void onlyRefreshLanguage(FMLClientHandler client, IResourceType... inclusion) {
		client.getClient().getLanguageManager().onResourceManagerReload(client.getClient().getResourceManager());
	}
}
