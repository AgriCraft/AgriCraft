package com.infinityraider.agricraft.content.core;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.items.IAgriJournalItem;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.content.AgriTabs;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.agricraft.render.items.JournalRenderer;
import com.infinityraider.infinitylib.item.ItemBase;
import com.infinityraider.infinitylib.render.item.InfItemRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ItemJournal extends ItemBase implements IAgriJournalItem {
    public ItemJournal() {
        super(Names.Items.JOURNAL, new Properties()
                .group(AgriTabs.TAB_AGRICRAFT)
                .maxStackSize(1)
        );
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull PlayerEntity player, @Nonnull Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if(player.isSneaking()) {
            return ActionResult.resultPass(stack);
        }
        if(world.isRemote()) {
            if(AgriCraft.instance.proxy().toggleJournalObserving(stack, hand)) {
                return ActionResult.resultConsume(stack);
            }
        }
        return ActionResult.resultPass(stack);
    }

    @Override
    public boolean isPlantDiscovered(@Nonnull ItemStack journal, @Nullable IAgriPlant plant) {
        // fetch entry list and stream
        return plant != null && this.getTag(journal).getList(AgriNBT.ENTRIES, 10).stream()
                // filter for CompoundNBT, should always be the case, but is here for safety
                .filter(tag -> tag instanceof CompoundNBT)
                // cast to CompoundNBT
                .map(tag -> (CompoundNBT) tag)
                // fetch plant from NBT
                .map(tag -> AgriApi.getPlantRegistry().get(tag.getString(AgriNBT.PLANT)))
                // filter for presence of plant in optional
                .filter(Optional::isPresent)
                // get plants from optionals
                .map(Optional::get)
                // check if any plants match
                .anyMatch(entry -> entry.equals(plant));
    }

    @Override
    public void addEntry(@Nonnull ItemStack journal, @Nullable IAgriPlant plant) {
        if(plant == null) {
            return;
        }
        if(!this.isPlantDiscovered(journal, plant)) {
            // fetch ItemStack NBT tag
            CompoundNBT tag = this.getTag(journal);
            // fetch list of entries from NBT tag
            ListNBT listTag = tag.getList(AgriNBT.ENTRIES, 10);
            // create new entry for the plant
            CompoundNBT plantTag = new CompoundNBT();
            plantTag.putString(AgriNBT.PLANT, plant.getId());
            // add new entry to the list
            listTag.add(plantTag);
            // put the list back in the ItemStack tag
            tag.put(AgriNBT.ENTRIES, listTag);
        }
    }

    @Nonnull
    @Override
    public List<IAgriPlant> getDiscoveredSeeds(@Nonnull ItemStack journal) {
        // fetch entry list and stream
        return this.getTag(journal).getList(AgriNBT.ENTRIES, 10).stream()
                // filter for CompoundNBT, should always be the case, but is here for safety
                .filter(tag -> tag instanceof CompoundNBT)
                // cast to CompoundNBT
                .map(tag -> (CompoundNBT) tag)
                // fetch plant from NBT
                .map(tag -> AgriApi.getPlantRegistry().get(tag.getString(AgriNBT.PLANT)))
                // filter for presence of plant in optional
                .filter(Optional::isPresent)
                // get plants from optionals
                .map(Optional::get)
                // collect to list
                .collect(Collectors.toList());
    }

    @Nonnull
    protected CompoundNBT getTag(ItemStack journal) {
        CompoundNBT tag = journal.getTag();
        if(tag == null) {
            tag = new CompoundNBT();
            journal.setTag(tag);
        }
        if(!tag.contains(AgriNBT.ENTRIES)) {
            tag.put(AgriNBT.ENTRIES, new ListNBT());
        }
        return tag;
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player) {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public InfItemRenderer getItemRenderer() {
        return JournalRenderer.getInstance();
    }
}
