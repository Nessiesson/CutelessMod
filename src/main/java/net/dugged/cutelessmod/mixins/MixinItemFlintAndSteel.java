package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockObserver;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemFlintAndSteel.class)
public class MixinItemFlintAndSteel extends Item {
	@Inject(method = "onItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z"))
	private void rightClickRocket(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ, CallbackInfoReturnable<EnumActionResult> cir) {
		IBlockState blockStateDown = worldIn.getBlockState(pos.down());
		if (Configuration.improveObserverFire && player.isCreative() && blockStateDown.getBlock() instanceof BlockObserver) {
			if (blockStateDown.getProperties().containsKey((BlockDirectional.FACING)) && blockStateDown.getProperties().get(BlockDirectional.FACING) == EnumFacing.UP) {
				final NetHandlerPlayClient connection = Minecraft.getMinecraft().getConnection();
				if (connection != null) {
					connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, EnumFacing.DOWN));
				}
			}
		}
	}
}