package net.dugged.cutelessmod.clientcommands.worldedit;

import static net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection.Position.A;
import static net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection.Position.B;

import net.dugged.cutelessmod.clientcommands.ClientCommand;
import net.dugged.cutelessmod.clientcommands.mixins.IItemSword;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemSword;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;

public class CommandPos extends ClientCommand {

	public CommandPos() {
		creativeOnly = false;
	}

	@Override
	public String getName() {
		return "pos";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation(
			"text.cutelessmod.clientcommands.worldEdit.pos.usage").getUnformattedText();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
		throws CommandException {
		if (args.length == 1) {
			Item itemInHand = mc.player.getHeldItemMainhand().getItem();
			Item.ToolMaterial material =
				itemInHand instanceof ItemSword ? ((IItemSword) itemInHand).getMaterial()
					: ToolMaterial.GOLD;
			if (parseInt(args[0]) == 0) {
				if (WorldEdit.getPos(material, A) == null || !WorldEdit.getPos(material, A)
					.equals(WorldEdit.playerPos())) {
					WorldEdit.setPos(material, A, WorldEdit.playerPos());
				} else {
					WorldEdit.setPos(material, A, null);
				}
			} else if (parseInt(args[0]) == 1) {
				if (WorldEdit.getPos(material, B) == null || !WorldEdit.getPos(material, B)
					.equals(WorldEdit.playerPos())) {
					WorldEdit.setPos(material, B, WorldEdit.playerPos());
				} else {
					WorldEdit.setPos(material, B, null);
				}
			} else {
				WorldEdit.sendMessage(new TextComponentTranslation(
					"text.cutelessmod.clientcommands.worldEdit.pos.invalidPosition"));
			}
		} else {
			WorldEdit.sendMessage(getUsage(sender));
		}
	}
}
