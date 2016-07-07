package com.infinityraider.agricraft.items;

import com.infinityraider.agricraft.reference.AgriNuggetType;
import com.infinityraider.agricraft.utility.RegisterHelper;
import java.util.List;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class ItemNugget extends ItemBase {

	public ItemNugget() {
		super("agri_nugget", true, AgriNuggetType.getNuggets());
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
	@SideOnly(Side.CLIENT)
	public void registerItemRenderer() {
		for (int i = 0; i < AgriNuggetType.values().length; i++) {
			ModelResourceLocation model = RegisterHelper.getItemModel("agricraft:items/" + AgriNuggetType.values()[i].nugget);
			ModelLoader.setCustomModelResourceLocation(this, i, model);
		}
	}

}
