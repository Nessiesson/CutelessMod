package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.CutelessMod;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiOverlayDebug;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
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

	@Inject(method = "call", at = @At("RETURN"))
	private void packetCount(final CallbackInfoReturnable<List<String>> cir) {
		final int rx_per_second = Arrays.stream(CutelessMod.receivedPackets).sum();
		final int tx_per_second = Arrays.stream(CutelessMod.sendPackets).sum();
		cir.getReturnValue().add(5, String.format("Networking packets: %drx/s %dtx/s", rx_per_second, tx_per_second));
	}

	@Inject(method = "call", at = @At("RETURN"))
	private void displayRegion(final CallbackInfoReturnable<List<String>> cir) {
		final BlockPos pos = mc.player.getPosition();
		final String region = "r." + (pos.getX() >> 9) + "." + (pos.getZ() >> 9) + ".mca";
		cir.getReturnValue().add(11, String.format("Region: %s", region));
	}

	@Inject(method = "getDebugInfoRight", at = @At("RETURN"))
	private void addMetadata(final CallbackInfoReturnable<List<String>> cir) {
		if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK && mc.objectMouseOver.getBlockPos() != null) {
			final BlockPos blockpos = mc.objectMouseOver.getBlockPos();
			final IBlockState iblockstate = mc.world.getBlockState(blockpos);
			int i = 0;
			for (String s : cir.getReturnValue()) {
				if (s.startsWith("minecraft")) {
					break;
				}
				i++;
			}
			cir.getReturnValue().add(i + 1, "metadata: " + iblockstate.getBlock().getMetaFromState(iblockstate));
		}
	}

	@Redirect(method = "call", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getVersion()Ljava/lang/String;"))
	private String pureVanilla(final Minecraft mc) {
		return "vanilla++";
	}
}
