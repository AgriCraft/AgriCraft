package com.infinityraider.agricraft.init;

import com.agricraft.agricore.config.AgriConfigCategory;
import com.agricraft.agricore.config.AgriConfigurable;
import com.infinityraider.agricraft.items.*;
import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.compat.jei.AgriCraftJEIPlugin;
import com.agricraft.agricore.util.ReflectionHelper;
import com.infinityraider.agricraft.apiimpl.v1.SeedRegistry;
import com.infinityraider.agricraft.utility.RegisterHelper;
import net.minecraft.item.Item;

public class AgriItems {

	@AgriConfigurable(
			category = AgriConfigCategory.TOOLS,
			key = "Enable Hand Rake",
			comment = "Set to false to disable the Hand Rake."
	)
	public static boolean enableHandRake = true;

	@AgriConfigurable(
			category = AgriConfigCategory.TOOLS,
			key = "Enable Trowel",
			comment = "Set to false to disable the Trowel."
	)
	public static boolean enableTrowel = true;

	@AgriConfigurable(
			category = AgriConfigCategory.TOOLS,
			key = "Enable Magnifying Glass",
			comment = "Set to false to disable the Magnifying Glass."
	)
	public static boolean enableMagnifyingGlass = true;

	@AgriConfigurable(
			category = AgriConfigCategory.TOOLS,
			key = "Enable Clipper",
			comment = "Set to false to disable the Clipper."
	)
	public static boolean enableClipper = true;
	
	static {
		AgriCore.getConfig().addConfigurable(AgriItems.class);
	}

	public static Item crops;
	public static Item journal;
	public static ItemTrowel trowel;
	public static Item magnifyingGlass;
	public static Item debugItem;
	public static Item handRake;
	public static Item clipper;
	public static final ItemClipping clipping = new ItemClipping();
	public static final ItemAgriCraftSeed seed = new ItemAgriCraftSeed();
	public static final ItemNugget nugget = new ItemNugget();

	public static void init() {
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
		
		// Register seed handler.
		SeedRegistry.getInstance().registerAdapter(seed);
		
		// Register the Items
		ReflectionHelper.forEachIn(AgriItems.class, ItemBase.class, (ItemBase item) -> {
			AgriCore.getLogger("AgriCraft").debug("Registering Item: {0}", item.internalName);
			RegisterHelper.registerItem(item, item.internalName);
			AgriCraftJEIPlugin.registerNbtIgnore(item, item.getIgnoredNBT());
		});
		
		AgriCore.getLogger("AgriCraft").debug("Items Registered");
	}

}
