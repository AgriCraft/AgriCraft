/*
 */
package com.infinityraider.agricraft.core;

import com.agricraft.agricore.util.AgriValidator;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 *
 * 
 */
public class ModValidator implements AgriValidator {

	@Override
	public boolean isValidBlock(String block) {
		String[] parts = block.split(":");
		if (parts.length < 2) {
			return false;
		} else {
			Block b = GameRegistry.findBlock(parts[0], parts[1]);
			//AgriCore.getLogger("AgriCraft").debug(b);
			return b != null;
		}
	}

	@Override
	public boolean isValidItem(String item) {
		String[] parts = item.split(":");
		if (parts.length < 2) {
			return false;
		} else {
			Item i = GameRegistry.findItem(parts[0], parts[1]);
			//AgriCore.getLogger("AgriCraft").debug(i);
			return i != null;
		}
	}

	@Override
	public boolean isValidTexture(String texture) {
        try {
            ResourceLocation temp = new ResourceLocation(texture);
            return true;
        } catch (NullPointerException e) {
            return false;
        }
	}
	
}
