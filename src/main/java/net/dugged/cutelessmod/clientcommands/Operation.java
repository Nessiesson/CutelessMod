package net.dugged.cutelessmod.clientcommands;

import net.minecraft.util.math.BlockPos;

public class Operation {

	public final BlockPos pos;
	public final String command;

	public Operation(BlockPos pos, String command) {
		this.pos = pos;
		this.command = command;
	}
}
