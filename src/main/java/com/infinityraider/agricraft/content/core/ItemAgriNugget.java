package com.infinityraider.agricraft.content.core;

import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.item.ItemBase;
import net.minecraft.item.ItemGroup;

public class ItemAgriNugget extends ItemBase {
    public ItemAgriNugget(String name) {
        super(Names.Items.NUGGET + "_" + name, new Properties().group(ItemGroup.MATERIALS));
    }
}
