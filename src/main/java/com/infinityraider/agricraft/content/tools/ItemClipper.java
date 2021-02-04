package com.infinityraider.agricraft.content.tools;

import com.infinityraider.agricraft.api.v1.items.IAgriClipperItem;
import com.infinityraider.agricraft.content.AgriTabs;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.item.ItemBase;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;

import javax.annotation.Nonnull;

public class ItemClipper extends ItemBase implements IAgriClipperItem {
    public ItemClipper() {
        super(Names.Items.CLIPPER, new Properties()
                .group(AgriTabs.TAB_AGRICRAFT)
                .maxStackSize(1)
        );
    }

    @Override
    public ActionResultType onItemUse(@Nonnull ItemUseContext context) {
        //TODO
        return ActionResultType.PASS;
    }
}
