package com.infinityraider.agricraft.items;

import com.infinityraider.agricraft.reference.AgriNuggetType;
import java.util.List;

import com.infinityraider.infinitylib.render.item.IAutoRenderedItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Collections;

import com.infinityraider.infinitylib.item.ItemBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

public class ItemNugget extends ItemBase implements IAutoRenderedItem {

	public ItemNugget() {
		super("agri_nugget", false);
		this.setCreativeTab(CreativeTabs.MATERIALS);
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> varients) {
		for (AgriNuggetType type : AgriNuggetType.values()) {
			ItemStack stack = new ItemStack(item, 1, type.ordinal());
			OreDictionary.registerOre(type.nugget, stack);
			varients.add(stack);
		}
	}

	@Override
	public boolean getHasSubtypes() {
		return true;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item.agricraft:" + AgriNuggetType.values()[stack.getMetadata()].nugget;
	}

	/*
	@SideOnly(Side.CLIENT)
	public void registerItemRenderer() {
		for (int i = 0; i < AgriNuggetType.values().length; i++) {
			ModelResourceLocation model = RegisterHelper.getItemModel("agricraft:items/" + AgriNuggetType.values()[i].nugget);
			ModelLoader.setCustomModelResourceLocation(this, i, model);
		}
		return tex;
	}
	 */
	@Override
	public List<String> getOreTags() {
		return Collections.emptyList();
	}

	@Override
	public String getModelId(ItemStack stack) {
		return AgriNuggetType.getNugget(stack.getMetadata()).nugget;
	}

	@Override
	public String getBaseTexture(ItemStack stack) {
		return AgriNuggetType.getNugget(stack.getMetadata()).texture;
	}

	@Override
	public List<ResourceLocation> getAllTextures() {
		return Collections.emptyList();
	}

}
