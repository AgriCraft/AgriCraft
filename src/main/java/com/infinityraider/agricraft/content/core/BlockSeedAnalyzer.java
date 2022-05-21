package com.infinityraider.agricraft.content.core;

import com.google.common.collect.Lists;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.content.items.IAgriJournalItem;
import com.infinityraider.agricraft.api.v1.content.items.IAgriSeedItem;
import com.infinityraider.agricraft.reference.AgriToolTips;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.block.BlockBaseTile;
import com.infinityraider.infinitylib.block.property.InfProperty;
import com.infinityraider.infinitylib.block.property.InfPropertyConfiguration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.BiFunction;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockSeedAnalyzer extends BlockBaseTile<TileEntitySeedAnalyzer> implements SimpleWaterloggedBlock {
    // Properties
    public static final InfProperty<Direction> ORIENTATION = InfProperty.Creators.createHorizontals("orientation", Direction.NORTH);
    public static final InfProperty<Boolean> JOURNAL = InfProperty.Creators.create("journal", false);

    private static final InfPropertyConfiguration PROPERTIES = InfPropertyConfiguration.builder()
            .add(ORIENTATION)
            .add(JOURNAL)
            .waterloggable()
            .build();

    // TileEntity factory
    private static final BiFunction<BlockPos, BlockState, TileEntitySeedAnalyzer> TILE_FACTORY = TileEntitySeedAnalyzer::new;

    // VoxelShapes
    private static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 4, 15);

    public BlockSeedAnalyzer() {
        super(Names.Blocks.SEED_ANALYZER, Properties.of(Material.WOOD)
                .noOcclusion()
        );
    }

    @Override
    protected InfPropertyConfiguration getPropertyConfiguration() {
        return PROPERTIES;
    }

    @Override
    public BiFunction<BlockPos, BlockState, TileEntitySeedAnalyzer> getTileEntityFactory() {
        return TILE_FACTORY;
    }

    @Override
    public Item asItem() {
        return AgriApi.getAgriContent().getItems().getSeedAnalyzerItem();
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter world, BlockPos pos) {
        return this.getShape(state, world, pos, CollisionContext.empty());
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter world, BlockPos pos) {
        return this.getCollisionShape(state, world, pos, CollisionContext.empty());
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getInteractionShape(BlockState state, BlockGetter world, BlockPos pos) {
        return this.getVisualShape(state, world, pos, CollisionContext.empty());
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return this.getShape(state, world, pos, context);
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return this.getShape(state, world, pos, CollisionContext.empty());
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockState current = world.getBlockState(pos);
        return current.equals(state) || current.getMaterial().isReplaceable();
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = this.defaultBlockState();
        if(state.canSurvive(context.getLevel(), context.getClickedPos())) {
            return ORIENTATION.apply(
                    this.fluidlog(state, context.getLevel(), context.getClickedPos()),
                    context.getHorizontalDirection().getOpposite());
        }
        return null;
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if(hand == InteractionHand.OFF_HAND) {
            return InteractionResult.FAIL;
        }
        BlockEntity tile = world.getBlockEntity(pos);
        if(!(tile instanceof TileEntitySeedAnalyzer)) {
            return InteractionResult.FAIL;
        }
        TileEntitySeedAnalyzer analyzer = (TileEntitySeedAnalyzer) tile;
        ItemStack heldItem = player.getItemInHand(hand);
        // debugging
        if(heldItem.getItem() instanceof ItemDebugger) {
            return InteractionResult.PASS;
        }
        // Player is sneaking: insertion / extraction logic
        if(player.isDiscrete()) {
            // No sneak-action on the client
            if (world.isClientSide()) {
                return InteractionResult.PASS;
            }
            // Try extracting the seed
            if(analyzer.hasSeed()) {
                this.extractSeed(analyzer, player);
                return InteractionResult.CONSUME;
            }
            // Try inserting a seed
            if(heldItem.getItem() instanceof IAgriSeedItem) {
                player.getItemInHand(hand).setCount(analyzer.insertSeed(heldItem).getCount());
                return InteractionResult.CONSUME;
            }
            // Try extracting the journal
            if(analyzer.hasJournal()) {
                // Only allow extraction of journal with empty hand
                if(heldItem.isEmpty()) {
                    this.extractJournal(analyzer, player);
                    return InteractionResult.CONSUME;
                }
                return InteractionResult.FAIL;
            }
            // Try inserting a journal
            if(heldItem.getItem() instanceof IAgriJournalItem) {
                if (analyzer.insertJournal(heldItem).isEmpty()) {
                    heldItem.shrink(1);
                }
                return InteractionResult.CONSUME;
            }
            return InteractionResult.FAIL;
        } else {
            // On the client, inspect the genome
            if(world.isClientSide()) {
                if(!analyzer.isObserved()) {
                    if(this.isViewBlocked(world, pos, ORIENTATION.fetch(state))) {
                        player.sendMessage(AgriToolTips.MSG_ANALYZER_VIEW_BLOCKED, Util.NIL_UUID);
                    } else {
                        analyzer.setObserving(true);
                    }
                }
            }
            return InteractionResult.CONSUME;
        }
    }

    public void extractSeed(TileEntitySeedAnalyzer analyzer, @Nullable Player player) {
        if(analyzer.hasSeed()) {
            ItemStack seed = analyzer.extractSeed();
            if(player == null || !player.addItem(seed)) {
                if(analyzer.getLevel() != null) {
                    this.spawnItem(analyzer.getLevel(), analyzer.getBlockPos(), seed);
                }
            }
        }
    }

    public void extractJournal(TileEntitySeedAnalyzer analyzer, @Nullable Player player) {
        if(analyzer.hasJournal()) {
            ItemStack journal = analyzer.extractJournal();
            if(player == null || !player.addItem(journal)) {
                if(analyzer.getLevel() != null) {
                    this.spawnItem(analyzer.getLevel(), analyzer.getBlockPos(), journal);
                }
            }
        }
    }

    protected boolean isViewBlocked(Level world, BlockPos pos, Direction orientation) {
        BlockPos up = pos.above();
        if(!world.getBlockState(up).isAir()) {
            return true;
        }
        BlockPos front = pos.relative(orientation);
        if(!world.getBlockState(front).isAir()) {
            return true;
        }
        return !world.getBlockState(front.above()).isAir();
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder context) {
        List<ItemStack> drops = Lists.newArrayList();
        drops.add(new ItemStack(AgriApi.getAgriContent().getItems().getSeedAnalyzerItem()));
        BlockEntity tile = context.getParameter(LootContextParams.BLOCK_ENTITY);
        if(tile instanceof TileEntitySeedAnalyzer) {
            TileEntitySeedAnalyzer analyzer = (TileEntitySeedAnalyzer) tile;
            if(analyzer.hasSeed()) {
                drops.add(analyzer.getSeed());
            }
            if(analyzer.hasJournal()) {
                drops.add(analyzer.getJournal());
            }
        }
        return drops;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public RenderType getRenderType() {
        return RenderType.cutout();
    }
}
