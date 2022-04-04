package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Mixin(ChunkGeneratorOverworld.class)
public abstract class MixinChunkGeneratorOverworld {
	@Shadow
	@Final
	private Random rand;
	private static final Path path = Paths.get("/home/log_" + UUID.randomUUID() + ".txt");
	private static boolean initialized = false;
	private static BufferedWriter writer;

	private void initialize() {
		try {
			Files.deleteIfExists(path);
			FileWriter file = new FileWriter(String.valueOf(path));
			writer = new BufferedWriter(file);
			writer.write("ChunkX ChunkZ | SpawnerPosX SpawnerPosY SpawnerPos Z | DungeonWidthX DungeonWidthZ\n\n");
		} catch (IOException e) {
		}
	}

	@Inject(method = "populate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/feature/WorldGenDungeons;generate(Lnet/minecraft/world/World;Ljava/util/Random;Lnet/minecraft/util/math/BlockPos;)Z", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
	private void saveDungeonsPos(final int x, final int z, final CallbackInfo ci, final int i, final int j, final BlockPos blockpos, final Biome biome, final long k, final long l, final boolean flag, final ChunkPos chunkpos, final int j2, final int i3, final int l3, final int l1) {
		if (Configuration.saveDungeonLocations && l3 > 80 && l3 < 240) {
			if (!initialized) {
				initialize();
				initialized = true;
			}
			BlockPos spawnerpos = new BlockPos(i, 0, j).add(i3, l3, l1);
			Random copiedRand = new Random(getSeed(rand));
			int widthX = copiedRand.nextInt(2) + 3;
			int widthZ = copiedRand.nextInt(2) + 3;
			try {
				writer.write(x + " " + z + " | " + spawnerpos.getX() + " " + spawnerpos.getY() + " " + spawnerpos.getZ() + " | " + widthX + " " + widthZ + "\n");
			} catch (IOException e) {
			}
		}
	}

	private static long getSeed(Random rand) {
		long seed = 0;
		try {
			Field field = Random.class.getDeclaredField("seed");
			field.setAccessible(true);
			AtomicLong scrambledSeed = (AtomicLong) field.get(rand);   //this needs to be XOR'd with 0x5DEECE66DL
			seed = scrambledSeed.get() ^ 0x5DEECE66DL;
		} catch (Exception e) {
			//catch
		}
		return seed;
	}
}