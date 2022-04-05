package com.infinityraider.agricraft.content.core;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.api.v1.content.items.IAgriSeedItem;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.impl.v1.plant.NoPlant;
import com.infinityraider.agricraft.content.AgriTabs;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.item.ItemBase;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class ItemDynamicAgriSeed extends ItemBase implements IAgriSeedItem {
    private static final IAgriPlant NO_PLANT = NoPlant.getInstance();

    public static ItemStack toStack(IAgriPlant plant, int amount) {
        return toStack(AgriApi.getAgriGenomeBuilder(plant).build(), amount);
    }

    public static ItemStack toStack(IAgriGenome genome, int amount) {
        // Create the stack.
        ItemStack stack = new ItemStack(AgriCraft.instance.getModItemRegistry().seed, amount);
        // Create the tag.
        CompoundNBT tag = new CompoundNBT();
        genome.writeToNBT(tag);
        // Put the tag on stack
        stack.setTag(tag);
        // Return the stack
        return stack;
    }

    public ItemDynamicAgriSeed() {
        super(Names.Items.SEED, new Properties()
                .group(AgriTabs.TAB_AGRICRAFT_SEED)
                .maxStackSize(64)
        );
    }

    @Nonnull
    @Override
    public ActionResultType onItemUse(@Nonnull ItemUseContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        TileEntity tile = world.getTileEntity(pos);
        ItemStack stack = context.getItem();
        PlayerEntity player = context.getPlayer();
        // If crop sticks were clicked, attempt to plant the seed
        if (tile instanceof TileEntityCropSticks) {
            return this.attemptSeedPlant((TileEntityCropSticks) tile, stack, player);
        }
        // If a soil was clicked, check the block on top of the soil and handle accordingly
        return AgriApi.getSoil(world, pos).map(soil -> {
            BlockPos up = pos.up();
            TileEntity above = world.getTileEntity(up);
            // There are currently crop sticks on the soil, attempt to plant on the crop sticks
            if (above instanceof TileEntityCropSticks) {
                return this.attemptSeedPlant((TileEntityCropSticks) above, stack, player);
            }
            // There are currently no crop sticks, check if the place is suitable and plant the plant directly
            if (above == null && AgriCraft.instance.getConfig().allowPlantingOutsideCropSticks()) {
                BlockState newState = AgriCraft.instance.getModBlockRegistry().crop_plant.getStateForPlacement(world, up);
                if (newState != null && world.setBlockState(up, newState, 11)) {
                    boolean success = AgriApi.getCrop(world, up).map(crop ->
                            this.getGenome(context.getItem()).map(genome -> crop.plantGenome(genome, player)).map(result -> {
                                if (result) {
                                    // consume item
                                    if (player == null || !player.isCreative()) {
                                        stack.shrink(1);
                                    }
                                }
                                return result;
                            }).orElse(false)).orElse(false);
                    if (success) {
                        return ActionResultType.SUCCESS;
                    } else {
                        world.setBlockState(up, Blocks.AIR.getDefaultState());
                    }
                }
            }
            // Neither alternative option was successful, delegate the call to the super method
            return super.onItemUse(context);
        }).orElse(super.onItemUse(context));
    }

    protected ActionResultType attemptSeedPlant(IAgriCrop crop, ItemStack stack, PlayerEntity player) {
        return AgriApi.getGenomeAdapterizer().valueOf(stack)
                .map(seed -> {
                    if (crop.plantGenome(seed, player)) {
                        if (player != null && !player.isCreative()) {
                            stack.shrink(1);
                        }
                        return ActionResultType.CONSUME;
                    } else {
                        return ActionResultType.PASS;
                    }
                })
                .orElse(ActionResultType.PASS);
    }

    @Nonnull
    public ITextComponent getDisplayName(@Nonnull ItemStack stack) {
        return this.getPlant(stack).getSeedName();
    }

    @Override
    public void fillItemGroup(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
        if(this.isInGroup(group)) {
            AgriApi.getPlantRegistry()
                    .stream()
                    .map(IAgriPlant::toItemStack)
                    .forEach(items::add);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag advanced) {
        this.getStats(stack).ifPresent(stats -> stats.addTooltips(tooltip::add));
    }

    @Nonnull
    @Override
    public Optional<IAgriGenome> getGenome(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if(tag == null) {
            return Optional.empty();
        }
        IAgriGenome genome = AgriApi.getAgriGenomeBuilder(NO_PLANT).build();
        if(!genome.readFromNBT(tag)) {
            // Faulty NBT
            stack.setTag(null);
            return Optional.empty();
        }
        return Optional.of(genome);
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player) {
        return true;
    }
}
