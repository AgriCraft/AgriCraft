package com.infinityraider.agricraft.items;

import com.infinityraider.agricraft.creativetab.AgriCraftTab;
import com.infinityraider.agricraft.utility.RegisterHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.infinityraider.agricraft.api.v1.IAgriCraftRenderable;
import com.infinityraider.agricraft.utility.icon.IconUtil;

/**
 * The root Item class for all AgriCraft Items (excluding blockItems).
 */
public abstract class ItemBase extends Item implements IAgriCraftRenderable {

	public final String internalName;

	public final boolean isModelVanillia;
	
	protected final String[] varients;

	public ItemBase(String name, boolean isModelVanillia, String... varients) {
		super();
		this.setCreativeTab(AgriCraftTab.agriCraftTab);
		this.setMaxStackSize(64);
		this.internalName = name;
		this.isModelVanillia = isModelVanillia;
		if (varients.length == 0) {
			this.varients = new String[]{""};
		} else {
			this.varients = varients;
		}
		// This is a bad idea...
		RegisterHelper.registerItem(this, name);
	}

	@SideOnly(Side.CLIENT)
	public void registerItemRenderer() {
		if (this.isModelVanillia) {
			RegisterHelper.registerItemRenderer(this, varients);
		} else {
			RegisterHelper.registerItemRenderer(this, "agricraft:items/" + internalName);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite getIcon() {
		return IconUtil.getIcon(this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons() {
		IconUtil.registerIcon(this.getRegistryName().replaceFirst(":", ":items/"));
	}

}
