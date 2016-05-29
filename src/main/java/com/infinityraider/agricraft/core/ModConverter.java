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
	public Object toStack(String element) {
		return GameRegistry.makeItemStack(element, 0, 0, "");
	}

	@Override
	public Object toStack(String element, int amount, int meta) {
		return GameRegistry.makeItemStack(element, meta, amount, "");
	}

}
