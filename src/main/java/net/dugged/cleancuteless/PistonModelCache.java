package net.dugged.cleancuteless;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Caches the tessellated vertices of moving piston models.
 *
 * <p>A piston animation only translates a fixed model for a couple of ticks, yet the vanilla
 * tile entity renderer re-runs the whole block model pipeline (including Forge's smooth
 * lighting pass) for every visible piston on every frame. The raw vertex data produced by that
 * pipeline is identical for a given position/block state, so we keep the quads and replay them
 * with the current translation instead.</p>
 */
public final class PistonModelCache {
	// Large enough to hold every distinct (position, state) pair in a typical piston contraption.
	private static final int MAX_ENTRIES = 16384;

	private static final Cache<Key, int[][]> CACHE = CacheBuilder.newBuilder()
		.maximumSize(MAX_ENTRIES)
		.expireAfterWrite(10, TimeUnit.SECONDS)
		.build();

	private PistonModelCache() {
	}

	public static void clear() {
		CACHE.invalidateAll();
	}

	/**
	 * Replays a cached model into {@code buffer}. The buffer's current translation is applied by
	 * {@link BufferBuilder#putPosition(double, double, double)}.
	 *
	 * @return true when a cached model was emitted
	 */
	public static boolean emit(final BufferBuilder buffer, final World world, final BlockPos pos, final IBlockState state, final boolean checkSides) {
		final int light = world.getCombinedLight(pos, 0);
		final Key key = new Key(world, pos.toImmutable(), state, checkSides, light);
		final int[][] quads = CACHE.getIfPresent(key);
		if (quads == null) {
			return false;
		}
		for (final int[] quad : quads) {
			buffer.addVertexData(quad);
			buffer.putPosition(0D, 0D, 0D); // applies the buffer's current translation
		}
		return true;
	}

	/** Stores the vertices produced by a single renderModel call. */
	public static void store(final BufferBuilder buffer, final World world, final BlockPos pos, final IBlockState state, final boolean checkSides, final int[] raw) {
		if (raw == null || raw.length == 0) {
			return;
		}

		final int stride = DefaultVertexFormats.BLOCK.getIntegerSize();
		final int quadSize = stride * 4; // baked quads always consist of four vertices
		if (raw.length % quadSize != 0) {
			return;
		}

		final int[][] quads = new int[raw.length / quadSize][quadSize];
		for (int i = 0; i < quads.length; i++) {
			System.arraycopy(raw, i * quadSize, quads[i], 0, quadSize);
		}

		final int light = world.getCombinedLight(pos, 0);
		final Key key = new Key(world, pos.toImmutable(), state, checkSides, light);
		CACHE.put(key, quads);
	}

	private static final class Key {
		private final World world;
		private final BlockPos pos;
		private final IBlockState state;
		private final boolean checkSides;
		private final int light;

		private Key(final World world, final BlockPos pos, final IBlockState state, final boolean checkSides, final int light) {
			this.world = world;
			this.pos = pos;
			this.state = state;
			this.checkSides = checkSides;
			this.light = light;
		}

		@Override
		public boolean equals(final Object other) {
			if (this == other) {
				return true;
			}
			if (!(other instanceof Key)) {
				return false;
			}
			final Key key = (Key) other;
			return this.world == key.world && this.state == key.state && this.checkSides == key.checkSides
				&& this.light == key.light && this.pos.equals(key.pos);
		}

		@Override
		public int hashCode() {
			int hash = System.identityHashCode(this.world);
			hash = 31 * hash + this.pos.hashCode();
			hash = 31 * hash + System.identityHashCode(this.state);
			hash = 31 * hash + (this.checkSides ? 1231 : 1237);
			return 31 * hash + this.light;
		}
	}
}
