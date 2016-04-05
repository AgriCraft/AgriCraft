package com.infinityraider.agricraft.compatibility.thaumcraft;

import net.minecraftforge.fml.common.event.FMLInterModComms;
import com.infinityraider.agricraft.compatibility.ModHelper;
import com.infinityraider.agricraft.init.AgriCraftBlocks;
import com.infinityraider.agricraft.reference.AgriCraftMods;
import com.infinityraider.agricraft.reference.Constants;
import net.minecraft.item.ItemStack;


public class ThaumcraftHelper extends ModHelper {

	public ThaumcraftHelper() {
		super(AgriCraftMods.thaumcraft);
	}
	
    @Override
    protected void init() {
		
		// Fix Golems
        FMLInterModComms.sendMessage(
				this.getModId(),
				"harvestClickableCrop",
				new ItemStack(AgriCraftBlocks.blockCrop, 1, Constants.MATURE)
		);

    }

}
