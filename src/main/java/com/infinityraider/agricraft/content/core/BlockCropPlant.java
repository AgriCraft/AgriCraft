package com.infinityraider.agricraft.content.core;

import com.agricraft.agricore.plant.AgriParticleEffect;
import com.agricraft.agricore.util.TypeHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.api.v1.items.IAgriClipperItem;
import com.infinityraider.agricraft.api.v1.items.IAgriRakeItem;
import com.infinityraider.agricraft.api.v1.items.IAgriTrowelItem;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.block.property.InfPropertyConfiguration;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
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
            .waterloggable()
            .build();

    // TileEntity factory
    private static final BiFunction<BlockState, IBlockReader, TileEntityCropPlant> TILE_FACTORY = (s, w) -> new TileEntityCropPlant();

    private static final Map<Integer, VoxelShape> PLANT_SHAPES = Maps.newHashMap();

    public static VoxelShape getPlantShape(int height) {
        return PLANT_SHAPES.computeIfAbsent(height, h -> Optional.of(Block.makeCuboidShape(3, 0, 3, 13, h, 13)).get());
    }

    public BlockCropPlant() {
        super(Names.Blocks.CROP_PLANT, Properties.create(Material.PLANTS)
                .tickRandomly()
                .notSolid()
                .setLightLevel(LIGHT::fetch)
        );
    }

    @Override
    public BiFunction<BlockState, IBlockReader, TileEntityCropPlant> getTileEntityFactory() {
        return TILE_FACTORY;
    }

    @Override
    protected InfPropertyConfiguration getPropertyConfiguration() {
        return PROPERTIES;
    }

    @Override
    public Item asItem() {
        return AgriCraft.instance.getModItemRegistry().seed;
    }
    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
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
                    return ActionResultType.PASS;
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

    @Override
    public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
        Optional<IAgriCrop> optional = this.getCrop(world, pos);
        if (!optional.isPresent()) {
            return;
        }
        IAgriCrop crop = optional.get();
        if (crop.hasPlant() && crop.isMature()) {
            List<AgriParticleEffect> particleEffects = crop.getPlant().getParticleEffects();
            for (AgriParticleEffect particleEffect : particleEffects) {
                double deltaX = (rand.nextBoolean() ? 1 : -1) * particleEffect.getDeltaX() * rand.nextDouble();
                double deltaY = (rand.nextBoolean() ? 1 : -1) * particleEffect.getDeltaY() * rand.nextDouble();
                double deltaZ = (rand.nextBoolean() ? 1 : -1) * particleEffect.getDeltaZ() * rand.nextDouble();
                ParticleType<?> particle = ForgeRegistries.PARTICLE_TYPES.getValue(new ResourceLocation(particleEffect.getId()));
                if (particle != null) {
                    for (int amount = 0; amount < 3; ++amount) {
                        if (rand.nextDouble() < particleEffect.getProbability()) {
                            world.addParticle((IParticleData) particle,
                                    (double) pos.getX() + 0.5D + deltaX,
                                    (double) pos.getY() + 0.5D + deltaY,
                                    (double) pos.getZ() + 0.5D + deltaZ,
                                    0.0D, 0.0D, 0.0D);
                        }
                    }
                }
            }
        }
    }

}
