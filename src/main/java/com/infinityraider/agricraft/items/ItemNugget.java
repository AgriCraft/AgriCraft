package com.infinityraider.agricraft.items;

import com.infinityraider.agricraft.reference.AgriNuggetType;
import java.util.List;

import com.infinityraider.infinitylib.item.IAutoRenderedItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.infinityraider.infinitylib.item.ItemBase;
import java.util.ArrayList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

public class ItemNugget extends ItemBase implements IAutoRenderedItem {

	public ItemNugget() {
		super("agri_nugget");
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
		return AgriNuggetType.values()[stack.getMetadata()].getUnlocalizedName();
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
		List<ResourceLocation> textures = new ArrayList<>(AgriNuggetType.values().length);
		for (AgriNuggetType type : AgriNuggetType.values()) {
			textures.add(new ResourceLocation(type.texture));
		}
		return textures;
	}

}
