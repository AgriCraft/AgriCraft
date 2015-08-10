package com.InfinityRaider.AgriCraft.items;

import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.RegisterHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public abstract class ItemAgricraft extends Item {
    public ItemAgricraft(String name) {
        super();
        this.setCreativeTab(CreativeTabs.tabMaterials);
        this.setMaxStackSize(64);
        RegisterHelper.registerItem(this, name);
    }

    public ItemAgricraft() {
        super();
        this.setCreativeTab(CreativeTabs.tabMaterials);
        this.setMaxStackSize(64);
        RegisterHelper.registerItem(this, getInternalName());
    }

    protected abstract String getInternalName();

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg) {
        LogHelper.debug("registering icon for: " + this.getUnlocalizedName());
        itemIcon = reg.registerIcon(this.getUnlocalizedName().substring(this.getUnlocalizedName().indexOf('.')+1));
    }
}
