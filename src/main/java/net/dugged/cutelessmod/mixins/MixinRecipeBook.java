package net.dugged.cutelessmod.mixins;

import net.minecraft.stats.RecipeBook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RecipeBook.class)
public abstract class MixinRecipeBook {
	@Shadow
	protected boolean isFilteringCraftable;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void onInit(CallbackInfo ci) {
		this.isFilteringCraftable = true;
	}
}
