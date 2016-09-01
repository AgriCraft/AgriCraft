/*
 */
package com.infinityraider.agricraft.core;

import net.minecraftforge.fml.common.registry.GameRegistry;
import com.agricraft.agricore.util.AgriConverter;

/**
 *
 * @author RlonRyan
 */
public class ModConverter implements AgriConverter {

	@Override
	public Object toStack(String element, int meta, int amount, String tags) {
		return GameRegistry.makeItemStack(element, meta, amount, tags);
	}

}
