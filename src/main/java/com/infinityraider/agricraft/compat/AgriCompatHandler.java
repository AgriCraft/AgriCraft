package com.infinityraider.agricraft.compat;

import com.infinityraider.agricraft.blocks.BlockCrop;
import com.infinityraider.agricraft.tiles.TileEntityCrop;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;

public class AgriCompatHandler {

	private static final AgriCompatHandler INSTANCE = new AgriCompatHandler();

	public static AgriCompatHandler getInstance() {
		return INSTANCE;
	}

	private final Map<String, AgriCompatModule> compatModules;

	private final Map<Item, AgriCompatModule> toolCompatModules;
	private final List<AgriCompatModule> growthTickModules;

	public AgriCompatHandler() {
		this.compatModules = new HashMap<>();
		this.toolCompatModules = new HashMap<>();
		this.growthTickModules = new ArrayList<>();
	}

	public boolean register(AgriCompatModule module) {
		if (this.compatModules.containsKey(module.getModId())) {
			return false;
		}
		this.compatModules.put(module.getModId(), module);
		module.onRegisterModule();
		if (module.handleGrowthTick()) {
			growthTickModules.add(module);
		}
		for (Item tool : module.getCropTools()) {
			toolCompatModules.put(tool, module);
		}
		return true;
	}

	public boolean isCompatEnabled(String modId) {
		return compatModules.containsKey(modId) && compatModules.get(modId).enabled;
	}

	public void init() {
		compatModules.values().stream().filter(AgriCompatModule::isEnabled).forEach(AgriCompatModule::init);
	}

	public void postInit() {
		compatModules.values().stream().filter(AgriCompatModule::isEnabled).forEach(AgriCompatModule::postInit);
	}

	public void serverStart() {
		compatModules.values().stream().filter(AgriCompatModule::isEnabled).forEach(AgriCompatModule::serverStart);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void textureStitch(TextureStitchEvent.Pre event) {
		compatModules.values().stream().filter(AgriCompatModule::isEnabled).forEach(AgriCompatModule::textureStitch);
	}

	public void announceGrowthTick(World world, BlockPos pos, IBlockState state) {
		for (AgriCompatModule helper : growthTickModules) {
			helper.announceGrowthTick(world, pos, state);
		}
	}

	public boolean allowGrowthTick(World world, BlockPos pos, BlockCrop block, TileEntityCrop crop, Random rnd) {
		boolean flag = true;
		Iterator<AgriCompatModule> iterator = growthTickModules.iterator();
		while (iterator.hasNext() && flag) {
			AgriCompatModule helper = iterator.next();
			if (helper.isEnabled()) {
				flag = helper.allowGrowthTick(world, pos, block, crop, rnd);
			}
		}
		return flag;
	}

	public boolean isRightClickHandled(Item item) {
		AgriCompatModule helper = toolCompatModules.get(item);
		return helper != null && helper.isEnabled();
	}

	public boolean handleRightClick(World world, BlockPos pos, BlockCrop block, TileEntityCrop crop, EntityPlayer player, ItemStack stack) {
		return toolCompatModules.get(stack.getItem()).handleRightClick(world, pos, block, crop, player, stack);
	}

	public List<IAgriPlant> getCropPlants() {
		List<IAgriPlant> list = new ArrayList<>();
		compatModules.values().stream().filter(AgriCompatModule::isEnabled).forEach(helper -> list.addAll(helper.getCropPlants()));
		return list;
	}

}
