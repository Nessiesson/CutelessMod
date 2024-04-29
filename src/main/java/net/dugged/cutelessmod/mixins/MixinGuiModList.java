package net.dugged.cutelessmod.mixins;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.GuiModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(value = GuiModList.class)
public abstract class MixinGuiModList extends GuiScreen {
	@Shadow
	protected abstract void mouseClicked(int x, int y, int button) throws IOException;

	@Shadow
	protected abstract void actionPerformed(GuiButton button) throws IOException;

	@Inject(method = "initGui", at = @At("RETURN"))
	private void onGuiModListInit(final CallbackInfo ci) {
		for (final GuiButton button : this.buttonList) {
			if (button.id == 25) {
				try {
					this.actionPerformed(button);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
}
