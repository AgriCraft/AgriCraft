package com.InfinityRaider.AgriCraft.items;

import com.InfinityRaider.AgriCraft.api.v1.IIconRegistrar;
import com.InfinityRaider.AgriCraft.api.v1.IconRegisterable;
import com.InfinityRaider.AgriCraft.creativetab.AgriCraftTab;
import com.InfinityRaider.AgriCraft.renderers.items.RenderItemBase;
import com.InfinityRaider.AgriCraft.renderers.items.RenderItemIcon;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.RegisterHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * The root Item class for all AgriCraft Items (excluding blockItems).
 */
public abstract class ItemBase extends Item implements IconRegisterable {
    @SideOnly(Side.CLIENT)
    private TextureAtlasSprite icon;
	
    public ItemBase(String name) {
        super();
        this.setCreativeTab(AgriCraftTab.agriCraftTab);
        this.setMaxStackSize(64);
        RegisterHelper.registerItem(this, name);
    }

    public ItemBase() {
        super();
        this.setCreativeTab(AgriCraftTab.agriCraftTab);
        this.setMaxStackSize(64);
        RegisterHelper.registerItem(this, getInternalName());
    }

    protected abstract String getInternalName();

    @SideOnly(Side.CLIENT)
    public RenderItemBase getItemRenderer() {
        return new RenderItemIcon(this);
    }

    @SideOnly(Side.CLIENT)
    public TextureAtlasSprite getIcon(ItemStack stack) {
        return icon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegistrar iconRegistrar) {
        String name = this.getUnlocalizedName();
        int index = name.indexOf(":");
        name = index > 0 ? name.substring(index+1) : name;
        index = name.indexOf(".");
        name = index > 0 ? name.substring(index+1) : name;
        icon = iconRegistrar.registerIcon("agricraft:items/"+name);
    }
}
