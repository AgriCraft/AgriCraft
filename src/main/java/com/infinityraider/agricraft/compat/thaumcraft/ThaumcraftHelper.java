package com.infinityraider.agricraft.compat.thaumcraft;

import net.minecraftforge.fml.common.event.FMLInterModComms;
import com.infinityraider.agricraft.compat.ModHelper;
import com.infinityraider.agricraft.init.AgriBlocks;
import com.infinityraider.agricraft.reference.Constants;
import net.minecraft.item.ItemStack;


public class ThaumcraftHelper extends ModHelper {

	public ThaumcraftHelper() {
		super("Thaumcraft");
	}
	
    @Override
    protected void init() {
		
		// Fix Golems
        FMLInterModComms.sendMessage(this.getModId(),
				"harvestClickableCrop",
				new ItemStack(AgriBlocks.blockCrop, 1, Constants.MATURE)
		);

    }

}
