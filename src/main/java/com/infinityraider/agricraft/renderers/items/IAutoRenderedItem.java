/*
 */
package com.infinityraider.agricraft.renderers.items;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 *
 * @author RlonRyan
 */
public interface IAutoRenderedItem {
	
	@SideOnly(Side.CLIENT)
	public String getModelId(ItemStack stack);
	
	@SideOnly(Side.CLIENT)
	public String getBaseTexture(ItemStack stack);
	
	@SideOnly(Side.CLIENT)
	default public List<ItemModelTexture> getOverlayTextures(ItemStack stack) {
		return new ArrayList<>();
	}
	
	@SideOnly(Side.CLIENT)
	public List<ResourceLocation> getAllTextures();
	
}
