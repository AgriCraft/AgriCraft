package com.infinityraider.agricraft.items;

import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ItemNugget extends ItemBase {
	
	public static enum NuggetType {
		
		Emerald("gemEmerald", "oreEmerald"),
		Diamond("gemDiamond", "oreDiamond"),
		Quartz("quartz", "quartz"),
		Iron,
		Copper,
		Tin,
		Lead,
		Silver,
		Aluminum,
		Nickel,
		Platinum,
		Osmium;
		
		public final String nugget;
		public final String ingot;
		public final String ore;
		
		private NuggetType() {
			this.nugget = "nugget" + this.name();
			this.ingot = "ingot" + this.name();
			this.ore = "ore" + this.name();
		}

		private NuggetType(String ingot, String ore) {
			this.nugget = "nugget" + this.name();
			this.ingot = ingot;
			this.ore = ore;
		}
		
	}

	public ItemNugget() {
		super("agri_nugget", true);
		this.setCreativeTab(CreativeTabs.tabMaterials);
	}

	@Override
	public boolean getHasSubtypes() {
		return true;
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> varients) {
		for (NuggetType type : NuggetType.values()) {
			ItemStack stack = new ItemStack(item, 1, type.ordinal());
			OreDictionary.registerOre(type.nugget, stack);
			varients.add(stack);
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item.agricraft:" + NuggetType.values()[stack.getMetadata()].nugget;
	}

}
