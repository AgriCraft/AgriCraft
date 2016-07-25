package com.infinityraider.agricraft.items;

import com.infinityraider.agricraft.reference.AgriNuggetType;
import com.infinityraider.agricraft.renderers.items.IAutoRenderedItem;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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

	@Override
	public List<ResourceLocation> getAllTextures() {
		final List<ResourceLocation> tex = new ArrayList<>();
		for (AgriNuggetType type : AgriNuggetType.values()) {
			tex.add(new ResourceLocation(type.texture));
		}
		return tex;
	}

	@Override
	public String getModelId(ItemStack stack) {
		return AgriNuggetType.getNugget(stack.getMetadata()).nugget;
	}

	@Override
	public String getBaseTexture(ItemStack stack) {
		return AgriNuggetType.getNugget(stack.getMetadata()).texture;
	}

}
