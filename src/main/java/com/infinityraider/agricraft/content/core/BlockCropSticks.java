package com.infinityraider.agricraft.content.core;

import com.agricraft.agricore.util.TypeHelper;
import com.google.common.collect.Lists;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.items.IAgriClipperItem;
import com.infinityraider.agricraft.api.v1.items.IAgriRakeItem;
import com.infinityraider.agricraft.api.v1.items.IAgriTrowelItem;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.infinitylib.block.BlockBaseTile;
import com.infinityraider.infinitylib.block.property.InfProperty;
import com.infinityraider.infinitylib.block.property.InfPropertyConfiguration;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IGrowable;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IPlantable;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockCropSticks extends BlockBaseTile<TileEntityCropSticks> implements IGrowable, IPlantable {
    // Excluded classes for Iem usage logic
    private static final Class<?>[] ITEM_EXCLUDES = new Class[]{
            IAgriRakeItem.class,
            IAgriClipperItem.class,
            IAgriTrowelItem.class,
            ItemDebugger.class
    };

    // Properties
    public static final InfProperty<Boolean> CROSS_CROP = InfProperty.Creators.create("cross_crop", false);
    public static final InfProperty<Boolean> PLANT = InfProperty.Creators.create("plant", false);
    public static final InfProperty<Integer> LIGHT = InfProperty.Creators.create("light", 0, 0, 16);

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

    private static final VoxelShape SHAPE_PLANT = Stream.of(
            SHAPE_DEFAULT,
            Block.makeCuboidShape(4, 0, 4, 12, 16, 12)
            ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    private static final VoxelShape SHAPE_CROSS_CROP = Stream.of(
            SHAPE_DEFAULT,
            Block.makeCuboidShape(0, 11, 2, 16, 12, 3),
            Block.makeCuboidShape(0, 11, 13, 16, 12, 14),
            Block.makeCuboidShape(2, 11, 0, 3, 12, 16),
            Block.makeCuboidShape(13, 11, 0, 14, 12, 16)
            ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    private static final VoxelShape SHAPE_CROSS_PLANT = Stream.of(
            SHAPE_CROSS_CROP,
            Block.makeCuboidShape(4, 0, 4, 12, 16, 12)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    private final CropStickVariant variant;

    public BlockCropSticks(CropStickVariant variant) {
        super(variant.getId(), Properties.create(variant.getMaterial())
                .tickRandomly()
                .notSolid()
                .setLightLevel(LIGHT::fetch)
        );
        this.variant = variant;
    }

    public Optional<IAgriCrop> getCrop(IBlockReader world, BlockPos pos) {
        return AgriApi.getCrop(world, pos);
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
    public VoxelShape getRenderShape(BlockState state, IBlockReader world, BlockPos pos) {
        return this.getShape(state, world, pos, ISelectionContext.dummy());
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos) {
        return this.getCollisionShape(state, world, pos, ISelectionContext.dummy());
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getRaytraceShape(BlockState state, IBlockReader world, BlockPos pos) {
        return this.getShape(state, world, pos, ISelectionContext.dummy());
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        if(CROSS_CROP.fetch(state)) {
            if(PLANT.fetch(state)) {
                return SHAPE_CROSS_PLANT;
            } else {
                return SHAPE_CROSS_CROP;
            }
        } else {
            if(PLANT.fetch(state)) {
                return SHAPE_PLANT;
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
        return this.getShape(state, world, pos, ISelectionContext.dummy());
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
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if(fromPos.up().equals(pos)) {
            if(!state.isValidPosition(world, pos)) {
                this.breakBlock(state, world, pos, true);
            }
        }
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos) {
        BlockState current = world.getBlockState(pos);
        return current.getMaterial().isReplaceable() && AgriApi.getSoilRegistry().contains(world.getBlockState(pos.down()));
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState state = this.getDefaultState();
        if(state.isValidPosition(context.getWorld(), context.getPos())) {
            return this.waterlog(state, context.getWorld(), context.getPos());
        }
        return null;
    }

    public void breakBlock(BlockState state, World world, BlockPos pos, boolean doDrops) {
        if(!world.isRemote()) {
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
            if(doDrops) {
                spawnDrops(state, world, pos);
            }
        }
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        this.getCrop(world, pos).ifPresent(IAgriCrop::applyGrowthTick);
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if(world.isRemote()) {
            return ActionResultType.PASS;
        }
        Optional<IAgriCrop> optional = this.getCrop(world, pos);
        if(!optional.isPresent()) {
            return ActionResultType.PASS;
        }
        IAgriCrop crop = optional.get();
        ItemStack heldItem = player.getHeldItem(hand);
        // Harvesting
        if(heldItem.isEmpty()) {
            return crop.harvest(stack -> this.spawnItem(crop, stack), player);
        }
        // Specific item interactions
        if(TypeHelper.isAnyType(heldItem.getItem(), ITEM_EXCLUDES)) {
            return ActionResultType.PASS;
        }
        // Fertilization
        if(AgriApi.getFertilizerRegistry().hasAdapter(heldItem)) {
            return AgriApi.getFertilizerRegistry().valueOf(heldItem)
                    .map(f -> f.applyFertilizer(crop, heldItem, world.getRandom()))
                    .orElse(ActionResultType.PASS);
        }
        // Creation of Cross crops
        if(heldItem.getItem() == this.asItem()) {
            if(crop.setCrossCrop(true)) {
                if(!player.isCreative()) {
                    player.getHeldItem(hand).shrink(1);
                }
                return ActionResultType.CONSUME;
            }
        }
        // Planting
        if(AgriApi.getSeedRegistry().hasAdapter(heldItem)) {
           return AgriApi.getSeedRegistry().valueOf(heldItem)
                    .map(seed -> {
                        if(crop.setSeed(seed)) {
                            if(!player.isCreative()) {
                                player.getHeldItem(hand).shrink(1);
                            }
                            return ActionResultType.CONSUME;
                        } else {
                            return ActionResultType.PASS;
                        }
                    })
                    .orElse(ActionResultType.PASS);
        }
        // Fall Back to harvesting
        return crop.harvest(stack -> this.spawnItem(crop, stack), player);
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public void onBlockClicked(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        this.getCrop(world, pos).ifPresent(crop -> crop.breakCrop(player));
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

    @Override
    @OnlyIn(Dist.CLIENT)
    public RenderType getRenderType() {
        return RenderType.getCutout();
    }

    public void spawnItem(IAgriCrop crop, ItemStack stack) {
        World world = crop.getWorld();
        if(world != null) {
            this.spawnItem(world, crop.getPosition(), stack);
        }
    }

    /**
     * -------------------------
     * Vanilla IGrowable methods
     * -------------------------
     */

    @Override
    public boolean canGrow(IBlockReader world, BlockPos pos, BlockState state, boolean isClient) {
        return this.isFertile(state, world, pos);
    }

    private static final ItemStack BONE_MEAL = new ItemStack(Items.BONE_MEAL);

    @Override
    public boolean canUseBonemeal(World world, Random rand, BlockPos pos, BlockState state) {
        return AgriApi.getFertilizerRegistry().valueOf(BONE_MEAL)
                .flatMap(fertilizer ->
                        this.getCrop(world, pos).map(crop ->
                                crop.acceptsFertilizer(fertilizer)))
                .orElse(false);
    }

    @Override
    public void grow(ServerWorld world, Random rand, BlockPos pos, BlockState state) {
        AgriApi.getFertilizerRegistry().valueOf(BONE_MEAL).ifPresent(fertilizer ->
               this.getCrop(world, pos).ifPresent(crop ->
                        fertilizer.applyFertilizer(crop, BONE_MEAL, rand)));
    }

    /**
     * ------------------------
     * Forge IPlantable methods
     * ------------------------
     */

    @Override
    public BlockState getPlant(IBlockReader world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if(world instanceof World) {
            return this.getCrop(world, pos)
                    .flatMap(crop -> crop.getPlant().asBlockState(crop.getGrowthStage()))
                    .orElse(state);
        }
        return state;
    }
}
