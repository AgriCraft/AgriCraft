package com.infinityraider.agricraft.content.core;

import com.agricraft.agricore.util.TypeHelper;
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
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
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
        return AgriApi.getAgriContent().getItems().getSeedItem().toItem();
    }

    @Override
    protected boolean onFluidChanged(Level world, BlockPos pos, BlockState state, Fluid oldFluid, Fluid newFluid) {
        return this.getCrop(world, pos).map(crop -> {
            if(crop.hasPlant()) {
                IAgriGrowthResponse response = crop.getPlant().getGrowthRequirement(crop.getGrowthStage()).getFluidResponse(newFluid, crop.getStats().getStrength());
                if(response.killInstantly()) {
                    response.onPlantKilled(crop);
                    world.setBlock(pos, newFluid.defaultFluidState().createLegacyBlock(), 3);
                    return true;
                }
            }
            return false;
        }).orElse(false);
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
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
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return this.getShape(state, world, pos, context);
    }

    @Override
    public InteractionResult onCropRightClicked(Level world, BlockPos pos, IAgriCrop crop, Player player, InteractionHand hand, BlockHitResult hit) {
        if(hand == InteractionHand.OFF_HAND) {
            return InteractionResult.PASS;
        }
        ItemStack heldItem = player.getItemInHand(hand);
        // Harvesting
        if (heldItem.isEmpty()) {
            return crop.harvest(stack -> this.spawnItem(crop, stack), player);
        }
        // Specific item interactions
        if (TypeHelper.isAnyType(heldItem.getItem(), ITEM_EXCLUDES)) {
            return InteractionResult.PASS;
        }
        // Fertilization
        if (AgriApi.getFertilizerAdapterizer().hasAdapter(heldItem)) {
            return AgriApi.getFertilizerAdapterizer().valueOf(heldItem).map(fertilizer -> {
                if(crop.acceptsFertilizer(fertilizer)) {
                    InteractionResult result = fertilizer.applyFertilizer(world, pos, crop, heldItem, world.getRandom(), player);
                    if(result == InteractionResult.CONSUME || result == InteractionResult.SUCCESS) {
                        crop.onApplyFertilizer(fertilizer, world.getRandom());
                    }
                    return result;
                } else {
                    return InteractionResult.CONSUME;
                }
            }).orElse(InteractionResult.PASS);
        }
        // Fall Back to harvesting
        return crop.harvest(stack -> this.spawnItem(crop, stack), player);
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder context) {
        List<ItemStack> drops = Lists.newArrayList();
        BlockEntity tile = context.getParameter(LootContextParams.BLOCK_ENTITY);
        if (tile instanceof IAgriCrop) {
            IAgriCrop crop = (IAgriCrop) tile;
            if (crop.hasPlant()) {
                // add plant fruits
                crop.getPlant().getHarvestProducts(drops::add, crop.getGrowthStage(), crop.getStats(), context.getLevel().getRandom());
                // drop the seed
                if(crop.getGrowthStage().canDropSeed()) {
                    crop.getGenome().map(IAgriGenome::toSeedStack).ifPresent(drops::add);
                }
            }
        }
        return drops;
    }
}
