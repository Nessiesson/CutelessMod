package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.dugged.cutelessmod.ItemCounter;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityItem.class)
public abstract class MixinEntityItem extends Entity {
	public MixinEntityItem(final World world) {
		super(world);
	}

	@Shadow
	public abstract ItemStack getItem();

	@Redirect(method = "onUpdate", at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;isRemote:Z", opcode = Opcodes.GETFIELD, ordinal = 0))
	private boolean clientPushOutOfBlocks(final World world) {
		return !Configuration.smoothItemMovement && !Minecraft.getMinecraft().isSingleplayer();
	}

	@Inject(method = "onUpdate", at = @At(value = "HEAD"))
	private void itemCounter(CallbackInfo ci) {
		if (ItemCounter.checkPosition((int) posX, (int) posY, (int) posZ)) {
			ItemCounter.checkItem(getEntityId(), getItem());
		}
	}
}
