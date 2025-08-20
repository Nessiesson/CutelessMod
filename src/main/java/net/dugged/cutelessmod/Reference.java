package net.dugged.cutelessmod;

import java.util.function.Supplier;

public class Reference {
	public static final String MODID = "cutelessmod";
	public static final String NAME = "CutelessMod";
	public static final String VERSION = "@VERSION@";
	public static final boolean isForge = ((Supplier<Boolean>) () -> {
		try {
			Class.forName("net.minecraftforge.fml.client.FMLClientHandler");
			return true;
		} catch (final ClassNotFoundException e) {
			return false;
		}
	}).get();
}
