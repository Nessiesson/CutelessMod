package net.dugged.cutelessmod.mixins;

import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiButtonImage.class)
public interface IGuiButtonImage {
	@Mutable
	@Accessor
	void setResourceLocation(ResourceLocation resourceLocation);

	@Mutable
	@Accessor
	void setXTexStart(int xTexStart);

	@Mutable
	@Accessor
	void setYDiffText(int yDiffText);
}
