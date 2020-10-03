package net.dugged.cutelessmod;

import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import javax.annotation.Nullable;
import java.util.Map;

@IFMLLoadingPlugin.MCVersion(ForgeVersion.mcVersion)
public class CutelessModLoadingPlugin implements IFMLLoadingPlugin {
	public CutelessModLoadingPlugin() {
		MixinBootstrap.init();
		Mixins.addConfiguration("mixins.cutelessmod.json");
		Mixins.addConfiguration("mixins.cutelessmod.clientcommands.json");
		Mixins.addConfiguration("mixins.forge.cutelessmod.json");
		Mixins.addConfiguration("mixins.liteloader.cutelessmod.json");
	}

	// @formatter:off
	@Override
	public String getAccessTransformerClass() {
		return null;
	}

	@Override
	public String[] getASMTransformerClass() {
		return null;
	}

	@Nullable
	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
	}
	// @formatter:on
}
