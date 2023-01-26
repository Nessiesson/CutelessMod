package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.dugged.cutelessmod.CutelessMod;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryPlayer.class)
public class MixinInventoryPlayer {

	@Shadow
	public int currentItem;

	private static int lastSlot = 0;

	// @ModifyConstant(method = "changeCurrentItem", constant = @Constant(intValue = 9), expect = 3)
	// public int modifyHotbar(int orig) {
	//	return 9 + 4;
	// }

	@Shadow
	public EntityPlayer player;

	@Inject(method = "changeCurrentItem", at = @At("RETURN"))
	public void trackCurrentSlot(int direction, CallbackInfo ci) {
		if (Configuration.extendedCreativeHotbar && player.isCreative()) {
			Minecraft mc = Minecraft.getMinecraft();
			if (direction > 0) {
				direction = 1;
			} else if (direction < 0) {
				direction = -1;
			}
			CutelessMod.hotbarSlot -= direction;
			while (CutelessMod.hotbarSlot < 0) {
				CutelessMod.hotbarSlot += 13;
			}
			while (CutelessMod.hotbarSlot >= 13) {
				CutelessMod.hotbarSlot -= 13;
			}
			int stableSlot = player.getPrimaryHand().opposite() == EnumHandSide.RIGHT ? 8 : 0;
			if (CutelessMod.hotbarSlot >= 9) {
				currentItem = stableSlot;
			} else {
				currentItem = CutelessMod.hotbarSlot;
			}
			if (MathHelper.abs(CutelessMod.hotbarSlot - lastSlot) > 0) {
				if ((CutelessMod.hotbarSlot == 0 && lastSlot == 12) || (CutelessMod.hotbarSlot == 12 && lastSlot == 0)) {
					mc.playerController.windowClick(0, 30, stableSlot, ClickType.SWAP, mc.player);
				} else if ((CutelessMod.hotbarSlot == 9 && lastSlot == 8) || (CutelessMod.hotbarSlot == 8 && lastSlot == 9)) {
					mc.playerController.windowClick(0, 27, stableSlot, ClickType.SWAP, mc.player);
				} else if (CutelessMod.hotbarSlot >= 9) {
					mc.playerController.windowClick(0, 27 + lastSlot % 9, stableSlot, ClickType.SWAP, mc.player);
					mc.playerController.windowClick(0, 27 + CutelessMod.hotbarSlot % 9, stableSlot, ClickType.SWAP, mc.player);
				}
				lastSlot = CutelessMod.hotbarSlot;
			}
		}
	}

	@Inject(method = "changeCurrentItem", at = @At("HEAD"), cancellable = true)
	private void onScrollInHotbar(final int direction, final CallbackInfo ci) {
		if (CutelessMod.zoomerKey.isKeyDown()) {
			ci.cancel();
		}
	}
}
