package com.InfinityRaider.AgriCraft.compatibility.thaumcraft;

import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.reference.Names;
import cpw.mods.fml.common.event.FMLInterModComms;
import net.minecraft.item.ItemStack;

public class ThaumcraftHelper extends ModHelper {
    @Override
    protected void init() {
        FMLInterModComms.sendMessage(Names.Mods.thaumcraft, "harvestClickableCrop", new ItemStack(Blocks.blockCrop, 1, 7));
    }

    @Override
    protected void initPlants() {
        Aspects.registerAspects();
    }

    @Override
    protected String modId() {
        return Names.Mods.thaumcraft;
    }
}
