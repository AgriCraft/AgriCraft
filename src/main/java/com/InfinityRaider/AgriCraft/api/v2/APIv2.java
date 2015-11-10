package com.InfinityRaider.AgriCraft.api.v2;

import com.InfinityRaider.AgriCraft.api.v1.APIv1;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.world.World;

public interface APIv2 extends APIv1 {
    ICrop getCrop(World world, int x, int y, int z);

    void setStatCalculator(IStatCalculator calculator);

    @SideOnly(Side.CLIENT)
    void setStatStringDisplayer(IStatStringDisplayer displayer);
}
