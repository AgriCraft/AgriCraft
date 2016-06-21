package com.infinityraider.agricraft.items;

import net.minecraft.creativetab.CreativeTabs;

public class ItemNugget extends ItemBase {

	public static final String[] vanillaNuggets = {
		"Iron",
		"Emerald",
		"Diamond",
		"Quartz"
	};

	public static final String[] modNuggets = {
		"Copper",
		"Tin",
		"Lead",
		"Silver",
		"Aluminum",
		"Nickel",
		"Platinum",
		"Osmium"
	};

	public ItemNugget(String name) {
		super("nugget" + name, false);
		this.setCreativeTab(CreativeTabs.tabMaterials);
	}

}
