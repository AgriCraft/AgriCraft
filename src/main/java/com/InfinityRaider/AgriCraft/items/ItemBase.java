package com.InfinityRaider.AgriCraft.items;

import com.InfinityRaider.AgriCraft.api.v1.IIconRegistrar;
import com.InfinityRaider.AgriCraft.api.v1.IconRegisterable;
import com.InfinityRaider.AgriCraft.creativetab.AgriCraftTab;
import com.InfinityRaider.AgriCraft.renderers.items.RenderItemBase;
import com.InfinityRaider.AgriCraft.renderers.items.RenderItemIcon;
import com.InfinityRaider.AgriCraft.utility.RegisterHelper;
import com.InfinityRaider.AgriCraft.utility.icon.SafeIcon;
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
    private final SafeIcon icon;
	
	public final String internalName;
	
    public ItemBase(String name) {
        super();
        this.setCreativeTab(AgriCraftTab.agriCraftTab);
        this.setMaxStackSize(64);
		this.internalName = name;
        RegisterHelper.registerItem(this, name);
		this.icon = new SafeIcon(this);
    }

    @SideOnly(Side.CLIENT)
    public RenderItemBase getItemRenderer() {
        return new RenderItemIcon(this);
    }

    @SideOnly(Side.CLIENT)
    public TextureAtlasSprite getIcon(ItemStack stack) {
        return icon.getIcon();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegistrar iconRegistrar) {
        String name = this.getUnlocalizedName();
        int index = name.indexOf(":");
        name = index > 0 ? name.substring(index+1) : name;
        index = name.indexOf(".");
        name = index > 0 ? name.substring(index+1) : name;
        iconRegistrar.registerIcon("agricraft:items/"+name);
    }
	
}
