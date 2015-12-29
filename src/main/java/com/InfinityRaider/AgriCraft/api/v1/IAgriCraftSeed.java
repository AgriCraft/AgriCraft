package com.InfinityRaider.AgriCraft.api.v1;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IAgriCraftSeed extends IPlantable, IconRegisterable {
    /** Gets the plant for this seed */
    public IAgriCraftPlant getPlant();

    /** Gets the tier for this seed */
    public int tier();

    /** Gets the information for the journal for this seed */
    @SideOnly(Side.CLIENT)
    public String getInformation();

    /** Gets the registered TextureAtlasSprite icon for this seed */
    @SideOnly(Side.CLIENT)
    TextureAtlasSprite getIcon(ItemStack stack);
}
