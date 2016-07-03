package com.infinityraider.agricraft.tabs;

import com.infinityraider.agricraft.init.AgriItems;
import com.infinityraider.agricraft.reference.Reference;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class AgriTabs {

	public static final CreativeTabs TAB_AGRICRAFT = new CreativeTabs(Reference.MOD_ID) {
		@Override
		public Item getTabIconItem() {
			return AgriItems.debugItem;
		}
	};
	
	public static final CreativeTabs TAB_AGRICRAFT_SEED = new CreativeTabs(Reference.MOD_ID + "_seeds") {
		@Override
		public Item getTabIconItem() {
			return AgriItems.seed;
		}
	};
	
}
