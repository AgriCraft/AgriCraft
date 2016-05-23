package com.infinityraider.agricraft.init;

import com.infinityraider.agricraft.api.v1.BlockWithMeta;
import com.infinityraider.agricraft.api.v1.RenderMethod;
import com.infinityraider.agricraft.api.v1.RequirementType;
import com.infinityraider.agricraft.blocks.BlockModPlant;
import com.infinityraider.agricraft.handler.config.AgriCraftConfig;
import com.infinityraider.agricraft.handler.config.ConfigurationHandler;
import com.infinityraider.agricraft.items.ItemModSeed;
import com.infinityraider.agricraft.utility.IOHelper;
import com.agricraft.agricore.core.AgriCore;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import java.lang.reflect.Field;
import java.util.List;

public class CustomCrops {

	public static BlockModPlant[] customCrops;
	public static ItemModSeed[] customSeeds;

	@SuppressWarnings("deprecation")
	public static void init() {
		if (AgriCraftConfig.customCrops) {
			String[] cropsRawData = IOHelper.getLinesArrayFromData(ConfigurationHandler.readCustomCrops());
			customCrops = new BlockModPlant[cropsRawData.length];
			customSeeds = new ItemModSeed[cropsRawData.length];
			for (int i = 0; i < cropsRawData.length; i++) {
				String[] cropData = IOHelper.getData(cropsRawData[i]);
				//cropData[0]: name
				//cropData[1]: fruit name:meta
				//cropData[2]: soil
				//cropData[3]: base block name:meta
				//cropData[4]: tier
				//cropData[5]: render type
				//cropData[6]: information
				//cropData[7]: shearable drop (optional)
				boolean success = cropData.length == 7 || cropData.length == 8;
				String errorMsg = "Incorrect amount of arguments, arguments should be: (name, fruit:fruitMeta, soil, baseBlock:baseBlockMeta, tier, renderType, information, shearable (optional) )";
				AgriCore.getLogger("AgriCraft").debug(new StringBuffer("parsing ").append(cropsRawData[i]));
				if (success) {
					ItemStack fruitStack = IOHelper.getStack(cropData[1]);
					errorMsg = "Invalid fruit";
					success = (fruitStack != null && fruitStack.getItem() != null) || (cropData[1].equals("null"));
					if (success) {
						String name = cropData[0];
						//soil
						BlockWithMeta soil = IOHelper.getBlock(cropData[2]);
						//baseblock
						BlockWithMeta base = IOHelper.getBlock(cropData[3]);
						//tier
						int tier = Integer.parseInt(cropData[4]);
						//render method
						RenderMethod renderType = RenderMethod.values()[Integer.parseInt(cropData[5])];
						//shearable
						ItemStack shearable = cropData.length > 7 ? IOHelper.getStack(cropData[7]) : null;
						shearable = (shearable != null && shearable.getItem() != null) ? shearable : null;
						//info
						String info = cropData[6];
						try {
							customCrops[i] = new BlockModPlant(name, fruitStack, soil, RequirementType.BELOW, base, tier, renderType, shearable);
						} catch (Exception e) {
							if (AgriCraftConfig.debug) {
								AgriCore.getLogger("AgriCraft").trace(e);
							}
							break;
						}
						customSeeds[i] = customCrops[i].getSeed();
						//TODO: find out how to fix this
						//LanguageRegistry.addName(customCrops[i], Character.toUpperCase(name.charAt(0)) + name.substring(1));
						//LanguageRegistry.addName(customSeeds[i], Character.toUpperCase(name.charAt(0)) + name.substring(1) + " Seeds");
						if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
							customSeeds[i].setInformation(info);
						}
					}
				}
				if (!success) {
					AgriCore.getLogger("AgriCraft").info(new StringBuffer("Error when adding custom crop: ").append(errorMsg).append(" (line: ").append(cropsRawData[i]).append(")"));
				}
			}
			AgriCore.getLogger("AgriCraft").info("Custom crops registered");
		}
	}

	public static void initGrassSeeds() {
		if (AgriCraftConfig.wipeTallGrassDrops) {
			List seedList = null;
			boolean error = false;
			try {
				Field fieldSeedList = (ForgeHooks.class).getDeclaredField("seedList");
				fieldSeedList.setAccessible(true);
				seedList = (List) fieldSeedList.get(null);
			} catch (NoSuchFieldException | IllegalAccessException e) {
				error = true;
			}
			if (error) {
				AgriCore.getLogger("AgriCraft").info("Error when wiping tall grass drops: couldn't get seed list");
			} else {
				seedList.clear();
				AgriCore.getLogger("AgriCraft").info("Wiped seed entries");
			}
		}
		String[] rawData = IOHelper.getLinesArrayFromData(ConfigurationHandler.readGrassDrops());
		for (String data : rawData) {
			String[] dropData = IOHelper.getData(data);
			boolean success = dropData.length == 2;
			String errorMsg = "Incorrect amount of arguments";
			AgriCore.getLogger("AgriCraft").debug("parsing " + data);
			if (success) {
				ItemStack seedStack = IOHelper.getStack(dropData[0]);
				Item drop = seedStack != null ? seedStack.getItem() : null;
				success = drop != null;
				errorMsg = "Invalid fruit";
				if (success) {
					int meta = seedStack.getItemDamage();
					int weight = Integer.parseInt(dropData[1]);
					MinecraftForge.addGrassSeed(new ItemStack(drop, 1, meta), weight);
					AgriCore.getLogger("AgriCraft").info(new StringBuffer("Registered ").append(Item.itemRegistry.getNameForObject(drop)).append(":").append(meta).append(" as a drop from grass (weight: ").append(weight).append(')'));
				}
			}
			if (!success) {
				AgriCore.getLogger("AgriCraft").info(new StringBuffer("Error when adding grass drop: ").append(errorMsg).append(" (line: ").append(data).append(")"));
			}
		}
	}

}
