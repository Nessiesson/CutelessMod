package net.dugged.cutelessmod.mixins;

import net.minecraft.client.gui.ServerListEntryNormal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.concurrent.ThreadPoolExecutor;

@Mixin(ServerListEntryNormal.class)
public interface IServerListEntryNormal {
	@Accessor("EXECUTOR")
	static ThreadPoolExecutor getExecutor() {
		throw new UnsupportedOperationException();
	}
}
