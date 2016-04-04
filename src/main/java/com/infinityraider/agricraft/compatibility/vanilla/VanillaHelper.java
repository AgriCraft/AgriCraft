package com.infinityraider.agricraft.compatibility.vanilla;

import com.infinityraider.agricraft.compatibility.ModHelper;
import com.infinityraider.agricraft.farming.CropPlantHandler;
import com.infinityraider.agricraft.reference.AgriCraftMods;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemSeeds;
import net.minecraftforge.oredict.OreDictionary;

public class VanillaHelper extends ModHelper {

	public VanillaHelper() {
		super(AgriCraftMods.VANILLA);
	}
	
	@Override
	protected void preInit() {
		
		//Register vanilla plants. Now with less duplication.
        OreDictionary.registerOre("seedMelon", Items.melon_seeds);
        OreDictionary.registerOre("cropMelon", Items.melon);
        OreDictionary.registerOre("seedPumpkin", Items.pumpkin_seeds);
        OreDictionary.registerOre("cropPumpkin", Blocks.pumpkin);
		
		// Register the plants.
		CropPlantHandler.suppressedRegisterPlant(new CropPlantVanilla((ItemSeeds) Items.wheat_seeds, "wheat"));
        CropPlantHandler.suppressedRegisterPlant(new CropPlantVanillaStem((ItemSeeds) Items.melon_seeds, Blocks.melon_block));
        CropPlantHandler.suppressedRegisterPlant(new CropPlantVanillaStem((ItemSeeds) Items.pumpkin_seeds, Blocks.pumpkin));
        CropPlantHandler.suppressedRegisterPlant(new CropPlantNetherWart());
		
	}

}
