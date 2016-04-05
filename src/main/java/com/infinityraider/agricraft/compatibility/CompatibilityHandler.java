package com.infinityraider.agricraft.compatibility;

import com.infinityraider.agricraft.blocks.BlockCrop;
import com.infinityraider.agricraft.compatibility.applecore.AppleCoreHelper;
import com.infinityraider.agricraft.compatibility.computercraft.ComputerCraftHelper;
import com.infinityraider.agricraft.compatibility.thaumcraft.ThaumcraftHelper;
import com.infinityraider.agricraft.compatibility.vanilla.VanillaHelper;
import com.infinityraider.agricraft.compatibility.waila.WailaHelper;
import com.infinityraider.agricraft.farming.cropplant.CropPlant;
import com.infinityraider.agricraft.handler.config.AgriCraftConfig;
import com.infinityraider.agricraft.handler.config.ConfigurationHandler;
import com.infinityraider.agricraft.tileentity.TileEntityCrop;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class CompatibilityHandler {

	private static final CompatibilityHandler INSTANCE = new CompatibilityHandler();

	public static CompatibilityHandler getInstance() {
		return INSTANCE;
	}

	private final Map<String, ModHelper> compatModules;
	private final Map<String, Boolean> compatEnabled;
	private final Map<Item, ModHelper> toolCompatModules;
	private final List<ModHelper> growthTickModules;

	private CompatibilityHandler() {
		this.compatModules = new HashMap<>();
		this.compatEnabled = new HashMap<>();
		this.toolCompatModules = new HashMap<>();
		growthTickModules = new ArrayList<>();
		initCompatModules();
	}

	public boolean isCompatibilityEnabled(String modId) {
		if (compatModules.containsKey(modId)) {
			return compatModules.get(modId).isEnabled();
		}
		if (!compatEnabled.containsKey(modId)) {
			compatEnabled.put(modId, ConfigurationHandler.enableModCompatibility(modId));
		}
		return compatEnabled.get(modId);
	}

	public void preInit() {
		compatModules.values().stream().filter(ModHelper::isEnabled).forEach(ModHelper::preInit);
	}

	public void init() {
		compatModules.values().stream().filter(ModHelper::isEnabled).forEach(ModHelper::init);
	}

	public void postInit() {
		compatModules.values().stream().filter(ModHelper::isEnabled).forEach(ModHelper::postInit);
	}

	public void announceGrowthTick(World world, BlockPos pos, IBlockState state) {
		for (ModHelper helper : growthTickModules) {
			helper.announceGrowthTick(world, pos, state);
		}
	}

	public boolean allowGrowthTick(World world, BlockPos pos, BlockCrop block, TileEntityCrop crop, Random rnd) {
		boolean flag = true;
		Iterator<ModHelper> iterator = growthTickModules.iterator();
		while (iterator.hasNext() && flag) {
			ModHelper helper = iterator.next();
			if (helper.isEnabled()) {
				flag = helper.allowGrowthTick(world, pos, block, crop, rnd);
			}
		}
		return flag;
	}

	public boolean isRightClickHandled(Item item) {
		ModHelper helper = toolCompatModules.get(item);
		return helper != null && helper.isEnabled();
	}

	public boolean handleRightClick(World world, BlockPos pos, BlockCrop block, TileEntityCrop crop, EntityPlayer player, ItemStack stack) {
		return toolCompatModules.get(stack.getItem()).handleRightClick(world, pos, block, crop, player, stack);
	}

	public List<CropPlant> getCropPlants() {
		List<CropPlant> list = new ArrayList<>();
		compatModules.values().stream().filter(ModHelper::isEnabled).forEach(helper -> list.addAll(helper.getCropPlants()));
		return list;
	}

	/**
	 * method holding all ModHelper classes
	 */
	@SuppressWarnings("unchecked")
	private void initCompatModules() {
		Class[] classes = {
			VanillaHelper.class,
			AppleCoreHelper.class,
			ComputerCraftHelper.class,
			ThaumcraftHelper.class,
			WailaHelper.class
		};
		for (Class clazz : classes) {
			if (ModHelper.class.isAssignableFrom(clazz)) {
				ModHelper helper = createInstance(clazz);
				if (helper != null) {
					compatModules.put(helper.getModId(), helper);
					if (helper.handleGrowthTick()) {
						growthTickModules.add(helper);
					}
					for (Item tool : helper.getCropTools()) {
						toolCompatModules.put(tool, helper);
					}
				}
			}
		}
	}

	/**
	 * Method to create only one instance for each mod helper
	 */
	private ModHelper createInstance(Class<? extends ModHelper> clazz) {
		ModHelper helper = null;
		try {
			helper = clazz.getConstructor().newInstance();
		} catch (Exception e) {
			if (AgriCraftConfig.debug) {
				e.printStackTrace();
			}
		}
		return helper;
	}
}
