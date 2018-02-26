package com.infinityraider.agricraft.items.blocks;

import com.infinityraider.agricraft.items.tabs.AgriTabs;
import com.infinityraider.infinitylib.block.IInfinityBlock;
import com.infinityraider.infinitylib.item.IInfinityItem;
import com.infinityraider.infinitylib.item.IItemWithModel;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

/**
 * The root item for all AgriCraft ItemBlocks.
 * <p>
 * This class may appear pointless, but it is key in setting up a proper inheritance chain for the
 * mod. This class allows for all of the mod's ItemBlocks to refer to the same thing, as well as
 * allow for the future adding of common elements.
 * </p>
 */
public class ItemBlockAgricraft extends ItemBlock implements IInfinityItem, IItemWithModel {

    /**
     * The default constructor. A super call to this is generally all that is needed in subclasses.
     *
     * @param <T>
     * @param block the block associated with this item.
     */
    public <T extends Block & IInfinityBlock> ItemBlockAgricraft(Block block) {
        super(block);
        this.setCreativeTab(AgriTabs.TAB_AGRICRAFT);
    }

    @Override
    public String getInternalName() {
        return ((IInfinityBlock) this.block).getInternalName();
    }

    @Override
    public boolean isEnabled() {
        return ((IInfinityBlock) this.block).isEnabled();
    }

}
