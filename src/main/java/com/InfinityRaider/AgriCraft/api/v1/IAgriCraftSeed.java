package com.InfinityRaider.AgriCraft.api.v1;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.IPlantable;

public interface IAgriCraftSeed extends IPlantable {
    /** Gets the plant for this seed */
    public IAgriCraftPlant getPlant();

    /** Gets the tier for this seed */
    public int tier();

    /** Gets the information for the journal for this seed */
    @SideOnly(Side.CLIENT)
    public String getInformation();

    /** Gets the Icon for this seed */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack);
}
