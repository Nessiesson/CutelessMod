package net.dugged.cutelessmod.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.BlockMobSpawner;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockMobSpawner.class)
public abstract class MixinBlockMobSpawner extends Block {
	public MixinBlockMobSpawner(final Material material) {
		super(material);
	}

	@Inject(method = "getItem", at = @At("HEAD"), cancellable = true)
	private void onGetItem(final World world, final BlockPos pos, final IBlockState state, final CallbackInfoReturnable<ItemStack> cir) {
		cir.setReturnValue(super.getItem(world, pos, state));
	}
}
