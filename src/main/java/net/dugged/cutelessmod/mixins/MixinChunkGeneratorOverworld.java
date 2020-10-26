package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.dugged.cutelessmod.CutelessMod;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ChunkGeneratorOverworld.class)
public class MixinChunkGeneratorOverworld {
	@Inject(method = "populate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/feature/WorldGenDungeons;generate(Lnet/minecraft/world/World;Ljava/util/Random;Lnet/minecraft/util/math/BlockPos;)Z"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void saveDungeonsPos(final int x, final int z, final CallbackInfo ci, final int i, final int j, final BlockPos blockpos, final Biome biome, final long k, final long l, final boolean flag, final ChunkPos chunkpos, final int j2, final int i3, final int l3, final int l1) {
		if (Configuration.showDungeonLocations) {
			BlockPos spawnerpos = new BlockPos(i, 0, j).add(i3, l3, l1);
			if (!CutelessMod.dungeonPositions.containsKey(spawnerpos)) {
				System.out.println("Generated Spawner at: " + spawnerpos);
				CutelessMod.dungeonPositions.put(spawnerpos, chunkpos);
			}
		}
	}
}