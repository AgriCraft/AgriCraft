package com.infinityraider.agricraft.content.core;

import com.agricraft.agricore.util.TypeHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.api.v1.content.items.IAgriClipperItem;
import com.infinityraider.agricraft.api.v1.content.items.IAgriRakeItem;
import com.infinityraider.agricraft.api.v1.content.items.IAgriTrowelItem;
import com.infinityraider.agricraft.api.v1.requirement.IAgriGrowthResponse;
import com.infinityraider.agricraft.content.AgriBlockRegistry;
import com.infinityraider.agricraft.content.tools.ItemSeedBag;
import com.infinityraider.infinitylib.block.property.InfProperty;
import com.infinityraider.infinitylib.block.property.InfPropertyConfiguration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
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
            .fluidloggable()
            .build();

    // TileEntity factory
    private static final BiFunction<BlockPos, BlockState, TileEntityCropSticks> TILE_FACTORY = TileEntityCropSticks::new;

    // VoxelShapes
    private static final VoxelShape SHAPE_DEFAULT = Stream.of(
            Block.box(2, -3, 2, 3, 14, 3),
            Block.box(13, -3, 2, 14, 14, 3),
            Block.box(2, -3, 13, 3, 14, 14),
            Block.box(13, -3, 13, 14, 14, 14)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape SHAPE_CROSS_CROP = Stream.of(
            SHAPE_DEFAULT,
            Block.box(0, 11, 2, 16, 12, 3),
            Block.box(0, 11, 13, 16, 12, 14),
            Block.box(2, 11, 0, 3, 12, 16),
            Block.box(13, 11, 0, 14, 12, 16)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final Map<Integer, VoxelShape> PLANT_SHAPES = Maps.newHashMap();

    private static final Map<Integer, VoxelShape> CROSS_PLANT_SHAPES = Maps.newHashMap();

    public static VoxelShape getPlantShape(int height) {
        return PLANT_SHAPES.computeIfAbsent(height, h -> Stream.of(
                SHAPE_DEFAULT,
                Block.box(3, 0, 3, 13, h, 13)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get());
    }

    public static VoxelShape getCrossPlantShape(int height) {
        return CROSS_PLANT_SHAPES.computeIfAbsent(height, h -> Stream.of(
                SHAPE_CROSS_CROP,
                Block.box(3, 0, 3, 13, h, 13)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get());
    }

    private final CropStickVariant variant;

    public BlockCropSticks(CropStickVariant variant) {
        super(variant.getId(), Properties.of(variant.getMaterial())
                .randomTicks()
                .noOcclusion()
                .lightLevel(LIGHT::fetch)
        );
        this.variant = variant;
    }

    public CropStickVariant getVariant() {
        return this.variant;
    }

    public boolean isVariant(ItemCropSticks sticks) {
        return this.getVariant() == sticks.getVariant();
    }

    @Override
    public BiFunction<BlockPos, BlockState, TileEntityCropSticks> getTileEntityFactory() {
        return TILE_FACTORY;
    }

    @Override
    protected InfPropertyConfiguration getPropertyConfiguration() {
        return PROPERTIES;
    }

    @Override
    public ItemCropSticks asItem() {
        return this.getVariant().getItem();
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
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
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if(AgriCraft.instance.getConfig().cropSticksCollide()) {
            return CROSS_CROP.fetch(state) ? SHAPE_CROSS_CROP : SHAPE_DEFAULT;
        } else {
            return Shapes.empty();
        }
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return this.getShape(state, world, pos, context);
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, world, pos, block, fromPos, isMoving);
        BlockEntity tile = world.getBlockEntity(pos);
        if(tile instanceof TileEntityCropSticks) {
            Arrays.stream(Direction.values())
                    .filter(dir -> dir.getAxis().isHorizontal())
                    .filter(dir -> pos.relative(dir).equals(fromPos))
                    .forEach(dir -> ((TileEntityCropSticks) tile).onNeighbourChange(dir, fromPos, world.getBlockState(fromPos)));
        }
    }

    @Override
    protected boolean onFluidChanged(Level world, BlockPos pos, BlockState state, Fluid oldFluid, Fluid newFluid) {
        Optional<IAgriCrop> optCrop = this.getCrop(world, pos);
        boolean noMorePlant = optCrop.map(crop -> {
            if (!crop.hasPlant()) {
                return true;
            }
            IAgriGrowthResponse response = crop.getPlant().getGrowthRequirement(crop.getGrowthStage()).getFluidResponse(newFluid, crop.getStats().getStrength());
            if (response.killInstantly()) {
                response.onPlantKilled(crop);
                crop.removeGenome();
                return true;
            }
            return false;
        }).orElse(true);
        if(this.getVariant().canExistInFluid(newFluid)) {
            // the crop sticks remain, regardless of what happened to the plant
            return false;
        } else {
            if(noMorePlant) {
                // no more crop sticks, no more plant, only fluid
                world.setBlock(pos, newFluid.defaultFluidState().createLegacyBlock(), 3);
                if(world instanceof ServerLevel) {
                    double x = pos.getX() + 0.5;
                    double y = pos.getY() + 0.5;
                    double z = pos.getZ() + 0.5;
                    for(int i = 0; i < 2; i++) {
                        world.addParticle(ParticleTypes.SMOKE,
                                x + 0.25*world.getRandom().nextDouble(), y, z + 0.25*world.getRandom().nextDouble(),
                                0, 1, 0);
                    }
                    world.playSound(null, x, y, z, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS,
                            0.2F + world.getRandom().nextFloat() * 0.2F, 0.9F + world.getRandom().nextFloat() * 0.05F);
                }
            } else {
                // no more crop sticks, but still plant, and fluid
                BlockState newState = AgriBlockRegistry.CROP_PLANT.defaultBlockState();
                newState = BlockCropBase.PLANT.mimic(state, newState);
                newState = BlockCropBase.LIGHT.mimic(state, newState);
                newState = InfProperty.Defaults.fluidlogged().mimic(state, newState);
                world.setBlock(pos, newState, 3);
                // If there was trouble, reset and abort.
                BlockEntity tile = world.getBlockEntity(pos);
                if(!(tile instanceof TileEntityCropPlant)) {
                    world.setBlock(pos, state, 3);
                    return false;
                }
                // Mimic plant and weed
                ((TileEntityCropPlant) tile).mimicFrom(optCrop.get());
            }
            return true;
        }
    }

    @Override
    public InteractionResult onCropRightClicked(Level world, BlockPos pos, IAgriCrop crop, Player player, InteractionHand hand, BlockHitResult hit) {
        if(hand == InteractionHand.OFF_HAND) {
            return InteractionResult.PASS;
        }
        ItemStack heldItem = player.getItemInHand(hand);
        // Harvesting (or de-cross-crop'ing)
        if (heldItem.isEmpty()) {
            if(crop.isCrossCrop()) {
                if (crop.setCrossCrop(false) && !player.isCreative()) {
                    this.spawnItem(crop, new ItemStack(this.asItem()));
                    return InteractionResult.CONSUME;
                }
            } else {
                return crop.harvest(stack -> this.spawnItem(crop, stack), player);
            }
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
        // Creation of Cross crops
        if (heldItem.getItem() == this.asItem()) {
            if (crop.setCrossCrop(true)) {
                if (!player.isCreative()) {
                    player.getItemInHand(hand).shrink(1);
                }
                this.asItem().playCropStickSound(world, pos);
                return InteractionResult.SUCCESS;
            }
        }
        // Planting from seed (copying the stats)
        if (AgriApi.getGenomeAdapterizer().hasAdapter(heldItem)) {
            return AgriApi.getGenomeAdapterizer().valueOf(heldItem)
                    .map(seed -> {
                        if (crop.plantGenome(seed, player)) {
                            if (!player.isCreative()) {
                                player.getItemInHand(hand).shrink(1);
                            }
                            player.swing(hand);
                            return InteractionResult.CONSUME;
                        } else {
                            return InteractionResult.PASS;
                        }})
                    .orElse(InteractionResult.PASS);
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
