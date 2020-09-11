package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFirework;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemFirework.class)
public abstract class MixinItemFirework extends Item {
    @Inject(method = "onItemRightClick", at = @At(value = "HEAD"), cancellable = true)
    private void rightClickRocket(World worldIn, EntityPlayer playerIn, EnumHand handIn, CallbackInfoReturnable<ActionResult<ItemStack>> cir) {
        if (playerIn.getCooldownTracker().hasCooldown(this)) {
            cir.cancel();
        }
        if (Configuration.rocketCooldown) {
            playerIn.getCooldownTracker().setCooldown(this, 12);
        }
    }
}