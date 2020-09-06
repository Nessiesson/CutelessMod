package net.dugged.cutelessmod.mixins;

import com.mojang.authlib.GameProfile;
import net.dugged.cutelessmod.Configuration;
import net.dugged.cutelessmod.CutelessMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.stats.StatBase;
import net.minecraft.world.World;
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

	@Inject(method = "addStat", at = @At("HEAD"))
	private void addStat(StatBase stat, int amount, CallbackInfo ci) {
		if (stat.statId.matches(CutelessMod.statPluginFilter)) {
			if (CutelessMod.statPlugin.isConnected()) {
				CutelessMod.statPlugin.sendStatIncrease(amount, false);
			}
		}
	}
}
