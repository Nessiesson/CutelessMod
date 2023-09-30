package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.dugged.cutelessmod.CutelessMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.ISnooperInfo;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.RayTraceResult;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft implements IThreadListener, ISnooperInfo {
	@Shadow
	public EntityPlayerSP player;
	@Shadow
	public PlayerControllerMP playerController;
	@Shadow
	public RayTraceResult objectMouseOver;
	@Shadow
	private int rightClickDelayTimer;

	@Redirect(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/IReloadableResourceManager;registerReloadListener(Lnet/minecraft/client/resources/IResourceManagerReloadListener;)V", ordinal = 0), slice = @Slice(from = @At(value = "NEW", args = "class=net/minecraft/client/audio/SoundHandler")))
	private void cuteless$conditionallyRegisterSounds(final IReloadableResourceManager instance, final IResourceManagerReloadListener listener) {
		if (CutelessMod.enableSounds) {
			instance.registerReloadListener(listener);
		}
	}

	@Inject(method = "dispatchKeypresses()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/GameSettings;setOptionValue(Lnet/minecraft/client/settings/GameSettings$Options;I)V"), cancellable = true)
	private void onNarratorKeypress(CallbackInfo ci) {
		ci.cancel();
	}

	@Inject(method = "rightClickMouse", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;rightClickDelayTimer:I", opcode = Opcodes.PUTFIELD, ordinal = 0, shift = At.Shift.AFTER))
	private void onRightClick(CallbackInfo ci) {
		this.rightClickDelayTimer = Configuration.speedyPlace;
	}

	@ModifyArg(method = "runTickMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;clamp(FFF)F"), index = 2)
	private float allowFasterSpectatorFlight(float value) {
		return (float) Configuration.spectatorMaxSpeed;
	}

	@Inject(method = "middleClickMouse", at = @At("RETURN"))
	private void onMiddleClickMouse(final CallbackInfo ci) {
		if (Configuration.alwaysPickBlockMaxStack && this.player.isCreative() && this.objectMouseOver != null && this.objectMouseOver.typeOfHit != RayTraceResult.Type.MISS) {
			final InventoryPlayer inv = this.player.inventory;
			final ItemStack currentItem = inv.getCurrentItem();
			currentItem.setCount(currentItem.getMaxStackSize());
			inv.setPickedItemStack(currentItem);
			this.playerController.sendSlotPacket(this.player.getHeldItem(EnumHand.MAIN_HAND), 36 + inv.currentItem);
		}
	}

	@Redirect(method = "runTickMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/InventoryPlayer;changeCurrentItem(I)V"))
	private void scrollCompassMenu(InventoryPlayer inventoryPlayer, int direction) {
		if (CutelessMod.guiCompass.isMenuActive()) {
			CutelessMod.guiCompass.onMouseScroll(direction);
		} else {
			player.inventory.changeCurrentItem(direction);
		}
	}

	@Redirect(method = "runTickMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/KeyBinding;onTick(I)V"))
	private void clickCompassMenu(int keyCode) {
		if (CutelessMod.guiCompass.isMenuActive() && (keyCode + 100) == 2) {
			CutelessMod.guiCompass.onMiddleClick();
		} else {
			KeyBinding.onTick(keyCode);
		}
	}
}
