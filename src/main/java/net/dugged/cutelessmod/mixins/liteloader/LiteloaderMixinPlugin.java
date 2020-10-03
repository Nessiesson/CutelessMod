package net.dugged.cutelessmod.mixins.liteloader;

import net.dugged.cutelessmod.Reference;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class LiteloaderMixinPlugin implements IMixinConfigPlugin {
	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		return !Reference.isForge;
	}

	// @formatter:off
	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public List<String> getMixins() {
		return null;
	}

	@Override
	public void onLoad(String mixinPackage) {
	}
	// @formatter:on
}
