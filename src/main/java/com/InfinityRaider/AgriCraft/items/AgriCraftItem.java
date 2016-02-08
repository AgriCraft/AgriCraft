/*
 * The root for all agricraft items.
 */
package com.InfinityRaider.AgriCraft.items;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 *
 * @author ryeni
 */
public interface AgriCraftItem {
	
	@SideOnly(Side.CLIENT)
    public TextureAtlasSprite getIcon(ItemStack stack);
	
}
