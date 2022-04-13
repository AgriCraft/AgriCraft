package com.infinityraider.agricraft.content.tools;

import com.google.common.collect.Lists;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.content.items.IAgriSeedBagItem;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import com.infinityraider.agricraft.capability.CapabilitySeedBagContents;
import com.infinityraider.agricraft.content.AgriBlockRegistry;
import com.infinityraider.agricraft.content.AgriEnchantmentRegistry;
import com.infinityraider.agricraft.content.AgriTabs;
import com.infinityraider.agricraft.content.core.ItemDynamicAgriSeed;
import com.infinityraider.agricraft.content.core.TileEntityCropSticks;
import com.infinityraider.agricraft.impl.v1.genetics.AgriGeneRegistry;
import com.infinityraider.agricraft.impl.v1.genetics.GeneSpecies;
import com.infinityraider.agricraft.impl.v1.plant.NoPlant;
import com.infinityraider.agricraft.reference.AgriToolTips;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.item.ItemBase;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemSeedBag extends ItemBase implements IAgriSeedBagItem {
    private static final Component NAME_DEACTIVATED = new TranslatableComponent("item.agricraft.agri_seed_bag_inactive");

    private final List<Sorter> sorters;

    public ItemSeedBag() {
        super(Names.Items.SEED_BAG, new Properties()
                .tab(AgriTabs.TAB_AGRICRAFT)
                .stacksTo(1)
        );
        this.sorters = Lists.newArrayList();
        this.addSorter(DEFAULT_SORTER);
    }

    @Override
    public Contents getContents(ItemStack stack) {
        return CapabilitySeedBagContents.getInstance().getCapability(stack).map(impl -> (Contents) impl).orElse(EMPTY);
    }

    @Override
    public boolean isActivated(ItemStack stack) {
        return EnchantmentHelper.getEnchantments(stack).containsKey(AgriEnchantmentRegistry.getInstance().seed_bag.get());
    }

    @Override
    public boolean incrementSorter(ItemStack stack, int delta) {
        if(this.isActivated(stack)) {
            Contents contents = this.getContents(stack);
            int newPos = contents.getSorterIndex() + delta;
            newPos = (newPos < 0) ? (newPos + sorters.size()) : (newPos % sorters.size());
            contents.setSorterIndex(newPos);
            return true;
        }
        return false;
    }

    @Override
    public Sorter getSorter(int index) {
        return sorters.get(index % sorters.size());
    }

    @Override
    public int addSorter(Sorter sorter) {
        if(sorters.contains(sorter)) {
            return sorters.indexOf(sorter);
        } else {
            sorters.add(sorter);
            return sorters.size() - 1;
        }
    }

    @Override
    public int addSorter(IAgriStat stat) {
        return addSorter(new StatSorter(stat));
    }

    @Nonnull
    @Override
    public InteractionResult useOn(@Nonnull UseOnContext context) {
        Contents contents = this.getContents(context.getItemInHand());
        if(this.isActivated(context.getItemInHand())) {
            InteractionHand hand = context.getHand();
            Player player = context.getPlayer();
            if (hand == InteractionHand.OFF_HAND) {
                // From off hand: interact with main to insert / extract seeds
                if (player != null && this.attemptExtractOrInsertSeed(player, contents)) {
                    return InteractionResult.SUCCESS;
                }
            } else {
                // From main hand: interact with the world to plant the seed
                if (this.attemptPlantSeed(context.getLevel(), context.getClickedPos(), contents, player)) {
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level world, @Nonnull Player player, @Nonnull InteractionHand hand) {
        // From off hand: interact with main to insert / extract seeds
        if(hand == InteractionHand.OFF_HAND) {
            ItemStack stack = player.getItemInHand(hand);
            Contents contents = this.getContents(stack);
            if(this.isActivated(stack) && this.attemptExtractOrInsertSeed(player, contents)) {
                return InteractionResultHolder.success(stack);
            }
        }
        // From main hand: world-interaction behaviour which is not handled in this method
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    protected boolean attemptPlantSeed(Level world, BlockPos pos, Contents contents, @Nullable  Player player) {
        if (contents.getCount() > 0) {
            BlockEntity tile = world.getBlockEntity(pos);
            if (tile instanceof TileEntityCropSticks) {
                // Attempt to plant on crop sticks
                if(this.attemptPlantOnCrops((TileEntityCropSticks) tile, contents.extractFirstSeed(1, true), player)) {
                    contents.extractFirstSeed(1, false);
                    return true;
                }
            } else {
                BlockPos up = pos.above();
                tile = world.getBlockEntity(up);
                if(tile instanceof TileEntityCropSticks) {
                    // Attempt to plant above
                    if(this.attemptPlantOnCrops((TileEntityCropSticks) tile, contents.extractFirstSeed(1, true), player)) {
                        contents.extractFirstSeed(1, false);
                        return true;
                    }
                } else if(tile == null && AgriCraft.instance.getConfig().allowPlantingOutsideCropSticks()) {
                    // Attempt to plant on soil
                    if(this.attemptPlantOnSoil(world, pos, contents.extractFirstSeed(1, true), player)) {
                        contents.extractFirstSeed(1, false);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected boolean attemptPlantOnCrops(IAgriCrop crop, ItemStack seedStack, @Nullable  Player player) {
        return AgriApi.getGenomeAdapterizer().valueOf(seedStack).map(seed -> crop.plantGenome(seed, player)).orElse(false);
    }

    protected boolean attemptPlantOnSoil(Level world, BlockPos pos, ItemStack seedStack, @Nullable  Player player) {
        BlockPos up = pos.above();
        return AgriApi.getSoil(world, pos).map(soil -> {
            BlockState newState = AgriBlockRegistry.getInstance().crop_plant.get().getStateForPlacement(world, up);
            if (newState != null && world.setBlock(up, newState, 11)) {
                boolean success = AgriApi.getCrop(world, up).map(crop ->
                        this.attemptPlantOnCrops(crop, seedStack, player))
                        .orElse(false);
                if (success) {
                    return true;
                } else {
                    world.setBlock(up, Blocks.AIR.defaultBlockState(), 3);
                }
            }
            return false;
        }).orElse(false);
    }

    protected boolean attemptExtractOrInsertSeed(Player player, Contents contents) {
        // insertion / extraction of seeds is always executed from the main hand
        ItemStack held = player.getItemInHand(InteractionHand.MAIN_HAND);
        if(held.isEmpty()) {
            boolean last = player.isDiscrete();
            if(last) {
                ItemStack out = contents.extractLastSeed(1, true);
                if(!out.isEmpty()) {
                    player.setItemInHand(InteractionHand.MAIN_HAND, contents.extractLastSeed(1, false));
                    return true;
                }
            } else {
                ItemStack out =  contents.extractFirstSeed(1, true);
                if(!out.isEmpty()) {
                    player.setItemInHand(InteractionHand.MAIN_HAND, contents.extractFirstSeed(1, false));
                    return true;
                }
            }
        } else if(held.getItem() instanceof ItemDynamicAgriSeed) {
            ItemStack remaining = contents.insertSeed(held, true);
            if(remaining.isEmpty() || remaining.getCount() != held.getCount()) {
                player.setItemInHand(InteractionHand.MAIN_HAND, contents.insertSeed(held, false));
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isEnchantable(@Nonnull ItemStack stack) {
        return !this.isActivated(stack);
    }

    @Override
    public int getEnchantmentValue() {
        return 1;
    }

    @Override
    public int getItemEnchantability(ItemStack stack) {
        return this.isActivated(stack) ? 0 : 1;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment instanceof EnchantmentSeedBag;
    }

    @Nonnull
    @Override
    public Component getName(@Nonnull ItemStack stack) {
        return this.isActivated(stack)
                ? super.getName(stack)
                : NAME_DEACTIVATED;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level world, @Nonnull  List<Component> tooltip, @Nonnull TooltipFlag flag) {
        // Overriding this method to leave a note:
        // We are handling the tooltip from an event handler to remove the enchantment tooltip
        super.appendHoverText(stack, world, tooltip, flag);
    }

    public static class StatSorter implements Sorter {
        private final IAgriStat stat;
        private final Component description;

        protected StatSorter(IAgriStat stat) {
            this.stat = stat;
            this.description = new TranslatableComponent("agricraft.message.seed_bag_sorter." + stat.getId());
        }

        public IAgriStat getStat() {
            return this.stat;
        }

        @Override
        public Component getName() {
            return this.getStat().getDescription();
        }

        @Override
        public Component describe() {
            return this.description;
        }

        @Override
        public int compare(IAgriGenome o1, IAgriGenome o2) {
            int s1 = o1.getStats().getValue(this.getStat());
            int s2 = o2.getStats().getValue(this.getStat());
            if(s1 == s2) {
                return DEFAULT_SORTER.compare(o1, o2);
            }
            return s1 - s2;
        }
    }

    public static final Sorter DEFAULT_SORTER = new Sorter() {
        @Override
        public Component getName() {
            return AgriToolTips.SEED_BAG_SORTER_DEFAULT;
        }

        @Override
        public Component describe() {
            return AgriToolTips.MSG_SEED_BAG_DEFAULT_SORTER;
        }

        @Override
        public int compare(IAgriGenome o1, IAgriGenome o2) {
            int s1 = o1.getStats().getSum();
            int s2 = o2.getStats().getSum();
            if(s1 != s2) {
                return s1 - s2;
            }
            return AgriGeneRegistry.getInstance().stream()
                    .filter(gene -> !(gene instanceof GeneSpecies))
                    .mapToInt(gene -> {
                        int w = gene.getComparatorWeight();
                        int d1 = o1.getGenePair(gene).getDominant().comparatorValue();
                        int r1 = o1.getGenePair(gene).getRecessive().comparatorValue();
                        int d2 = o2.getGenePair(gene).getDominant().comparatorValue();
                        int r2 = o2.getGenePair(gene).getRecessive().comparatorValue();
                        return w*((d1 + r1) - (d2 + r2));
                    }).sum();
        }
    };

    private static final Contents EMPTY = new Contents() {
        @Override
        public IAgriPlant getPlant() {
            return NoPlant.getInstance();
        }

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public boolean isFull() {
            return false;
        }

        @Override
        public Sorter getSorter() {
            return DEFAULT_SORTER;
        }

        @Override
        public int getSorterIndex() {
            return 0;
        }

        @Override
        public void setSorterIndex(int index) {}

        @Override
        public ItemStack extractFirstSeed(int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack extractLastSeed(int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        public int getCapacity() {
            return 0;
        }

        @Override
        public int getSlots() {
            return 0;
        }

        @Nonnull
        @Override
        public ItemStack getStackInSlot(int slot) {
            return ItemStack.EMPTY;
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return stack;
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 0;
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return false;
        }
    };
}
