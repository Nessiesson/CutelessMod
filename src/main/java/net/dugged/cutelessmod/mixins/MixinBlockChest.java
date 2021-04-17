package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.dugged.cutelessmod.DoubleChestUtils;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;

@Mixin(BlockChest.class)
public abstract class MixinBlockChest extends BlockContainer {
	public MixinBlockChest(final Material material) {
		super(material);
	}

	@Inject(method = "<init>", at = @At("RETURN"))
	private void onInit(final BlockChest.Type chest, final CallbackInfo ci) {
		this.setDefaultState(this.blockState.getBaseState().withProperty(DoubleChestUtils.AABB, DoubleChestUtils.NONE));
	}

	@ModifyArg(method = "createBlockState", index = 1, at = @At(value = "INVOKE", target = "Lnet/minecraft/block/state/BlockStateContainer;<init>(Lnet/minecraft/block/Block;[Lnet/minecraft/block/properties/IProperty;)V"))
	private IProperty<?>[] onCreateBlockState(IProperty<?>[] properties) {
		properties = Arrays.copyOf(properties, properties.length + 1);
		properties[properties.length - 1] = DoubleChestUtils.AABB;
		return properties;
	}

	@Inject(method = "getRenderType", at = @At("HEAD"), cancellable = true)
	private void onGetRenderType(final IBlockState state, final CallbackInfoReturnable<EnumBlockRenderType> cir) {
		if (Configuration.chestWithoutTESR) {
			cir.setReturnValue(EnumBlockRenderType.MODEL);
		}
	}
}
