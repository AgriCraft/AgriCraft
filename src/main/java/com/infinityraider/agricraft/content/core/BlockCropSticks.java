package com.infinityraider.agricraft.content.core;

import com.agricraft.agricore.util.TypeHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.items.IAgriClipperItem;
import com.infinityraider.agricraft.api.v1.items.IAgriRakeItem;
import com.infinityraider.agricraft.api.v1.items.IAgriTrowelItem;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.content.tools.ItemSeedBag;
import com.infinityraider.infinitylib.block.property.InfProperty;
import com.infinityraider.infinitylib.block.property.InfPropertyConfiguration;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockCropSticks extends BlockCropBase<TileEntityCropSticks> {
    // Excluded classes for Item usage logic
    private static final Class<?>[] ITEM_EXCLUDES = new Class[]{
            IAgriRakeItem.class,
            IAgriClipperItem.class,
            IAgriTrowelItem.class,
            ItemSeedBag.class,
            ItemDebugger.class
    };

    // Properties
    public static final InfProperty<Boolean> CROSS_CROP = InfProperty.Creators.create("cross_crop", false);

    private static final InfPropertyConfiguration PROPERTIES = InfPropertyConfiguration.builder()
            .add(CROSS_CROP)
            .add(PLANT)
            .add(LIGHT)
            .waterloggable()
            .build();

    // TileEntity factory
    private static final BiFunction<BlockState, IBlockReader, TileEntityCropSticks> TILE_FACTORY = (s, w) -> new TileEntityCropSticks();

    // VoxelShapes
    private static final VoxelShape SHAPE_DEFAULT = Stream.of(
            Block.makeCuboidShape(2, -3, 2, 3, 14, 3),
            Block.makeCuboidShape(13, -3, 2, 14, 14, 3),
            Block.makeCuboidShape(2, -3, 13, 3, 14, 14),
            Block.makeCuboidShape(13, -3, 13, 14, 14, 14)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    private static final VoxelShape SHAPE_CROSS_CROP = Stream.of(
            SHAPE_DEFAULT,
            Block.makeCuboidShape(0, 11, 2, 16, 12, 3),
            Block.makeCuboidShape(0, 11, 13, 16, 12, 14),
            Block.makeCuboidShape(2, 11, 0, 3, 12, 16),
            Block.makeCuboidShape(13, 11, 0, 14, 12, 16)
            ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    private static final Map<Integer, VoxelShape> PLANT_SHAPES = Maps.newHashMap();

    private static final Map<Integer, VoxelShape> CROSS_PLANT_SHAPES = Maps.newHashMap();

    public static VoxelShape getPlantShape(int height) {
        return PLANT_SHAPES.computeIfAbsent(height, h -> Stream.of(
                SHAPE_DEFAULT,
                Block.makeCuboidShape(3, 0, 3, 13, h, 13)
        ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get());
    }

    public static VoxelShape getCrossPlantShape(int height) {
        return CROSS_PLANT_SHAPES.computeIfAbsent(height, h -> Stream.of(
                SHAPE_CROSS_CROP,
                Block.makeCuboidShape(3, 0, 3, 13, h, 13)
        ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get());
    }

    private final CropStickVariant variant;

    public BlockCropSticks(CropStickVariant variant) {
        super(variant.getId(), Properties.create(variant.getMaterial())
                .tickRandomly()
                .notSolid()
                .setLightLevel(LIGHT::fetch)
        );
        this.variant = variant;
    }

    @Override
    public BiFunction<BlockState, IBlockReader, TileEntityCropSticks> getTileEntityFactory() {
        return TILE_FACTORY;
    }

    @Override
    protected InfPropertyConfiguration getPropertyConfiguration() {
        return PROPERTIES;
    }

    @Override
    public Item asItem() {
        return this.variant.getItem();
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        if(CROSS_CROP.fetch(state)) {
            if(PLANT.fetch(state)) {
                return getCrossPlantShape(this.getCrop(world, pos)
                        .map(crop -> Math.max(
                                crop.getPlant().getPlantHeight(crop.getGrowthStage()),
                                crop.getWeeds().getPlantHeight(crop.getWeedGrowthStage())))
                        .map(Double::intValue)
                        .orElse(0));
            } else {
                return SHAPE_CROSS_CROP;
            }
        } else {
            if(PLANT.fetch(state)) {
                return getPlantShape(this.getCrop(world, pos)
                        .map(crop -> Math.max(
                                crop.getPlant().getPlantHeight(crop.getGrowthStage()),
                                crop.getWeeds().getPlantHeight(crop.getWeedGrowthStage())))
                        .map(Double::intValue)
                        .orElse(0));
            } else {
                return SHAPE_DEFAULT;
            }
        }
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return CROSS_CROP.fetch(state) ? SHAPE_CROSS_CROP : SHAPE_DEFAULT;
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getRayTraceShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return this.getShape(state, world, pos, context);
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos) {
        this.getCrop(world, pos).ifPresent(crop -> {
            if(crop instanceof TileEntityCropSticks) {
                ((TileEntityCropSticks) crop).onNeighbourChange(facing, facingPos, facingState);
            }
        });
        return state;
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (world.isRemote()) {
            return ActionResultType.PASS;
        }
        if(hand == Hand.OFF_HAND) {
            return ActionResultType.PASS;
        }
        Optional<IAgriCrop> optional = this.getCrop(world, pos);
        if (!optional.isPresent()) {
            return ActionResultType.FAIL;
        }
        IAgriCrop crop = optional.get();
        ItemStack heldItem = player.getHeldItem(hand);
        // Harvesting (or de-cross-crop'ing)
        if (heldItem.isEmpty()) {
            if(crop.isCrossCrop()) {
                if (crop.setCrossCrop(false) && !player.isCreative()) {
                    this.spawnItem(crop, new ItemStack(this.asItem()));
                    return ActionResultType.CONSUME;
                }
            } else {
                return crop.harvest(stack -> this.spawnItem(crop, stack), player);
            }
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
                    return ActionResultType.PASS;
                }
            }).orElse(ActionResultType.PASS);
        }
        // Creation of Cross crops
        if (heldItem.getItem() == this.asItem()) {
            if (crop.setCrossCrop(true)) {
                if (!player.isCreative()) {
                    player.getHeldItem(hand).shrink(1);
                }
                return ActionResultType.CONSUME;
            }
        }
        // Planting from seed (copying the stats)
        if (AgriApi.getSeedAdapterizer().hasAdapter(heldItem)) {
            return AgriApi.getSeedAdapterizer().valueOf(heldItem)
                    .map(seed -> {
                        if (crop.plantSeed(seed)) {
                            if (!player.isCreative()) {
                                player.getHeldItem(hand).shrink(1);
                            }
                            return ActionResultType.CONSUME;
                        } else {
                            return ActionResultType.PASS;
                        }})
                    .orElse(ActionResultType.PASS);
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
                    crop.getSeed().map(AgriSeed::toStack).ifPresent(drops::add);
                }
            }
            if (!(crop.hasWeeds() && AgriCraft.instance.getConfig().weedsDestroyCropSticks())) {
                // add crop sticks
                drops.add(new ItemStack(this.asItem(), crop.isCrossCrop() ? 2 : 1));
            }
        } else {
            // The TileEntity no longer exists, use the data that we have to try at least dropping the crop sticks...
            AgriCraft.instance.getLogger().error("Could not find IAgriCrop instance associated with crop sticks: " + context);
            drops.add(new ItemStack(this.asItem(), 1));
            if(CROSS_CROP.fetch(state)) {
                drops.add(new ItemStack(this.asItem(), 1));
            }
        }
        return drops;
    }
}
