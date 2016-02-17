package com.InfinityRaider.AgriCraft.reference;

import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlant;
import java.util.Collection;
import java.util.List;
import net.minecraft.block.properties.PropertyHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * A property for having a crop plant.
 *
 * @author RlonRyan
 */
public class PropertyCropPlant extends PropertyHelper<CropPlant> {
	
	protected PropertyCropPlant(String name) {
		 super(name, CropPlant.class);
	}
	
	public static PropertyCropPlant create(String name) {
		return new PropertyCropPlant(name);
	}

	@Override
	public Collection<CropPlant> getAllowedValues() {
		List<CropPlant> list = CropPlantHandler.getPlantsUpToTier(5);
		list.add(CropPlantHandler.NONE);
		return list;
	}

	@Override
	public String getName(CropPlant value) {
		ItemStack seed = value.getSeed();
		if (seed == null) {
			return "NONE";
		}
		return Item.itemRegistry.getNameForObject(seed.getItem()).toString() + ":" + seed.getItemDamage();
	}

}
