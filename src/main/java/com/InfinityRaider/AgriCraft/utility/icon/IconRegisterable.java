package com.InfinityRaider.AgriCraft.utility.icon;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IconRegisterable {
    @SideOnly(Side.CLIENT)
    void registerIcons(IIconRegistrar iconRegistrar);
}
