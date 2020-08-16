package net.dugged.cutelessmod.mixins;

import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiPlayerTabOverlay.class)
public interface IGuiPlayerTabOverlay {
	@Accessor()
	ITextComponent getFooter();
}
