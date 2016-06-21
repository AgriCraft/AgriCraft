package com.infinityraider.agricraft.reference;

import com.google.common.base.Optional;
import java.util.Collection;
import java.util.List;
import net.minecraft.block.properties.PropertyHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.apiimpl.v1.PlantRegistry;
import com.infinityraider.agricraft.apiimpl.v1.SeedRegistry;
import com.infinityraider.agricraft.farming.cropplant.CropPlantNone;

/**
 * A property for having a crop plant.
 *
 * @author RlonRyan
 */
public class PropertyCropPlant extends PropertyHelper<IAgriPlant> {
	
	/**
	 * None object to avoid NPE's with block states
	 */
	public static final IAgriPlant NONE = CropPlantNone.NONE;
	
	protected PropertyCropPlant(String name) {
		 super(name, IAgriPlant.class);
	}
	
	public static PropertyCropPlant create(String name) {
		return new PropertyCropPlant(name);
	}

	@Override
	public Collection<IAgriPlant> getAllowedValues() {
		List<IAgriPlant> list = PlantRegistry.getInstance().getPlants();
		list.add(NONE);
		return list;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Optional<IAgriPlant> parseValue(String value) {
		if(value.equals("none")) {
			return null;
		}
		ItemStack stack = new ItemStack(Item.itemRegistry.getObject(new ResourceLocation(value)));
		AgriSeed seed = SeedRegistry.getInstance().getSeed(stack);
		if(seed != null) {
			return Optional.of(seed.getPlant());
		} else {
			return null;
		}
	}

	@Override
	public String getName(IAgriPlant value) {
		ItemStack seed = value.getSeed();
		if (seed == null) {
			return "none";
		}
		int id = Item.getIdFromItem(seed.getItem());
		int meta = seed.getItemDamage();
		return "id" + id + "m" + meta;
	}

}
