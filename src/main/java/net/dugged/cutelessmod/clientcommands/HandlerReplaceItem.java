package net.dugged.cutelessmod.clientcommands;

import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HandlerReplaceItem extends Handler {
	private static final int COMMANDS_EXECUTED_PER_TICK = 512; // Minimum 2
	public static boolean replaceitemPermission = false;
	private final List<BlockPos> containerPositions = new ArrayList<>();
	private final Map<BlockPos, SimpleContainer> containers = new LinkedHashMap<>();
	private final int blockCount = 0;

	public HandlerReplaceItem(World worldIn) {
		super(worldIn);
	}

	public static void getCommandPermission() {
		if (mc.player != null && mc.player.connection != null) {
			replaceitemPermission = false;
			mc.player.connection.sendPacket(new CPacketTabComplete("/replaceite", null, false));
		}
	}

	public void fillContainer(BlockPos pos, ItemStack stack) {
		int slotCount = ((IInventory) world.getTileEntity(pos)).getSizeInventory();
		containerPositions.add(pos);
		containers.put(pos, new SimpleContainer(world.getBlockState(pos), stack, slotCount));
		totalCount += slotCount;
		affectedBlocks++;
	}

	synchronized public void tick() {
		super.tick();
		if (containerPositions.size() > 0) {
			final int handlerCount = ClientCommandHandler.instance.countHandlerType(HandlerReplaceItem.class);
			int commandsExecuted = 0;
			while (containerPositions.size() > 0 && commandsExecuted < (COMMANDS_EXECUTED_PER_TICK / handlerCount)) {
				final BlockPos pos = containerPositions.get(containerPositions.size() - 1);
				SimpleContainer container = containers.get(pos);
				for (int slot = 0; slot < container.slotCount; slot++) {
					if (commandsExecuted >= (COMMANDS_EXECUTED_PER_TICK / handlerCount)) {
						container.slot = slot;
						containers.put(pos, container);
						return;
					}
					currentCount++;
					if (sendReplaceItemCommand(pos, container.stack, slot)) {
						commandsExecuted++;
					}
				}
				containerPositions.remove(containerPositions.size() - 1);
				containers.remove(pos);
			}
		} else if (age > 5) {
			finish();
		}
	}

	public void finish() {
		if (sendAffectedBlocks) {
			mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("text.cutelessmod.clientcommands.replaceitem.result", totalCount, affectedBlocks));
		}
		super.finish();
	}

	private boolean sendReplaceItemCommand(BlockPos pos, ItemStack stack, int slot) {
		final String name = stack.getItem().getRegistryName().toString();
		final int count = stack.getCount();
		final int damage = stack.getItemDamage();
		if (replaceitemPermission && world.isBlockLoaded(pos) && Math.min(pos.getY(), pos.getY()) >= 0 && Math.max(pos.getY(), pos.getY()) < 256) {
			last_execution = age;
			world.sendPacketToServer(new CPacketChatMessage("/replaceitem block " + pos.getX() + " " + pos.getY() + " " + pos.getZ() + " slot.container." + slot + " " + name + " " + count + " " + damage));
			return true;
		} else {
			return false;
		}
	}

	private class SimpleContainer {
		public IBlockState blockState;
		public ItemStack stack;
		public int slot;
		public int slotCount;

		public SimpleContainer(IBlockState containerBlockState, ItemStack itemStack, int maxSlotCount) {
			blockState = containerBlockState;
			stack = itemStack;
			slotCount = maxSlotCount;
			slot = 0;
		}
	}
}