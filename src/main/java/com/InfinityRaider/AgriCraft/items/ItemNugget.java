package com.InfinityRaider.AgriCraft.items;

import com.InfinityRaider.AgriCraft.renderers.items.RenderItemBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemNugget extends ItemBase {

    public ItemNugget(String name) {
        super("nugget"+name);
        this.setCreativeTab(CreativeTabs.tabMaterials);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderItemBase getItemRenderer() {
        return null;
    }
	
}
