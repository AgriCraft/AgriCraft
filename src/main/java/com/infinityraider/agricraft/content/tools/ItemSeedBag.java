package com.infinityraider.agricraft.content.tools;

import com.google.common.collect.Lists;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.content.AgriTabs;
import com.infinityraider.agricraft.content.core.ItemDynamicAgriSeed;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.item.ItemBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.List;

public class ItemSeedBag extends ItemBase {
    public ItemSeedBag() {
        super(Names.Items.SEED_BAG, new Properties()
                .group(AgriTabs.TAB_AGRICRAFT)
                .maxStackSize(1)
        );
    }

    public static class Contents implements IItemHandler {
        private IAgriPlant plant;

        private final List<Entry> contents = Lists.newArrayList();
        private int count;
        private Comparator<Entry> sorter;

        private IAgriGenome nextGenome;
        private ItemStack nextStack;

        protected Contents(ItemStack stack) {

        }

        public IAgriPlant getPlant() {
            return this.plant;
        }

        public int getCount() {
            return this.count;
        }

        public Comparator<Entry> getSorter() {
            return this.sorter;
        }

        @Override
        public int getSlots() {
            return 1;
        }

        @Nonnull
        @Override
        public ItemStack getStackInSlot(int slot) {
            return this.nextStack;
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (this.isItemValid(slot, stack)) {
                return ((ItemDynamicAgriSeed) stack.getItem()).getGenome(stack).map(genome -> {
                    boolean flag = true;
                    for(Entry entry : this.contents) {
                        if(entry.matches(genome)) {
                            flag = !simulate;
                            if(!simulate) {
                                entry.add(stack.getCount());
                                this.count += stack.getCount();
                            }
                            break;
                        }
                    }
                    if(flag) {
                        this.contents.add(new Entry(genome, stack.getCount()));
                        this.count += stack.getCount();
                        this.contents.sort(this.getSorter());
                    }
                    return ItemStack.EMPTY;
                }).orElse(stack);
            }
            return stack;
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if(this.contents.size() >= 1) {
                ItemStack out = this.nextStack.copy();
                if (amount >= this.contents.get(0).getAmount()) {
                    // More seeds were requested than there actually are
                    Entry entry = simulate ? this.contents.get(0) : this.contents.remove(0);
                    out.setCount(entry.getAmount());
                    if(!simulate) {
                        this.count -= out.getCount();
                        if (this.contents.size() > 0) {
                            this.nextStack = this.contents.get(0).initializeStack();
                        } else {
                            this.nextStack = ItemStack.EMPTY;
                        }
                    }
                } else {
                    out.setCount(amount);
                    if(!simulate) {
                        this.contents.get(0).extract(amount);
                        this.count -= out.getCount();
                    }
                }
                return out;
            }
            return ItemStack.EMPTY;
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
    }

    public static class Entry {
        private final IAgriGenome genome;
        private int amount;

        public Entry(IAgriGenome genome, int amount) {
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
            return new AgriSeed(this.genome).toStack();
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
    }
}
