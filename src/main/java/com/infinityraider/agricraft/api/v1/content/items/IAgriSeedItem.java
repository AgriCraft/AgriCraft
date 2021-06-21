package com.infinityraider.agricraft.api.v1.content.items;

import com.infinityraider.agricraft.api.v1.genetics.IAgriGeneCarrierItem;
import net.minecraft.item.Item;

/**
 * Implemented in the AgriCraft seed Item object
 * To obtain, check if an ItemStack's item is an instance of this class and cast.
 */
public interface IAgriSeedItem extends IAgriGeneCarrierItem {
    /**
     * @return this, but cast to Item
     */
    default Item asItem() {
        return (Item) this;
    }
}
