package com.infinityraider.agricraft.content.core;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.content.items.IAgriJournalItem;
import com.infinityraider.agricraft.api.v1.event.JournalContentsUpdatedEvent;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.capability.CapabilityJournalData;
import com.infinityraider.agricraft.content.AgriTabs;
import com.infinityraider.agricraft.impl.v1.journal.*;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.agricraft.render.items.journal.JournalItemRenderer;
import com.infinityraider.infinitylib.item.ItemBase;
import com.infinityraider.infinitylib.render.item.InfItemRenderer;
import com.infinityraider.infinitylib.utility.ISerializable;
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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.loading.FMLEnvironment;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
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
            if(AgriCraft.instance.proxy().toggleJournalObserving(player, hand)) {
                return ActionResult.resultConsume(stack);
            }
        }
        return ActionResult.resultPass(stack);
    }

    public LazyOptional<JournalData> getJournalData(@Nonnull ItemStack journal) {
        return CapabilityJournalData.getInstance().getCapability(journal);
    }

    @Override
    public boolean isPlantDiscovered(@Nonnull ItemStack journal, @Nullable IAgriPlant plant) {
        return this.getJournalData(journal).map(data -> data.isPlantDiscovered(plant)).orElse(false);
    }

    @Override
    public void addEntry(@Nonnull ItemStack journal, @Nullable IAgriPlant plant) {
        if(plant == null) {
            return;
        }
        this.getJournalData(journal).ifPresent(data -> data.addEntry(plant));
    }

    @Nonnull
    @Override
    public List<IAgriPlant> getDiscoveredSeeds(@Nonnull ItemStack journal) {
        return this.getJournalData(journal).map(JournalData::getPlants).orElse(Collections.emptyList());
    }

    @Override
    public int getCurrentPageIndex(@Nonnull ItemStack journal) {
        return this.getJournalData(journal).map(JournalData::getCurrentIndex).orElse(0);
    }

    @Override
    public void setCurrentPageIndex(@Nonnull ItemStack journal, int index) {
        this.getJournalData(journal).ifPresent(data -> data.setCurrentIndex(index));
    }

    @Nonnull
    @Override
    public List<IPage> getPages(@Nonnull ItemStack journal) {
        return this.getJournalData(journal).map(JournalData::getPages).orElse(Collections.emptyList());
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player) {
        return true;
    }

    // Make sure NBT is synced to the client
    @Override
    public boolean shouldSyncTag() {
        return true;
    }

    /*
     In dedicated server play, there is a bug where the journal capability data is wiped whenever the player opens their
     inventory, for this reason, the getShareTag and readShareTag methods are overridden, but only run logic on the
     server and client side respectively.
     If these methods are not overridden, the client does not receive the capability data, if they are overridden,
     but run their logic regardless the side, the aforementioned bug occurs.

     In single player, overriding these methods this way will have no effect, as the share tag is never written, it will
     not exist when it is read.
     */

    // Serialize the capabilities only on the server to be sent to the client
    @Nullable
    @Override
    public CompoundNBT getShareTag(ItemStack stack) {
        if(FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
            final CompoundNBT tag = stack.hasTag() ? stack.getTag() : new CompoundNBT();
            if (tag != null) {
                // should always be the case
                this.getJournalData(stack).ifPresent(data -> tag.put(AgriNBT.CONTENTS, data.writeToNBT()));
            }
            return tag;
        } else {
            return super.getShareTag(stack);
        }
    }

    // Read the capabilities on the client only
    @Override
    public void readShareTag(ItemStack stack, @Nullable CompoundNBT tag) {
        if(FMLEnvironment.dist == Dist.CLIENT) {
            if (tag != null && tag.contains(AgriNBT.CONTENTS)) {
                // deserialize the journal data
                this.getJournalData(stack).ifPresent(data -> data.readFromNBT(tag.getCompound(AgriNBT.CONTENTS)));
            }
        } else {
            super.readShareTag(stack, tag);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public InfItemRenderer getItemRenderer() {
        return JournalItemRenderer.getInstance();
    }

    public static class JournalData implements ISerializable {
        private final ItemStack journal;

        private int index;
        private List<IAgriPlant> plants;
        private List<IPage> pages;

        public JournalData(ItemStack journal, List<IAgriPlant> plants) {
            this.journal = journal;
            this.index = 0;
            this.plants = plants;
            this.initializePages();
        }

        public IAgriJournalItem getJournalItem() {
            return (IAgriJournalItem) this.getJournalStack().getItem();
        }

        public ItemStack getJournalStack() {
            return this.journal;
        }

        public int getCurrentIndex() {
            return this.index;
        }

        public void setCurrentIndex(int index) {
            this.index = Math.min(this.getPages().size() -1, Math.max(0, index));
        }

        @Nonnull
        public List<IAgriPlant> getPlants() {
            return this.plants;
        }

        @Nonnull
        public List<IPage> getPages() {
            return this.pages;
        }

        public void addEntry(IAgriPlant plant) {
            if(!this.isPlantDiscovered(plant)) {
                this.plants.add(plant);
                this.initializePages();
            }
        }

        public boolean isPlantDiscovered(IAgriPlant plant) {
            return this.getPlants().contains(plant);
        }

        @Override
        public void readFromNBT(CompoundNBT tag) {
            // read the plants
            if(tag.contains(AgriNBT.ENTRIES)) {
                this.plants = tag.getList(AgriNBT.ENTRIES, 10).stream()
                        // filter for CompoundNBT, should always be the case, but is here for safety
                        .filter(plantTag -> plantTag instanceof CompoundNBT)
                        // cast to CompoundNBT
                        .map(plantTag -> (CompoundNBT) plantTag)
                        // fetch plant from NBT
                        .map(plantTag -> AgriApi.getPlantRegistry().get(plantTag.getString(AgriNBT.PLANT)))
                        // filter for presence of plant in optional
                        .filter(Optional::isPresent)
                        // get plants from optionals
                        .map(Optional::get)
                        // collect to list
                        .collect(Collectors.toList());
            } else {
                this.plants = new ArrayList<>();
            }
            // Populate pages
            this.initializePages();
            // read the index
            this.setCurrentIndex(tag.contains(AgriNBT.INDEX) ? tag.getInt(AgriNBT.INDEX) : 0);
        }

        @Override
        public CompoundNBT writeToNBT() {
            // Create new tag
            CompoundNBT tag = new CompoundNBT();
            // Write index
            tag.putInt(AgriNBT.INDEX, this.getCurrentIndex());
            // Write plants
            ListNBT list = new ListNBT();
            this.getPlants().forEach(plant -> {
                // create new entry for the plant
                CompoundNBT plantTag = new CompoundNBT();
                plantTag.putString(AgriNBT.PLANT, plant.getId());
                // add new entry to the list
                list.add(plantTag);
            });
            tag.put(AgriNBT.ENTRIES, list);
            // return the tag
            return tag;
        }

        protected void initializePages() {
            // Initialize fixed pages
            List<IPage> pages = Lists.newArrayList();
            pages.add(FrontPage.INSTANCE);
            pages.add(IntroductionPage.INSTANCE);
            pages.add(GeneticsPage.INSTANCE);
            pages.add(GrowthReqsPage.INSTANCE);

            // Add pages for plants
            this.plants.stream().sorted().forEach(plant -> {
                PlantPage page = new PlantPage(plant, this.plants);
                pages.add(page);
                List<List<IAgriPlant>> mutations = page.getOffPageMutations();
                int size = mutations.size();
                if (size > 0) {
                    int remaining = size;
                    int from = 0;
                    int to = Math.min(remaining, MutationsPage.LIMIT);
                    while (remaining > 0) {
                        pages.add(new MutationsPage(mutations.subList(from, to)));
                        remaining -= (to - from);
                        from = to;
                        to = from + Math.min(remaining, MutationsPage.LIMIT);
                    }
                }
            });

            // Fire event to allow modification of journal contents
            JournalContentsUpdatedEvent event = new JournalContentsUpdatedEvent(this.getJournalStack(), this.getJournalItem(), pages);
            MinecraftForge.EVENT_BUS.post(event);

            // Set the journal data
            this.pages = ImmutableList.copyOf(event.getPages());
        }

        public static JournalData createFromLegacyTag(ItemStack stack) {
            return new JournalData(stack, getPlantsFromLegacyTag(stack));
        }

        private static List<IAgriPlant> getPlantsFromLegacyTag(ItemStack stack) {
            List<IAgriPlant> plants = Lists.newArrayList();
            CompoundNBT existingTag = stack.getTag();
            if(existingTag != null && existingTag.contains(AgriNBT.ENTRIES)) {
                // extract plants from legacy tags
                plants.addAll(existingTag.getList(AgriNBT.ENTRIES, 10).stream()
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
                        // collect
                        .collect(Collectors.toList())
                );
                // Remove legacy tag
                existingTag.remove(AgriNBT.ENTRIES);
                if(existingTag.keySet().isEmpty()) {
                    stack.setTag(null);
                }
            }
            return plants;
        }
    }
}
