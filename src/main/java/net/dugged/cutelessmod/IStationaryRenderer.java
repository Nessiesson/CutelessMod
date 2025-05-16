package net.dugged.cutelessmod;

import net.minecraft.entity.player.EntityPlayer;

public interface IStationaryRenderer {
    void render(float partialTicks);

    void updatePosition(EntityPlayer player);
}
