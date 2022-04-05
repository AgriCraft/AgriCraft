package com.infinityraider.agricraft.content.core;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.api.v1.content.items.IAgriClipperItem;
import com.infinityraider.agricraft.api.v1.content.items.IAgriRakeItem;
import com.infinityraider.agricraft.api.v1.content.items.IAgriTrowelItem;
import com.infinityraider.agricraft.api.v1.requirement.IAgriGrowthResponse;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.block.property.InfPropertyConfiguration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;#
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockCropPlant extends BlockCropBase<TileEntityCropPlant> {
    // Excluded classes for Item usage logic
    private static final Class<?>[] ITEM_EXCLUDES = new Class[]{
            IAgriRakeItem.class,
            IAgriClipperItem.class,
            IAgriTrowelItem.class,
            ItemDebugger.class
    };

    // Properties
    private static final InfPropertyConfiguration PROPERTIES = InfPropertyConfiguration.builder()
            .add(PLANT)
            .add(LIGHT)
            .fluidloggable()
            .build();

    // TileEntity factory
    private static final BiFunction<BlockPos, BlockState, TileEntityCropPlant> TILE_FACTORY = TileEntityCropPlant::new;

    private static final Map<Integer, VoxelShape> PLANT_SHAPES = Maps.newHashMap();

    public static VoxelShape getPlantShape(int height) {
        return PLANT_SHAPES.computeIfAbsent(height, h -> Optional.of(Block.box(3, 0, 3, 13, h, 13)).get());
    }

    public BlockCropPlant() {
        super(Names.Blocks.CROP_PLANT, Properties.of(Material.PLANT)
                .randomTicks()
                .noCollission()
                .lightLevel(LIGHT::fetch)
                .sound(SoundType.CROP)
        );
    }

    @Override
    public BiFunction<BlockPos, BlockState, TileEntityCropPlant> getTileEntityFactory() {
        return TILE_FACTORY;
    }

    @Override
    protected InfPropertyConfiguration getPropertyConfiguration() {
        return PROPERTIES;
    }

    @Override
    public Item asItem() {
        return AgriApi.getAgriContent().getItems().getSeedItem();
    }

    @Override
    protected boolean onFluidChanged(Level world, BlockPos pos, BlockState state, Fluid oldFluid, Fluid newFluid) {
        return this.getCrop(world, pos).map(crop -> {
            if(crop.hasPlant()) {
                IAgriGrowthResponse response = crop.getPlant().getGrowthRequirement(crop.getGrowthStage()).getFluidResponse(newFluid, crop.getStats().getStrength());
                if(response.killInstantly()) {
                    response.onPlantKilled(crop);
                    world.setBlockState(pos, newFluid.getDefaultState().getBlockState());
                    return true;
                }
            }
            return false;
        }).orElse(false);
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, ISelectionContext context) {
        return getPlantShape(this.getCrop(world, pos)
                .map(crop -> Math.max(
                        crop.getPlant().getPlantHeight(crop.getGrowthStage()),
                        crop.getWeeds().getPlantHeight(crop.getWeedGrowthStage())))
                .map(Double::intValue)
                .orElse(0));
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return VoxelShapes.empty();
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getRayTraceShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return this.getShape(state, world, pos, context);
    }

    @Override
    public ActionResultType onCropRightClicked(World world, BlockPos pos, IAgriCrop crop, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if(hand == Hand.OFF_HAND) {
            return ActionResultType.PASS;
        }
        ItemStack heldItem = player.getHeldItem(hand);
        // Harvesting
        if (heldItem.isEmpty()) {
            return crop.harvest(stack -> this.spawnItem(crop, stack), player);
        }
        // Specific item interactions
        if (TypeHelper.isAnyType(heldItem.getItem(), ITEM_EXCLUDES)) {
            return ActionResultType.PASS;
        }
        // Fertilization
        if (AgriApi.getFertilizerAdapterizer().hasAdapter(heldItem)) {
            return AgriApi.getFertilizerAdapterizer().valueOf(heldItem).map(fertilizer -> {
                if(crop.acceptsFertilizer(fertilizer)) {
                    ActionResultType result = fertilizer.applyFertilizer(world, pos, crop, heldItem, world.getRandom(), player);
                    if(result.isSuccessOrConsume()) {
                        crop.onApplyFertilizer(fertilizer, world.getRandom());
                    }
                    return result;
                } else {
                    return ActionResultType.CONSUME;
                }
            }).orElse(ActionResultType.PASS);
        }
        // Fall Back to harvesting
        return crop.harvest(stack -> this.spawnItem(crop, stack), player);
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder context) {
        List<ItemStack> drops = Lists.newArrayList();
        TileEntity tile = context.get(LootParameters.BLOCK_ENTITY);
        if (tile instanceof IAgriCrop) {
            IAgriCrop crop = (IAgriCrop) tile;
            if (crop.hasPlant()) {
                // add plant fruits
                crop.getPlant().getHarvestProducts(drops::add, crop.getGrowthStage(), crop.getStats(), context.getWorld().getRandom());
                // drop the seed
                if(crop.getGrowthStage().canDropSeed()) {
                    crop.getGenome().map(IAgriGenome::toSeedStack).ifPresent(drops::add);
                }
            }
        }
        return drops;
    }
}
