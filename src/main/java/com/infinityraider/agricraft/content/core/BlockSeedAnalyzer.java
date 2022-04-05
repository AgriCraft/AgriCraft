package com.infinityraider.agricraft.content.core;

import com.google.common.collect.Lists;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.content.items.IAgriJournalItem;
import com.infinityraider.agricraft.api.v1.content.items.IAgriSeedItem;
import com.infinityraider.agricraft.reference.AgriToolTips;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.block.BlockBaseTile;
import com.infinityraider.infinitylib.block.property.InfProperty;
import com.infinityraider.infinitylib.block.property.InfPropertyConfiguration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.BiFunction;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockSeedAnalyzer extends BlockBaseTile<TileEntitySeedAnalyzer> implements IWaterLoggable {
    // Properties
    public static final InfProperty<Direction> ORIENTATION = InfProperty.Creators.createHorizontals("orientation", Direction.NORTH);
    public static final InfProperty<Boolean> JOURNAL = InfProperty.Creators.create("journal", false);

    private static final InfPropertyConfiguration PROPERTIES = InfPropertyConfiguration.builder()
            .add(ORIENTATION)
            .add(JOURNAL)
            .waterloggable()
            .build();

    // TileEntity factory
    private static final BiFunction<BlockState, IBlockReader, TileEntitySeedAnalyzer> TILE_FACTORY = (s, w) -> new TileEntitySeedAnalyzer();

    // VoxelShapes
    private static final VoxelShape SHAPE = Block.makeCuboidShape(1, 0, 1, 15, 4, 15);

    public BlockSeedAnalyzer() {
        super(Names.Blocks.SEED_ANALYZER, Properties.create(Material.WOOD)
                .notSolid()
        );
    }

    @Override
    protected InfPropertyConfiguration getPropertyConfiguration() {
        return PROPERTIES;
    }

    @Override
    public BiFunction<BlockState, IBlockReader, TileEntitySeedAnalyzer> getTileEntityFactory() {
        return TILE_FACTORY;
    }

    @Override
    public Item asItem() {
        return AgriCraft.instance.getModItemRegistry().seed_analyzer;
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
        return this.getRayTraceShape(state, world, pos, ISelectionContext.dummy());
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return this.getShape(state, world, pos, context);
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
    public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos) {
        BlockState current = world.getBlockState(pos);
        return current.getMaterial().isReplaceable();
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState state = this.getDefaultState();
        if(state.isValidPosition(context.getWorld(), context.getPos())) {
            return ORIENTATION.apply(
                    this.fluidlog(state, context.getWorld(), context.getPos()),
                    context.getPlacementHorizontalFacing().getOpposite());
        }
        return null;
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if(hand == Hand.OFF_HAND) {
            return ActionResultType.FAIL;
        }
        TileEntity tile = world.getTileEntity(pos);
        if(!(tile instanceof TileEntitySeedAnalyzer)) {
            return ActionResultType.FAIL;
        }
        TileEntitySeedAnalyzer analyzer = (TileEntitySeedAnalyzer) tile;
        ItemStack heldItem = player.getHeldItem(hand);
        // debugging
        if(heldItem.getItem() instanceof ItemDebugger) {
            return ActionResultType.PASS;
        }
        // Player is sneaking: insertion / extraction logic
        if(player.isSneaking()) {
            // No sneak-action on the client
            if (world.isRemote()) {
                return ActionResultType.PASS;
            }
            // Try extracting the seed
            if(analyzer.hasSeed()) {
                this.extractSeed(analyzer, player);
                return ActionResultType.CONSUME;
            }
            // Try inserting a seed
            if(heldItem.getItem() instanceof IAgriSeedItem) {
                player.getHeldItem(hand).setCount(analyzer.insertSeed(heldItem).getCount());
                return ActionResultType.CONSUME;
            }
            // Try extracting the journal
            if(analyzer.hasJournal()) {
                // Only allow extraction of journal with empty hand
                if(heldItem.isEmpty()) {
                    this.extractJournal(analyzer, player);
                    return ActionResultType.CONSUME;
                }
                return ActionResultType.FAIL;
            }
            // Try inserting a journal
            if(heldItem.getItem() instanceof IAgriJournalItem) {
                if (analyzer.insertJournal(heldItem).isEmpty()) {
                    heldItem.shrink(1);
                }
                return ActionResultType.CONSUME;
            }
            return ActionResultType.FAIL;
        } else {
            // On the client, inspect the genome
            if(world.isRemote()) {
                if(!analyzer.isObserved()) {
                    if(this.isViewBlocked(world, pos, ORIENTATION.fetch(state))) {
                        player.sendMessage(AgriToolTips.MSG_ANALYZER_VIEW_BLOCKED, Util.DUMMY_UUID);
                    } else {
                        analyzer.setObserving(true);
                    }
                }
            }
            return ActionResultType.CONSUME;
        }
    }

    public void extractSeed(TileEntitySeedAnalyzer analyzer, @Nullable PlayerEntity player) {
        if(analyzer.hasSeed()) {
            ItemStack seed = analyzer.extractSeed();
            if(player == null || !player.addItemStackToInventory(seed)) {
                if(analyzer.getWorld() != null) {
                    this.spawnItem(analyzer.getWorld(), analyzer.getPos(), seed);
                }
            }
        }
    }

    public void extractJournal(TileEntitySeedAnalyzer analyzer, @Nullable PlayerEntity player) {
        if(analyzer.hasJournal()) {
            ItemStack journal = analyzer.extractJournal();
            if(player == null || !player.addItemStackToInventory(journal)) {
                if(analyzer.getWorld() != null) {
                    this.spawnItem(analyzer.getWorld(), analyzer.getPos(), journal);
                }
            }
        }
    }

    protected boolean isViewBlocked(World world, BlockPos pos, Direction orientation) {
        BlockPos up = pos.up();
        if(!world.isAirBlock(up)) {
            return true;
        }
        BlockPos front = pos.offset(orientation);
        if(!world.isAirBlock(front)) {
            return true;
        }
        return !world.isAirBlock(front.up());
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder context) {
        List<ItemStack> drops = Lists.newArrayList();
        drops.add(new ItemStack(AgriCraft.instance.getModItemRegistry().seed_analyzer));
        TileEntity tile = context.get(LootParameters.BLOCK_ENTITY);
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
        return RenderType.getCutout();
    }
}
