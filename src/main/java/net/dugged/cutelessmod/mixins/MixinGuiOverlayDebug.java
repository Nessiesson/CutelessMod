package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.CutelessMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiOverlayDebug;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.List;

@Mixin(GuiOverlayDebug.class)
public abstract class MixinGuiOverlayDebug {
	@Shadow
	@Final
	private Minecraft mc;

	//TODO: Make sure this is accurate when possible
	@Inject(method = "call", at = @At("RETURN"))
	private void tpsDebugText(final CallbackInfoReturnable<List<String>> cir) {
		final double mspt = CutelessMod.mspt;
		final String currentLine = cir.getReturnValue().get(4);
		cir.getReturnValue().set(4, String.format("%s, mspt: \u2248%.1f, tps: \u2248%.1f", currentLine, mspt, 1000D / mspt));
	}

	//TODO: Ensure correct packet count
	@Inject(method = "call", at = @At("RETURN"))
	private void packetCount(final CallbackInfoReturnable<List<String>> cir) {
		final int rx_per_second = Arrays.stream(CutelessMod.receivedPackets).sum();
		final int tx_per_second = Arrays.stream(CutelessMod.sendPackets).sum();
		cir.getReturnValue().add(5, String.format("Networking packets: %drx/s %dtx/s", rx_per_second, tx_per_second));
	}

	@Inject(method = "call", at = @At("RETURN"))
	private void displayRegion(final CallbackInfoReturnable<List<String>> cir) {
		final Entity entity = this.mc.getRenderViewEntity();
		if (entity == null || entity.getCollisionBoundingBox() == null) {
			return;
		}

		final BlockPos pos = new BlockPos(entity.posX, entity.getCollisionBoundingBox().minY, entity.posZ);
		final String region = "r." + (pos.getX() >> 9) + "." + (pos.getZ() >> 9) + ".mca";
		cir.getReturnValue().add(11, String.format("Region: %s", region));
	}

	@Redirect(method = "call", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getVersion()Ljava/lang/String;"))
	private String pureVanilla(final Minecraft mc) {
		return "vanilla++";
	}
}
