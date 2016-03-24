package com.infinityraider.agricraft.init;

import com.infinityraider.agricraft.handler.config.AgriCraftConfigurable;
import com.infinityraider.agricraft.handler.config.ConfigCategory;
import com.infinityraider.agricraft.handler.config.ConfigurationHandler;
import com.infinityraider.agricraft.items.*;
import com.infinityraider.agricraft.utility.LogHelper;
import net.minecraft.item.Item;

public class AgriCraftItems {

	@AgriCraftConfigurable(
			category = ConfigCategory.TOOLS,
			key = "Enable Hand Rake",
			comment = "Set to false to disable the Hand Rake."
	)
	public static boolean enableHandRake = true;

	@AgriCraftConfigurable(
			category = ConfigCategory.TOOLS,
			key = "Enable Trowel",
			comment = "Set to false to disable the Trowel."
	)
	public static boolean enableTrowel = true;

	@AgriCraftConfigurable(
			category = ConfigCategory.TOOLS,
			key = "Enable Magnifying Glass",
			comment = "Set to false to disable the Magnifying Glass."
	)
	public static boolean enableMagnifyingGlass = true;

	@AgriCraftConfigurable(
			category = ConfigCategory.TOOLS,
			key = "Enable Clipper",
			comment = "Set to false to disable the Clipper."
	)
	public static boolean enableClipper = true;
	
	static {
		ConfigurationHandler.addConfigurable(AgriCraftItems.class);
	}

	public static Item crops;
	public static Item journal;
	public static Item trowel;
	public static Item magnifyingGlass;
	public static Item debugItem;
	public static Item handRake;
	public static Item clipper;
	public static ItemClipping clipping;

	public static void init() {
		clipping = new ItemClipping();
		crops = new ItemCrop();
		journal = new ItemJournal();
		magnifyingGlass = new ItemMagnifyingGlass();
		debugItem = new ItemDebugger();
		if (enableTrowel) {
			trowel = new ItemTrowel();
		}
		if (enableHandRake) {
			handRake = new ItemHandRake();
		}
		if (enableClipper) {
			clipper = new ItemClipper();
		}
		LogHelper.debug("Items Registered");
	}
}
