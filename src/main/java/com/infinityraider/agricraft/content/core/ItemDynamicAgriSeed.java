package com.infinityraider.agricraft.content.core;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.api.v1.content.items.IAgriSeedItem;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.content.AgriBlockRegistry;
import com.infinityraider.agricraft.content.AgriItemRegistry;
import com.infinityraider.agricraft.impl.v1.plant.NoPlant;
import com.infinityraider.agricraft.content.AgriTabs;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.item.ItemBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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
        ItemStack stack = new ItemStack(AgriItemRegistry.SEED, amount);
        // Create the tag.
        CompoundTag tag = new CompoundTag();
        genome.writeToNBT(tag);
        // Put the tag on stack
        stack.setTag(tag);
        // Return the stack
        return stack;
    }

    public ItemDynamicAgriSeed() {
        super(Names.Items.SEED, new Properties()
                .tab(AgriTabs.TAB_AGRICRAFT_SEED)
                .stacksTo(64)
        );
    }

    @Nonnull
    @Override
    public InteractionResult useOn(@Nonnull UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockEntity tile = world.getBlockEntity(pos);
        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();
        // If crop sticks were clicked, attempt to plant the seed
        if (tile instanceof TileEntityCropSticks) {
            return this.attemptSeedPlant((TileEntityCropSticks) tile, stack, player);
        }
        // If a soil was clicked, check the block on top of the soil and handle accordingly
        return AgriApi.getSoil(world, pos).map(soil -> {
            BlockPos up = pos.above();
            BlockEntity above = world.getBlockEntity(up);
            // There are currently crop sticks on the soil, attempt to plant on the crop sticks
            if (above instanceof TileEntityCropSticks) {
                return this.attemptSeedPlant((TileEntityCropSticks) above, stack, player);
            }
            // There are currently no crop sticks, check if the place is suitable and plant the plant directly
            if (above == null && AgriCraft.instance.getConfig().allowPlantingOutsideCropSticks()) {
                BlockState newState = AgriBlockRegistry.CROP_PLANT.getStateForPlacement(world, up);
                if (newState != null && world.setBlock(up, newState, 11)) {
                    boolean success = AgriApi.getCrop(world, up).map(crop ->
                            this.getGenome(context.getItemInHand()).map(genome -> crop.plantGenome(genome, player)).map(result -> {
                                if (result) {
                                    // consume item
                                    if (player == null || !player.isCreative()) {
                                        stack.shrink(1);
                                    }
                                }
                                return result;
                            }).orElse(false)).orElse(false);
                    if (success) {
                        return InteractionResult.SUCCESS;
                    } else {
                        world.setBlock(up, Blocks.AIR.defaultBlockState(), 3);
                    }
                }
            }
            // Neither alternative option was successful, delegate the call to the super method
            return super.useOn(context);
        }).orElse(super.useOn(context));
    }

    protected InteractionResult attemptSeedPlant(IAgriCrop crop, ItemStack stack, Player player) {
        return AgriApi.getGenomeAdapterizer().valueOf(stack)
                .map(seed -> {
                    if (crop.plantGenome(seed, player)) {
                        if (player != null && !player.isCreative()) {
                            stack.shrink(1);
                        }
                        return InteractionResult.CONSUME;
                    } else {
                        return InteractionResult.PASS;
                    }
                })
                .orElse(InteractionResult.PASS);
    }

    @Nonnull
    public Component getDisplayName(@Nonnull ItemStack stack) {
        return this.getPlant(stack).getSeedName();
    }

    @Override
    public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> items) {
        if(this.allowdedIn(group)) {
            AgriApi.getPlantRegistry()
                    .stream()
                    .map(IAgriPlant::toItemStack)
                    .forEach(items::add);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level world, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag advanced) {
        this.getStats(stack).ifPresent(stats -> stats.addTooltips(tooltip::add));
    }

    @Nonnull
    @Override
    public Optional<IAgriGenome> getGenome(ItemStack stack) {
        CompoundTag tag = stack.getTag();
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
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader world, BlockPos pos, Player player) {
        return true;
    }
}
