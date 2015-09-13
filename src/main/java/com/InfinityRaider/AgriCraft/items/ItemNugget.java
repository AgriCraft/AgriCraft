package com.InfinityRaider.AgriCraft.items;

import net.minecraft.creativetab.CreativeTabs;

public class ItemNugget extends ItemAgricraft {
    private final String name;

    public ItemNugget(String name) {
        super("nugget"+name);
        this.name = "nugget"+name;
        this.setCreativeTab(CreativeTabs.tabMaterials);
    }

    @Override
    protected String getInternalName() {
        return name;
    }
}
