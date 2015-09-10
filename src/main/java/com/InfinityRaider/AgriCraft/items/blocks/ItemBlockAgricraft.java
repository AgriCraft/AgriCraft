package com.InfinityRaider.AgriCraft.items.blocks;

import com.InfinityRaider.AgriCraft.creativetab.AgriCraftTab;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

/**
 * The root item for all AgriCraft ItemBlocks.
 * <p>
 * This class may appear pointless, but it is key in setting up a proper inheritance chain for the mod.
 * This class allows for all of the mod's ItemBlocks to refer to the same thing, as well as allow for the future adding of common elements.
 * </p>
 */
public class ItemBlockAgricraft extends ItemBlock {
	
    /**
     * The default constructor.
     * A super call to this is generally all that is needed in subclasses.
     * 
     * @param block the block associated with this item.
     */
    public ItemBlockAgricraft(Block block) {
        super(block);
        this.setCreativeTab(AgriCraftTab.agriCraftTab);
    }

}