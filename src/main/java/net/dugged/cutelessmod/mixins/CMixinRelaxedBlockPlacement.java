package net.dugged.cutelessmod.mixins;

import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({BlockFenceGate.class, BlockPumpkin.class})
public abstract class CMixinRelaxedBlockPlacement extends BlockHorizontal {
	protected CMixinRelaxedBlockPlacement(final Material material) {
		super(material);
	}

	@Inject(method = "canPlaceBlockAt", at = @At("HEAD"), cancellable = true)
	void allowFenceGateMidair(final World world, final BlockPos pos, final CallbackInfoReturnable<Boolean> cir) {
		final EntityPlayerSP player = Minecraft.getMinecraft().player;
		if ((player != null && player.isCreative()) && super.canPlaceBlockAt(world, pos)) {
			cir.setReturnValue(true);
		}
	}
}
