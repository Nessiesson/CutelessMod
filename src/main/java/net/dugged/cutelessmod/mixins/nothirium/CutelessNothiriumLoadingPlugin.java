package net.dugged.cutelessmod.mixins.nothirium;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class CutelessNothiriumLoadingPlugin implements IMixinConfigPlugin {
	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		try {
			Class.forName("meldexun.nothirium.mc.asm.NothiriumPlugin");
			return true;
		} catch (final ClassNotFoundException exception) {
			return false;
		}
	}

	// @formatter:off
	@Override public void postApply(final String targetClassName, final ClassNode targetClass, final String mixinClassName, final IMixinInfo mixinInfo) {}
	@Override public void preApply(final String targetClassName, final ClassNode targetClass, final String mixinClassName, final IMixinInfo mixinInfo) {}
	@Override public void acceptTargets(final Set<String> myTargets, final Set<String> otherTargets) {}
	@Override public String getRefMapperConfig() { return null; }
	@Override public List<String> getMixins() { return null; }
	@Override public void onLoad(final String mixinPackage) {}
	// @formatter:on
}
