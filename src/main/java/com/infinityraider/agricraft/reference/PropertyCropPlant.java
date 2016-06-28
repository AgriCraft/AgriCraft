package com.infinityraider.agricraft.reference;

import com.google.common.base.Optional;
import java.util.Collection;
import java.util.List;
import net.minecraft.block.properties.PropertyHelper;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.apiimpl.v1.PlantRegistry;
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
	public Optional<IAgriPlant> parseValue(String value) {
		return Optional.fromNullable(PlantRegistry.getInstance().getPlant(value));
	}

	@Override
	public String getName(IAgriPlant value) {
		return value.getId();
	}

}
