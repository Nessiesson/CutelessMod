package net.dugged.cleancuteless.mixins;

import net.dugged.cleancuteless.Configuration;
import net.dugged.cleancuteless.CleanCuteless;
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
		if (Configuration.building.extendedCreativeHotbar && player.isCreative()) {
			Minecraft mc = Minecraft.getMinecraft();
			if (direction > 0) {
				direction = 1;
			} else if (direction < 0) {
				direction = -1;
			}
			CleanCuteless.hotbarSlot -= direction;
			while (CleanCuteless.hotbarSlot < 0) {
				CleanCuteless.hotbarSlot += 13;
			}
			while (CleanCuteless.hotbarSlot >= 13) {
				CleanCuteless.hotbarSlot -= 13;
			}
			int stableSlot = player.getPrimaryHand().opposite() == EnumHandSide.RIGHT ? 8 : 0;
			if (CleanCuteless.hotbarSlot >= 9) {
				currentItem = stableSlot;
			} else {
				currentItem = CleanCuteless.hotbarSlot;
			}
			if (MathHelper.abs(CleanCuteless.hotbarSlot - lastSlot) > 0) {
				if ((CleanCuteless.hotbarSlot == 0 && lastSlot == 12) || (CleanCuteless.hotbarSlot == 12 && lastSlot == 0)) {
					mc.playerController.windowClick(0, 30, stableSlot, ClickType.SWAP, mc.player);
				} else if ((CleanCuteless.hotbarSlot == 9 && lastSlot == 8) || (CleanCuteless.hotbarSlot == 8 && lastSlot == 9)) {
					mc.playerController.windowClick(0, 27, stableSlot, ClickType.SWAP, mc.player);
				} else if (CleanCuteless.hotbarSlot >= 9) {
					mc.playerController.windowClick(0, 27 + lastSlot % 9, stableSlot, ClickType.SWAP, mc.player);
					mc.playerController.windowClick(0, 27 + CleanCuteless.hotbarSlot % 9, stableSlot, ClickType.SWAP, mc.player);
				}
				lastSlot = CleanCuteless.hotbarSlot;
			}
		}
	}
}
