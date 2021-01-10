package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiMainMenu.class)
public class MixinGuiMainMenu extends GuiScreen {
	@Shadow(remap = false)
	private GuiButton modButton;

	@Inject(method = "addSingleplayerMultiplayerButtons", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiMainMenu;addButton(Lnet/minecraft/client/gui/GuiButton;)Lnet/minecraft/client/gui/GuiButton;"), cancellable = true)
	private void changeButtons(int p_73969_1_, int p_73969_2_, CallbackInfo ci) {
		if (Configuration.disableRealmsButton) {
			buttonList.add(modButton = new GuiButton(6, this.width / 2 - 100, p_73969_1_ + p_73969_2_ * 2, I18n.format("fml.menu.mods")));
			ci.cancel();
		}
	}
}
