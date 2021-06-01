package com.infinityraider.agricraft.capability;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.content.core.ItemDynamicAgriSeed;
import com.infinityraider.agricraft.content.tools.ItemSeedBag;
import com.infinityraider.agricraft.impl.v1.plant.NoPlant;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.capability.IInfSerializableCapabilityImplementation;
import com.infinityraider.infinitylib.utility.ISerializable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.infinityraider.agricraft.content.tools.ItemSeedBag.*;

public class CapabilitySeedBagContents implements IInfSerializableCapabilityImplementation<ItemStack, CapabilitySeedBagContents.Impl> {
    private static final CapabilitySeedBagContents INSTANCE = new CapabilitySeedBagContents();

    public static CapabilitySeedBagContents getInstance() {
        return INSTANCE;
    }

    public static ResourceLocation KEY = new ResourceLocation(AgriCraft.instance.getModId().toLowerCase(), Names.Items.SEED_BAG);

    @CapabilityInject(CapabilitySeedBagContents.Impl.class)
    public static final Capability<CapabilitySeedBagContents.Impl> CAPABILITY = null;

    private CapabilitySeedBagContents() {}

    @Override
    public Class<Impl> getCapabilityClass() {
        return Impl.class;
    }

    @Override
    public Capability<Impl> getCapability() {
        return CAPABILITY;
    }

    @Override
    public boolean shouldApplyCapability(ItemStack stack) {
        return stack.getItem() instanceof ItemSeedBag;
    }

    @Override
    public Impl createNewValue(ItemStack stack) {
        return new Impl();
    }

    @Override
    public ResourceLocation getCapabilityKey() {
        return KEY;
    }

    @Override
    public Class<ItemStack> getCarrierClass() {
        return ItemStack.class;
    }

    public static class Impl implements IContents, ISerializable {
        private IAgriPlant plant;

        private final List<Entry> contents;
        private int count;

        private final Map<Integer, Comparator<Entry>> sorters;
        private int sorterIndex;
        private Comparator<Entry> subSorter;

        private ItemStack firstStack;
        private ItemStack lastStack;

        private Impl() {
            this.plant = NoPlant.getInstance();
            this.contents = Lists.newArrayList();
            this.count = 0;
            this.sorters = Maps.newHashMap();
            this.setSorterIndex(0);
            this.firstStack = ItemStack.EMPTY;
            this.lastStack = ItemStack.EMPTY;
        }

        @Override
        public IAgriPlant getPlant() {
            return this.plant;
        }

        @Override
        public int getCount() {
            return this.count;
        }

        @Override
        public boolean isFull() {
            return this.getCount() >= this.getCapacity();
        }

        @Override
        public ISorter getSorter() {
            return ItemSeedBag.getSorter(this.getSorterIndex());
        }

        @Override
        public int getSorterIndex() {
            return this.sorterIndex;
        }

        @Override
        public void setSorterIndex(int index) {
            this.sorterIndex = index;
            this.subSorter = this.sorters.computeIfAbsent(index, value -> (a, b) -> this.getSorter().compare(a.getGenome(), b.getGenome()));
            this.sort();
        }

        protected void sort() {
            if(this.contents.size() > 0) {
                this.contents.sort(this.subSorter);
                this.firstStack = this.contents.get(0).initializeStack();
                this.firstStack = this.contents.get(this.contents.size() - 1).initializeStack();
            }
        }

        @Override
        public int getSlots() {
            return 2;
        }

        @Nonnull
        @Override
        public ItemStack getStackInSlot(int slot) {
            if (slot == 0) {
                return this.firstStack;
            }
            if (slot == 1) {
                return this.lastStack;
            }
            return ItemStack.EMPTY;
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (this.isFull()) {
                return stack;
            }
            if (this.isItemValid(slot, stack)) {
                return ((ItemDynamicAgriSeed) stack.getItem()).getGenome(stack).map(genome -> {
                    boolean flag = true;
                    int amount = Math.min(this.getCapacity() - this.getCount(), stack.getCount());
                    if (amount <= 0) {
                        return stack;
                    }
                    for (Entry entry : this.contents) {
                        if (entry.matches(genome)) {
                            flag = false;
                            if (!simulate) {
                                entry.add(amount);
                                this.count += amount;
                            }
                            break;
                        }
                    }
                    if (flag && !simulate) {
                        if (!this.plant.isPlant()) {
                            this.plant = ((ItemDynamicAgriSeed) stack.getItem()).getPlant(stack);
                        }
                        this.contents.add(new Entry(genome, amount));
                        this.count += amount;
                        this.sort();
                    }
                    if (amount >= stack.getCount()) {
                        return ItemStack.EMPTY;
                    } else {
                        ItemStack result = stack.copy();
                        result.setCount(result.getCount() - amount);
                        return result;
                    }
                }).orElse(stack);
            }
            return stack;
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if(this.contents.size() >= 1) {
                if(slot == 0) {
                    return this.extractFirstSeed(amount, simulate);
                }
                if(slot == 1) {
                    return this.extractLastSeed(amount, simulate);
                }
            }
            return ItemStack.EMPTY;
        }


        @Nonnull
        @Override
        public ItemStack extractFirstSeed(int amount, boolean simulate) {
            if(this.firstStack.isEmpty()) {
                return ItemStack.EMPTY;
            }
            ItemStack out = this.firstStack.copy();
            if (amount >= this.contents.get(0).getAmount()) {
                // More seeds were requested than there actually are
                Entry entry = simulate ? this.contents.get(0) : this.contents.remove(0);
                out.setCount(entry.getAmount());
                if (!simulate) {
                    this.count -= out.getCount();
                    if (this.contents.size() > 0) {
                        this.firstStack = this.contents.get(0).initializeStack();
                    } else {
                        this.firstStack = ItemStack.EMPTY;
                        this.lastStack = ItemStack.EMPTY;
                        this.plant = NoPlant.getInstance();
                    }
                }
            } else {
                out.setCount(amount);
                if (!simulate) {
                    this.contents.get(0).extract(amount);
                    this.count -= out.getCount();
                }
            }
            return out;
        }

        @Nonnull
        @Override
        public ItemStack extractLastSeed(int amount, boolean simulate) {
            if(this.lastStack.isEmpty()) {
                return ItemStack.EMPTY;
            }
            ItemStack out = this.lastStack.copy();
            if (amount >= this.contents.get(this.contents.size() - 1).getAmount()) {
                // More seeds were requested than there actually are
                Entry entry = simulate ? this.contents.get(this.contents.size() - 1) : this.contents.remove(this.contents.size() - 1);
                out.setCount(entry.getAmount());
                if (!simulate) {
                    this.count -= out.getCount();
                    if (this.contents.size() > 0) {
                        this.lastStack = this.contents.get(this.contents.size() - 1).initializeStack();
                    } else {
                        this.firstStack = ItemStack.EMPTY;
                        this.lastStack = ItemStack.EMPTY;
                        this.plant = NoPlant.getInstance();
                    }
                }
            } else {
                out.setCount(amount);
                if (!simulate) {
                    this.contents.get(this.contents.size() - 1).extract(amount);
                    this.count -= out.getCount();
                }
            }
            return out;
        }

        @Override
        public int getCapacity() {
            return AgriCraft.instance.getConfig().seedBagCapacity();
        }

        @Override
        public int getSlotLimit(int slot) {
            return 64;
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if(stack.getItem() instanceof ItemDynamicAgriSeed) {
                ItemDynamicAgriSeed seed = (ItemDynamicAgriSeed) stack.getItem();
                IAgriPlant stackPlant = seed.getPlant(stack);
                if(stackPlant.isPlant()) {
                    return (!this.getPlant().isPlant()) || this.getPlant() == stackPlant;
                }
            }
            return false;
        }

        @Override
        public void readFromNBT(CompoundNBT tag) {
            this.contents.clear();
            this.count = 0;
            this.firstStack = ItemStack.EMPTY;
            this.lastStack = ItemStack.EMPTY;
            this.plant = tag.contains(AgriNBT.PLANT)
                    ? AgriApi.getPlantRegistry().get(tag.getString(AgriNBT.PLANT)).orElse(NoPlant.getInstance())
                    : NoPlant.getInstance();
            if (this.getPlant().isPlant()) {
                if (tag.contains(AgriNBT.ENTRIES)) {
                    ListNBT entryTags = tag.getList(AgriNBT.ENTRIES, 10);
                    entryTags.stream().filter(entryTag -> entryTag instanceof CompoundNBT)
                            .map(entryTag -> (CompoundNBT) entryTag).
                            forEach(entryTag -> Entry.readFromTag(entryTag).ifPresent(entry -> {
                                this.contents.add(entry);
                                this.count += entry.getAmount();
                            }));
                    if (this.count > 0) {
                        this.firstStack = this.contents.get(0).initializeStack();
                        this.lastStack = this.contents.get(this.contents.size() - 1).initializeStack();
                    }
                } else {
                    this.plant = NoPlant.getInstance();
                }
            }
            this.setSorterIndex(tag.contains(AgriNBT.KEY) ? tag.getInt(AgriNBT.KEY) : 0);
        }

        @Override
        public CompoundNBT writeToNBT() {
            CompoundNBT tag = new CompoundNBT();
            // Write plant
            tag.putString(AgriNBT.PLANT, this.getPlant().getId());
            // Write contents
            ListNBT entryTags = new ListNBT();
            this.contents.forEach(entry -> entryTags.add(entry.writeToTag()));
            tag.put(AgriNBT.ENTRIES, entryTags);
            // Write sorter
            tag.putInt(AgriNBT.KEY, this.getSorterIndex());
            // Return the tag
            return tag;
        }

        private static class Entry {
            private final IAgriGenome genome;
            private int amount;

            protected Entry(IAgriGenome genome, int amount) {
                this.genome = genome;
                this.amount = amount;
            }

            public int getAmount() {
                return this.amount;
            }

            public IAgriGenome getGenome() {
                return this.genome;
            }

            public ItemStack initializeStack() {
                return this.genome.toSeedStack();
            }

            public void add(int amount) {
                this.amount += amount;
            }

            public void extract(int amount) {
                this.amount -= amount;
                this.amount = Math.max(this.amount, 0);
            }

            public boolean matches(IAgriGenome genome) {
                return this.getGenome().equals(genome);
            }

            public CompoundNBT writeToTag() {
                CompoundNBT tag = new CompoundNBT();
                CompoundNBT genomeTag = new CompoundNBT();
                this.getGenome().writeToNBT(genomeTag);
                tag.put(AgriNBT.GENOME, genomeTag);
                tag.putInt(AgriNBT.ENTRIES, this.getAmount());
                return tag;
            }

            public static Optional<Entry> readFromTag(CompoundNBT tag) {
                if(!tag.contains(AgriNBT.GENOME) || !tag.contains(AgriNBT.ENTRIES)) {
                    return Optional.empty();
                }
                IAgriGenome genome = AgriApi.getAgriGenomeBuilder(NoPlant.getInstance()).build();
                if(!genome.readFromNBT(tag.getCompound(AgriNBT.GENOME))) {
                    return Optional.empty();
                }
                int count = tag.getInt(AgriNBT.ENTRIES);
                return Optional.of(new Entry(genome, count));
            }
        }
    }
}
