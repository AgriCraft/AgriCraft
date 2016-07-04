package com.infinityraider.agricraft.init;

import com.infinityraider.agricraft.items.*;
import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.log.AgriLogger;
import com.infinityraider.agricraft.compat.jei.AgriCraftJEIPlugin;
import com.agricraft.agricore.util.ReflectionHelper;
import com.infinityraider.agricraft.apiimpl.v1.SeedRegistry;
import com.infinityraider.agricraft.utility.RegisterHelper;

public class AgriItems {

	public static final ItemBase CROPS = new ItemCrop();
	public static final ItemBase JOURNAL = new ItemJournal();
	public static final ItemBase TROWEL = new ItemTrowel();
	public static final ItemBase DEBUGGER = new ItemDebugger();
	public static final ItemBase HAND_RAKE = new ItemHandRake();
	public static final ItemBase CLIPPER = new ItemClipper();
	public static final ItemBase AGRI_CLIPPING = new ItemClipping();
	public static final ItemBase AGRI_SEED = new ItemAgriCraftSeed();
	public static final ItemBase AGRI_NUGGET = new ItemNugget();
	public static final ItemBase MAGNIFYING_GLASS = new ItemMagnifyingGlass();

	public static void init() {

		// Fetch the logger
		final AgriLogger logger = AgriCore.getLogger("AgriCraft-Items");

		// Notify Log
		logger.debug("Starting Item Initialization...");

		// Register seed handler.
		SeedRegistry.getInstance().registerAdapter((ItemAgriCraftSeed) AGRI_SEED);

		// Configure the Items
		logger.debug("Starting Item Configuration...");
		ReflectionHelper.forEachIn(AgriItems.class, ItemBase.class, (ItemBase item) -> {
			logger.debug("Configuring Item: {0}", item.internalName);
			AgriCore.getConfig().addConfigurable(item);
		});
		logger.debug("Finished Item Configuration!");

		// Register the Items
		logger.debug("Starting Item Registration...");
		ReflectionHelper.forEachIn(AgriItems.class, ItemBase.class, (ItemBase item) -> {
			if (item.isEnabled()) {
				logger.debug("Registering Item: {0}", item.internalName);
				RegisterHelper.registerItem(item, item.internalName);
				AgriCraftJEIPlugin.registerNbtIgnore(item, item.getIgnoredNBT());
			}
		});
		logger.debug("Finished Item Registration!");

		// Notify Log
		AgriCore.getLogger("AgriCraft").debug("Finished Item Initialization!");

	}

}
