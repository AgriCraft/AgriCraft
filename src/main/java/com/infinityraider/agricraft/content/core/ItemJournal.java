package com.infinityraider.agricraft.content.core;

import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.api.v1.items.IAgriJournalItem;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.content.AgriTabs;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.item.ItemBase;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemJournal extends ItemBase implements IAgriJournalItem {
    public ItemJournal() {
        super(Names.Items.JOURNAL, new Properties()
                .group(AgriTabs.TAB_AGRICRAFT)
                .maxStackSize(1)
        );
    }

    @Override
    public boolean isSeedDiscovered(@Nonnull ItemStack journal, @Nullable IAgriPlant plant) {
        return false;
    }

    @Override
    public void addEntry(@Nonnull ItemStack journal, @Nullable IAgriPlant plant) {

    }

    @Nonnull
    @Override
    public List<IAgriPlant> getDiscoveredSeeds(@Nonnull ItemStack journal) {
        return ImmutableList.of();
    }
}
