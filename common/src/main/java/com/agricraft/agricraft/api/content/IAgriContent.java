package com.agricraft.agricraft.api.content;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.content.items.IAgriClipperItem;
import com.agricraft.agricraft.api.content.items.IAgriCropStickItem;
import com.agricraft.agricraft.api.content.items.IAgriJournalItem;
import com.agricraft.agricraft.api.content.items.IAgriRakeItem;
import com.agricraft.agricraft.api.content.items.IAgriSeedBagItem;
import com.agricraft.agricraft.api.content.items.IAgriSeedItem;
import com.agricraft.agricraft.api.content.items.IAgriTrowelItem;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

/**
 * This interface gives access to AgriCraft content added to Minecraft,
 * this includes Blocks, Items, Fluids, Enchantments, etc.
 */
public interface IAgriContent {

	/**
	 * @return the AgriCraft IAgriContent instance
	 */
	static IAgriContent getInstance() {
		return AgriApi.getAgriContent();
	}

	/**
	 * @return the AgriCraft Blocks
	 */
	@SuppressWarnings("unused")
	static Blocks blocks() {
		return getInstance().getBlocks();
	}

	/**
	 * @return the AgriCraft Enchantments
	 */
	@SuppressWarnings("unused")
	static Enchantments enchantments() {
		return getInstance().getEnchantments();
	}

	/**
	 * @return the AgriCraft Fluids
	 */
	@SuppressWarnings("unused")
	static Fluids fluids() {
		return getInstance().getFluids();
	}

	/**
	 * @return the AgriCraft Items
	 */
	@SuppressWarnings("unused")
	static Items items() {
		return getInstance().getItems();
	}

	/**
	 * @return the AgriCraft Sounds
	 */
	@SuppressWarnings("unused")
	static Sounds sounds() {
		return getInstance().getSounds();
	}

	/**
	 * @return the AgriCraft Sounds
	 */
	@SuppressWarnings("unused")
	static Tabs tabs() {
		return getInstance().getTabs();
	}

	/**
	 * @return Wrapper instance for the Blocks
	 */
	Blocks getBlocks();

	/**
	 * @return Wrapper instance for the Enchantments
	 */
	Enchantments getEnchantments();

	/**
	 * @return Wrapper instance for the Fluids
	 */
	Fluids getFluids();

	/**
	 * @return Wrapper instance for the Items
	 */
	Items getItems();

	/**
	 * @return Wrapper instance for the Sounds
	 */
	Sounds getSounds();

	/**
	 * @return Wrapper instance for the Tabs
	 */
	Tabs getTabs();

	/**
	 * Interface wrapping getters for the AgriCraft blocks
	 */
	@SuppressWarnings("unused")
	interface Blocks {

		/**
		 * @return the AgriCraft Crop fluid
		 */
		Block getCropBlock();

		/**
		 * @return the AgriCraft Seed Analyzer fluid
		 */
		Block getSeedAnalyzerBlock();

		/**
		 * @return the AgriCraft Irrigation Tank fluid
		 */
		Block getTankBlock();

		/**
		 * @return the AgriCraft Irrigation Channel fluid
		 */
		Block getChannelBlock();

		/**
		 * @return the AgriCraft Hollow Irrigation Channel fluid
		 */
		Block getHollowChannelBlock();

		/**
		 * @return the AgriCraft Sprinkler fluid
		 */
		Block getSprinklerBlock();

		/**
		 * @return the AgriCraft Grate fluid
		 */
		Block getGrateBlock();

		/**
		 * @return the AgriCraft Greenhouse Air fluid
		 */
		Block getGreenHouseAirBlock();

// FIXME: update
//		/**
//		 * @return the AgriCraft Greenhouse Monitor fluid
//		 */
//		BlockGreenHouseMonitor getGreenHouseMonitorBlock();

	}

	/**
	 * Interface wrapping getters for the AgriCraft enchantments
	 */
	@SuppressWarnings("unused")
	interface Enchantments {

		/**
		 * @return the AgriCraft Seed Bag enchantment
		 */
		Enchantment getSeedBagEnchantment();

	}

	/**
	 * Interface wrapping getters for the AgriCraft fluids
	 */
	@SuppressWarnings("unused")
	interface Fluids {

		/**
		 * @return the AgriCraft Irrigation Tank Water fluid
		 */
		Fluid getTankWater();

	}

	/**
	 * Interface wrapping getters for the AgriCraft items
	 */
	@SuppressWarnings("unused")
	interface Items {

		/**
		 * @return the AgriCraft Debugger Item
		 */
		Item getDebuggerItem();

		/**
		 * @return the AgriCraft Wooden Crop Sticks Item
		 */
		IAgriCropStickItem getWoodCropSticksItem();

		/**
		 * @return the AgriCraft Iron Crop Sticks Item
		 */
		IAgriCropStickItem getIronCropSticksItem();

		/**
		 * @return the AgriCraft Obsidian Crop Sticks Item
		 */
		IAgriCropStickItem getObsidianCropSticksItem();

		/**
		 * @return the AgriCraft Seed Analyzer Item
		 */
		Item getSeedAnalyzerItem();

		/**
		 * @return the AgriCraft Journal Item
		 */
		IAgriJournalItem getJournalItem();

		/**
		 * @return the AgriCraft Seed Item
		 */
		IAgriSeedItem getSeedItem();

		/**
		 * @return the AgriCraft Irrigation Tank Item
		 */
		Item getIrrigationTankItem();

		/**
		 * @return the AgriCraft Irrigation Channel Item
		 */
		Item getIrrigationChannelItem();

		/**
		 * @return the AgriCraft Hollow Irrigation Channel Item
		 */
		Item getHollowIrrigationChannelItem();

		/**
		 * @return the AgriCraft Sprinkler Item
		 */
		Item getSprinklerItem();

		/**
		 * @return the AgriCraft Irrigation Valve Item
		 */
		Item getValveItem();

		/**
		 * @return the AgriCraft Clipper Item
		 */
		IAgriClipperItem getClipperItem();

		/**
		 * @return the AgriCraft Magnifying Glass Item
		 */
		Item getMagnifyingGlassItem();

		/**
		 * @return the AgriCraft Wooden Rake Item
		 */
		IAgriRakeItem getWoodenRakeItem();

		/**
		 * @return the AgriCraft Iron Rake Item
		 */
		IAgriRakeItem getIronRakeItem();

		/**
		 * @return the AgriCraft Trowel Item
		 */
		IAgriTrowelItem getTrowelItem();

		/**
		 * @return the AgriCraft Seed Bag Item
		 */
		IAgriSeedBagItem getSeedBagItem();

		/**
		 * @return the AgriCraft Grate Item
		 */
		Item getGrateItem();

// FIXME: update
//		/**
//		 * @return the AgriCraft Greenhouse Monitor Item Item
//		 */
//		ItemGreenHouseMonitor getGreenHouseMonitorItem();

		/**
		 * @return the AgriCraft Copper Nugget Item (can be null if disabled in the config)
		 */
		@Nullable
		Item getCopperNuggetItem();

		/**
		 * @return the AgriCraft Coal Nugget Item (can be null if disabled in the config)
		 */
		@Nullable
		Item getCoalNuggetItem();

		/**
		 * @return the AgriCraft Diamond Nugget Item (can be null if disabled in the config)
		 */
		@Nullable
		Item getDiamondNuggetItem();

		/**
		 * @return the AgriCraft Emerald Nugget Item (can be null if disabled in the config)
		 */
		@Nullable
		Item getEmeraldNuggetItem();

		/**
		 * @return the AgriCraft Quartz Nugget Item (can be null if disabled in the config)
		 */
		@Nullable
		Item getQuartzNuggetItem();

		/**
		 * @return the AgriCraft Netherite Sliver Item (can be null if disabled in the config)
		 */
		@Nullable
		Item getNetheriteSliverItem();

		/**
		 * @return the AgriCraft Amathyllis Petal Item (can be null if disabled in the config)
		 */
		@Nullable
		Item getAmathyllisPetalItem();

	}

	/**
	 * Interface wrapping getters for the AgriCraft sound events
	 */
	@SuppressWarnings("unused")
	interface Sounds {

		/**
		 * @return the AgriCraft Valve sound
		 */
		SoundEvent getValveSound();

	}

	/**
	 * Interface wrapping getters for the AgriCraft creative tabs
	 */
	@SuppressWarnings("unused")
	interface Tabs {

		/**
		 * @return the AgriCraft main item group
		 */
		CreativeModeTab getAgriCraftTab();

		/**
		 * @return the AgriCraft seeds item group
		 */
		CreativeModeTab getSeedsTab();

	}

}
