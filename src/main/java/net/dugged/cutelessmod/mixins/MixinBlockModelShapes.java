package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.init.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.stream.IntStream;

@Mixin(BlockModelShapes.class)
public abstract class MixinBlockModelShapes {
	@ModifyVariable(method = "registerBuiltInBlocks", at = @At("HEAD"), argsOnly = true)
	private Block[] removeSpecialChestRenderer(final Block[] blocks) {
		if (Configuration.chestWithoutTESR) {
			IntStream.range(0, blocks.length).filter(i -> blocks[i] == Blocks.CHEST || blocks[i] == Blocks.TRAPPED_CHEST).forEach(i -> blocks[i] = Blocks.AIR);
		}

		return blocks;
	}
}
