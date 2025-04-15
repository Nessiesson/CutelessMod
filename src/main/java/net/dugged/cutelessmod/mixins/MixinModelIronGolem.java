package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.minecraft.client.model.ModelIronGolem;
import net.minecraft.client.model.ModelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ModelIronGolem.class)
public abstract class MixinModelIronGolem {
	@Redirect(method = "<init>(FF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelRenderer;addBox(FFFIIIF)V", ordinal = 1))
	private void cutelessmod$hideIronGolemNose(final ModelRenderer instance, final float offX, final float offY, final float offZ, final int width, final int height, final int depth, final float scaleFactor) {
		if (Configuration.showIronGolemNose) {
			instance.addBox(offX, offY, offZ, width, height, depth, scaleFactor);
		}
	}
}
