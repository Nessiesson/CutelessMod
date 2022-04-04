package net.dugged.cutelessmod.mixins;

import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockFenceGate.class)
public abstract class MixinBlockFenceGate extends BlockHorizontal {

	protected MixinBlockFenceGate(Material materialIn) {
		super(materialIn);
	}

	@Inject(method = "canPlaceBlockAt", at = @At("HEAD"), cancellable = true)
	void allowFenceGateMidair(World worldIn, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		if (Minecraft.getMinecraft().player.isCreative() && super.canPlaceBlockAt(worldIn, pos)) {
			cir.setReturnValue(true);
		}
	}
}
