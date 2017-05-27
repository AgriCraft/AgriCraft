/*
 */
package com.infinityraider.agricraft.init;

import com.agricraft.agricore.util.ReflectionHelper;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.init.Blocks;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Class to update OreDictionary to be more, better.
 */
public class AgriOreDict {

    public static void upgradeOreDict() {
        ReflectionHelper.forEachValueIn(Blocks.class, BlockFence.class, fence -> OreDictionary.registerOre("fenceWood", fence));
        ReflectionHelper.forEachValueIn(Blocks.class, BlockFenceGate.class, gate -> OreDictionary.registerOre("fenceGateWood", gate));
    }

}
