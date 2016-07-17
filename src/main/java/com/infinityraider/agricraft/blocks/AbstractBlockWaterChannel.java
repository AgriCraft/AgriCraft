/*
 * A intermediate for the water channel blocks.
 */
package com.infinityraider.agricraft.blocks;

import com.agricraft.agricore.util.TypeHelper;
import com.infinityraider.agricraft.reference.AgriProperties;
import com.infinityraider.agricraft.tiles.irrigation.TileEntityChannel;
import java.util.Set;
import net.minecraft.block.properties.IProperty;

/**
 *
 * @author RlonRyan
 */
public abstract class AbstractBlockWaterChannel<T extends TileEntityChannel> extends BlockCustomWood<T> {

	public AbstractBlockWaterChannel(String internalName) {
		super("water_channel_" + internalName, false);
	}
	
	public Set<IProperty> getProperties() {
		return TypeHelper.addAll(
				super.getProperties(),
				AgriProperties.WOOD_TYPE,
				AgriProperties.NORTH,
				AgriProperties.EAST,
				AgriProperties.SOUTH,
				AgriProperties.WEST
		);
	}
	
}
