package net.dugged.cutelessmod;

public class Reference {
	public static final String MODID = "@MODID@";
	public static final String NAME = "@MODNAME@";
	public static final String VERSION = "@VERSION@";
	public static final boolean isForge;

	static {
		boolean isForgeTmp;
		try {
			Class.forName("net.minecraftforge.fml.client.FMLClientHandler");
			isForgeTmp = true;
		} catch (final ClassNotFoundException e) {
			isForgeTmp = false;
		}

		isForge = isForgeTmp;
	}
}
