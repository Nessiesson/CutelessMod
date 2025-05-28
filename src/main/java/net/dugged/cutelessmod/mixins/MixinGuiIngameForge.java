package net.dugged.cutelessmod.mixins;

import net.minecraftforge.client.GuiIngameForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = GuiIngameForge.class, remap = false)
public abstract class MixinGuiIngameForge {
	@ModifyVariable(method = "renderRecordOverlay", ordinal = 2, index = 5, at = @At(value = "LOAD", ordinal = 1))
	private int cutelessmod$fixWeirdBlinkyTextForgeWhyDoYouDoThis(final int opacity) {
		return opacity > 8 ? opacity : 0;
	}
}
