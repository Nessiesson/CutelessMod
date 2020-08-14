package net.dugged.cutelessmod.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBarrier;
import net.minecraft.block.BlockCommandBlock;
import net.minecraft.block.BlockDragonEgg;
import net.minecraft.block.BlockStructure;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({BlockBarrier.class, BlockCommandBlock.class, BlockDragonEgg.class, BlockStructure.class})
public abstract class MixinExtraBlocksInCreativeMenu extends Block {
	protected MixinExtraBlocksInCreativeMenu(final Material material) {
		super(material);
	}

	@Inject(method = "<init>", at = @At("RETURN"))
	private void onInit(final CallbackInfo ci) {
		this.setCreativeTab(CreativeTabs.REDSTONE);
	}
}
