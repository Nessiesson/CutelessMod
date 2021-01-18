package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.util.EnumHandSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {

	@Shadow
	protected abstract void transformSideFirstPerson(EnumHandSide hand, float p_187459_2_);

	@Redirect(method = "renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemRenderer;transformSideFirstPerson(Lnet/minecraft/util/EnumHandSide;F)V"))
	public void disableItemSwapAnimation(ItemRenderer itemRenderer, EnumHandSide hand, float p_187459_2_) {
		if (!Configuration.showHandChangeAnimation) {
			transformSideFirstPerson(hand, 0F);
		} else {
			transformSideFirstPerson(hand, p_187459_2_);
		}
	}
}
