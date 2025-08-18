package net.dugged.cutelessmod.mixins;

import com.mojang.authlib.GameProfile;
import net.dugged.cutelessmod.Configuration;
import net.dugged.cutelessmod.CutelessMod;
import net.dugged.cutelessmod.CutelessModUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.stats.StatBase;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer {
	public MixinEntityPlayerSP(final World world, final GameProfile profile) {
		super(world, profile);
	}

	@Inject(method = "canUseCommand", at = @At("HEAD"), cancellable = true)
	private void overrideCommandPermissions(final int perm, final String command, final CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(true);
	}

	@Redirect(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;doesGuiPauseGame()Z"))
	private boolean mc2071(final GuiScreen gui) {
		return true;
	}

	@Inject(method = "closeScreen", at = @At("HEAD"))
	private void onCloseScreen(final CallbackInfo ci) {
		final Container container = this.openContainer;
		if (container instanceof ContainerMerchant) {
			final Minecraft mc = Minecraft.getMinecraft();
			mc.playerController.windowClick(container.windowId, 0, 1, ClickType.QUICK_MOVE, mc.player);
			mc.playerController.windowClick(container.windowId, 1, 1, ClickType.QUICK_MOVE, mc.player);
		}
	}

	// By Earthcomputer.
	@Inject(method = "onLivingUpdate", at = @At(value = "FIELD", target = "Lnet/minecraft/network/play/client/CPacketEntityAction$Action;START_FALL_FLYING:Lnet/minecraft/network/play/client/CPacketEntityAction$Action;"))
	public void onDeployElytra(final CallbackInfo ci) {
		if (Configuration.elytraFix) {
			this.setFlag(7, true);
		}
	}

	@Inject(method = "onLivingUpdate", at = @At("HEAD"))
	private void enableElytraCancelation(final CallbackInfo ci) {
		if (Configuration.elytraCancellation && this.getFlag(7) && GuiScreen.isShiftKeyDown() && Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			this.setFlag(7, false);
		}
	}

	@Inject(method = "addStat", at = @At("HEAD"))
	private void addStat(StatBase stat, int amount, CallbackInfo ci) {
		if (stat != null && stat.statId != null && stat.statId.matches(CutelessMod.statPluginFilter)) {
			if (CutelessMod.statPlugin.isConnected()) {
				CutelessMod.statPlugin.sendStatIncrease(amount, false);
			}
		}
	}

	@Override
	public void setVelocity(final double x, final double y, final double z) {
		if (Configuration.showDamageTilt) {
			final float result = (float) (-MathHelper.atan2(this.motionZ - z, this.motionX - x) * (180D / Math.PI) - this.rotationYaw);
			if (Float.isFinite(result)) {
				this.attackedAtYaw = result;
			}
		}

		super.setVelocity(x, y, z);
	}
}
