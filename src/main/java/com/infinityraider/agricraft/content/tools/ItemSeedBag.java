package com.infinityraider.agricraft.content.tools;

import com.google.common.collect.Lists;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import com.infinityraider.agricraft.capability.CapabilitySeedBagContents;
import com.infinityraider.agricraft.content.AgriTabs;
import com.infinityraider.agricraft.content.core.ItemDynamicAgriSeed;
import com.infinityraider.agricraft.content.core.TileEntityCropSticks;
import com.infinityraider.agricraft.impl.v1.genetics.AgriGeneRegistry;
import com.infinityraider.agricraft.impl.v1.genetics.GeneSpecies;
import com.infinityraider.agricraft.impl.v1.plant.NoPlant;
import com.infinityraider.agricraft.reference.AgriToolTips;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.item.ItemBase;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;

public class ItemSeedBag extends ItemBase {
    private static final List<ISorter> sorters = Lists.newArrayList();

    public static ISorter getSorter(int index) {
        return sorters.get(index % sorters.size());
    }

    public static int addSorter(ISorter sorter) {
        if(sorters.contains(sorter)) {
            return sorters.indexOf(sorter);
        } else {
            sorters.add(sorter);
            return sorters.size() - 1;
        }
    }

    public static int addSorter(IAgriStat stat) {
        return addSorter(new StatSorter(stat));
    }

    public ItemSeedBag() {
        super(Names.Items.SEED_BAG, new Properties()
                .group(AgriTabs.TAB_AGRICRAFT)
                .maxStackSize(1)
        );
    }

    public IContents getContents(ItemStack stack) {
        return CapabilitySeedBagContents.getInstance().getCapability(stack).map(impl -> (IContents) impl).orElse(EMPTY);
    }

    @Nonnull
    @Override
    public ActionResultType onItemUse(@Nonnull ItemUseContext context) {
        IContents contents = this.getContents(context.getItem());
        if(contents.isActivated()) {
            Hand hand = context.getHand();
            PlayerEntity player = context.getPlayer();
            if (hand == Hand.OFF_HAND) {
                // From off hand: interact with main to insert / extract seeds
                if (player != null && this.attemptExtractOrInsertSeed(player, contents)) {
                    return ActionResultType.SUCCESS;
                }
            } else {
                // From main hand: interact with the world to plant the seed
                if (this.attemptPlantSeed(context.getWorld(), context.getPos(), contents, player)) {
                    return ActionResultType.SUCCESS;
                }
            }
        }
        return ActionResultType.PASS;
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull PlayerEntity player, @Nonnull Hand hand) {
        // From off hand: interact with main to insert / extract seeds
        if(hand == Hand.OFF_HAND) {
            ItemStack stack = player.getHeldItem(hand);
            IContents contents = this.getContents(stack);
            if(contents.isActivated() && this.attemptExtractOrInsertSeed(player, contents)) {
                return ActionResult.resultSuccess(stack);
            }
        }
        // From main hand: world-interaction behaviour which is not handled in this method
        return ActionResult.resultPass(player.getHeldItem(hand));
    }

    protected boolean attemptPlantSeed(World world, BlockPos pos, IContents contents, @Nullable  PlayerEntity player) {
        if (contents.getCount() > 0) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileEntityCropSticks) {
                // Attempt to plant on crop sticks
                if(this.attemptPlantOnCrops((TileEntityCropSticks) tile, contents.extractFirstSeed(1, true), player)) {
                    contents.extractFirstSeed(1, false);
                    return true;
                }
            } else {
                BlockPos up = pos.up();
                tile = world.getTileEntity(up);
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

    protected boolean attemptPlantOnCrops(IAgriCrop crop, ItemStack seedStack, @Nullable  PlayerEntity player) {
        return AgriApi.getSeedAdapterizer().valueOf(seedStack).map(seed -> crop.plantSeed(seed, player)).orElse(false);
    }

    protected boolean attemptPlantOnSoil(World world, BlockPos pos, ItemStack seedStack, @Nullable  PlayerEntity player) {
        BlockState state = world.getBlockState(pos);
        BlockPos up = pos.up();
        return AgriApi.getSoilRegistry().valueOf(state).map(soil -> {
            BlockState newState = AgriCraft.instance.getModBlockRegistry().crop_plant.getStateForPlacement(world, up);
            if (newState != null && world.setBlockState(up, newState, 11)) {
                boolean success = AgriApi.getCrop(world, up).map(crop ->
                        this.attemptPlantOnCrops(crop, seedStack, player))
                        .orElse(false);
                if (success) {
                    return true;
                } else {
                    world.setBlockState(up, Blocks.AIR.getDefaultState());
                }
            }
            return false;
        }).orElse(false);
    }

    protected boolean attemptExtractOrInsertSeed(PlayerEntity player, IContents contents) {
        ItemStack held = player.getHeldItem(Hand.MAIN_HAND);
        if(held.isEmpty()) {
            boolean last = player.isSneaking();
            if(last) {
                ItemStack out = contents.extractLastSeed(1, true);
                if(!out.isEmpty()) {
                    player.setHeldItem(Hand.MAIN_HAND, contents.extractLastSeed(1, false));
                    return true;
                }
            } else {
                ItemStack out =  contents.extractFirstSeed(1, true);
                if(!out.isEmpty()) {
                    player.setHeldItem(Hand.MAIN_HAND, contents.extractFirstSeed(1, false));
                    return true;
                }
            }
        } else if(held.getItem() instanceof ItemDynamicAgriSeed) {
            ItemStack remaining = contents.insertSeed(held, true);
            if(remaining.isEmpty() || remaining.getCount() != held.getCount()) {
                player.setHeldItem(Hand.MAIN_HAND, contents.insertSeed(held, false));
                return true;
            }
        }
        return false;
    }

    public interface IContents extends IItemHandler {
        boolean isActivated();

        void activate();

        IAgriPlant getPlant();

        int getCount();

        ISorter getSorter();

        int getSorterIndex();

        void setSorterIndex(int index);

        default ItemStack insertSeed(ItemStack stack, boolean simulate) {
            return this.insertItem(0, stack, simulate);
        }

        ItemStack extractFirstSeed(int amount, boolean simulate);

        ItemStack extractLastSeed(int amount, boolean simulate);
    }

    public interface ISorter extends Comparator<IAgriGenome> {
        ITextComponent getName();
    }

    public static class StatSorter implements ISorter {
        private final IAgriStat stat;

        protected StatSorter(IAgriStat stat) {
            this.stat = stat;
        }

        public IAgriStat getStat() {
            return this.stat;
        }

        @Override
        public ITextComponent getName() {
            return this.getStat().getDescription();
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

    public static final ISorter DEFAULT_SORTER = new ISorter() {
        @Override
        public ITextComponent getName() {
            return AgriToolTips.TOTAL;
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

    private static final IContents EMPTY = new IContents() {
        @Override
        public boolean isActivated() {
            return false;
        }

        @Override
        public void activate() {}

        @Override
        public IAgriPlant getPlant() {
            return NoPlant.getInstance();
        }

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public ISorter getSorter() {
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

    static {
        addSorter(DEFAULT_SORTER);
    }
}
