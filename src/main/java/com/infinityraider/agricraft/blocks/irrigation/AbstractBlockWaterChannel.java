/*
 * A intermediate for the water channel blocks.
 */
package com.infinityraider.agricraft.blocks.irrigation;

import com.infinityraider.agricraft.blocks.BlockCustomWood;
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
	
	@Override
	public final void addPropertiesWood(Set<IProperty> properties) {
		addPropertiesChannel(properties);
		properties.add(AgriProperties.NORTH);
		properties.add(AgriProperties.EAST);
		properties.add(AgriProperties.SOUTH);
		properties.add(AgriProperties.WEST);
	}
	
	protected void addPropertiesChannel(Set<IProperty> properties) {}
	
}
