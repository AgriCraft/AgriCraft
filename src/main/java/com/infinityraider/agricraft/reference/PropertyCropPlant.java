package com.infinityraider.agricraft.reference;

import com.google.common.base.Optional;
import com.infinityraider.agricraft.farming.CropPlantHandler;
import java.util.Collection;
import java.util.List;
import net.minecraft.block.properties.PropertyHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.infinityraider.agricraft.api.v1.IAgriCraftPlant;

/**
 * A property for having a crop plant.
 *
 * @author RlonRyan
 */
public class PropertyCropPlant extends PropertyHelper<IAgriCraftPlant> {
	
	protected PropertyCropPlant(String name) {
		 super(name, IAgriCraftPlant.class);
	}
	
	public static PropertyCropPlant create(String name) {
		return new PropertyCropPlant(name);
	}

	@Override
	public Collection<IAgriCraftPlant> getAllowedValues() {
		List<IAgriCraftPlant> list = CropPlantHandler.getPlantsUpToTier(5);
		list.add(CropPlantHandler.NONE);
		return list;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Optional<IAgriCraftPlant> parseValue(String value) {
		if(value.equals("none")) {
			return null;
		}
		return Optional.fromNullable(CropPlantHandler.getPlantFromStack(new ItemStack(Item.itemRegistry.getObject(new ResourceLocation(value)))));
	}

	@Override
	public String getName(IAgriCraftPlant value) {
		ItemStack seed = value.getSeed();
		if (seed == null) {
			return "none";
		}
		int id = Item.getIdFromItem(seed.getItem());
		int meta = seed.getItemDamage();
		return "id" + id + "m" + meta;
	}

}
