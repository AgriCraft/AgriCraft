package com.infinityraider.agricraft.items;

import com.infinityraider.agricraft.api.v1.IIconRegistrar;
import com.infinityraider.agricraft.creativetab.AgriCraftTab;
import com.infinityraider.agricraft.utility.RegisterHelper;
import com.infinityraider.agricraft.utility.icon.SafeIcon;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.infinityraider.agricraft.api.v1.IAgriCraftRenderable;

/**
 * The root Item class for all AgriCraft Items (excluding blockItems).
 */
public abstract class ItemBase extends Item implements IAgriCraftRenderable {
	
    @SideOnly(Side.CLIENT)
    private final SafeIcon icon;
	
	public final String internalName;
	
    public ItemBase(String name) {
        super();
        this.setCreativeTab(AgriCraftTab.agriCraftTab);
        this.setMaxStackSize(64);
		this.internalName = name;
		// This is a bad idea...
        RegisterHelper.registerItem(this, name);
		this.icon = new SafeIcon(this);
    }

    @SideOnly(Side.CLIENT)
    public void registerItemRenderer() {
		RegisterHelper.registerItemRenderer(this);
	}

	@Override
    @SideOnly(Side.CLIENT)
    public TextureAtlasSprite getIcon() {
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
