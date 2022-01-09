package com.infinityraider.agricraft.impl.v1.journal;

import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.content.items.IAgriJournalItem;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.List;

public final class MutationsPage implements IAgriJournalItem.IPage {
    public static final int LIMIT = 18;

    public static final ResourceLocation ID = new ResourceLocation(AgriCraft.instance.getModId(), "mutation_page");

    private final List<List<IAgriPlant>> mutationsLeft;
    private final List<List<IAgriPlant>> mutationsRight;

    public MutationsPage(List<List<IAgriPlant>> mutations) {
        int count = mutations.size();
        if(count <= LIMIT/2) {
            this.mutationsLeft = mutations;
            this.mutationsRight = ImmutableList.of();
        } else {
            this.mutationsLeft = mutations.subList(0, LIMIT/2 - 1);
            this.mutationsRight = mutations.subList(LIMIT/2, count - 1);
        }
    }

    @Nonnull
    @Override
    public ResourceLocation getDataDrawerId() {
        return ID;
    }

    public List<List<IAgriPlant>> getMutationsLeft() {
        return this.mutationsLeft;
    }

    public List<List<IAgriPlant>> getMutationsRight() {
        return this.mutationsRight;
    }

    @Override
    public void onPageOpened(PlayerEntity player, ItemStack stack, IAgriJournalItem journal) {}
}
