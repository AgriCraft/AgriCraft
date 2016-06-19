package com.infinityraider.agricraft.compat;

import com.agricraft.agricore.config.AgriConfigCategory;
import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.blocks.BlockCrop;
import com.infinityraider.agricraft.compat.json.JsonHelper;
import com.infinityraider.agricraft.compat.thaumcraft.ThaumcraftHelper;
import com.infinityraider.agricraft.compat.waila.WailaHelper;
import com.infinityraider.agricraft.tiles.TileEntityCrop;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.infinityraider.agricraft.api.v3.plant.IAgriPlant;

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
			compatEnabled.put(modId, getCompatConfigEnabled(modId));
		}
		return compatEnabled.get(modId);
	}
	
	static boolean getCompatConfigEnabled(String modId) {
		return AgriCore.getConfig().getBoolean(modId, AgriConfigCategory.COMPATIBILITY, true, "set to false to disable compatibility for " + modId);
	}

	public void preInit() {
		MinecraftForge.EVENT_BUS.register(this);
		compatModules.values().stream().filter(ModHelper::isEnabled).forEach(ModHelper::preInit);
	}

	public void init() {
		compatModules.values().stream().filter(ModHelper::isEnabled).forEach(ModHelper::init);
	}

	public void postInit() {
		compatModules.values().stream().filter(ModHelper::isEnabled).forEach(ModHelper::postInit);
	}
	
	public void serverStart() {
		compatModules.values().stream().filter(ModHelper::isEnabled).forEach(ModHelper::serverStart);
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void textureStitch(TextureStitchEvent.Pre event) {
		compatModules.values().stream().filter(ModHelper::isEnabled).forEach(ModHelper::textureStitch);
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

	public List<IAgriPlant> getCropPlants() {
		List<IAgriPlant> list = new ArrayList<>();
		compatModules.values().stream().filter(ModHelper::isEnabled).forEach(helper -> list.addAll(helper.getCropPlants()));
		return list;
	}

	/**
	 * method holding all ModHelper classes
	 */
	@SuppressWarnings("unchecked")
	private void initCompatModules() {
		Class[] classes = {
			JsonHelper.class,
			ThaumcraftHelper.class,
			WailaHelper.class
		};
		for (Class clazz : classes) {
			if (ModHelper.class.isAssignableFrom(clazz)) {
				ModHelper helper = createInstance(clazz);
				if (helper != null && helper.shouldLoad()) {
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
			AgriCore.getLogger("AgriCompat").trace(e);
		}
		return helper;
	}
}
