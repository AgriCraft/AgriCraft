package com.agricraft.agricraft.common.block;

import com.agricraft.agricraft.common.block.entity.SeedAnalyzerBlockEntity;
import com.agricraft.agricraft.common.registry.ModBlocks;
import com.agricraft.agricraft.common.util.Platform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class SeedAnalyzerBlock extends Block implements EntityBlock {

	public static final BooleanProperty JOURNAL = BooleanProperty.create("journal");
	private static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 4, 15);

	public SeedAnalyzerBlock() {
		super(Properties.of().mapColor(MapColor.WOOD).strength(2, 3).noOcclusion());
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(JOURNAL, false)
				.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(JOURNAL, BlockStateProperties.HORIZONTAL_FACING);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState state = this.defaultBlockState();
		return state.setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (hand == InteractionHand.OFF_HAND) {
			return InteractionResult.FAIL;
		}
		BlockEntity blockEntity = level.getBlockEntity(pos);
		ItemStack heldItem = player.getItemInHand(hand);
		if (!(blockEntity instanceof SeedAnalyzerBlockEntity analyzer)) {
			return InteractionResult.FAIL;
		}
		if (player.isShiftKeyDown()) {
			// extract seeds
			if (analyzer.hasSeed()) {
				ItemStack seed = analyzer.extractSeed();
				if (!player.addItem(seed)) {
					level.addFreshEntity(new ItemEntity(level, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, seed));
				}

				return InteractionResult.CONSUME;
			}
			// extract journal
			if (analyzer.hasJournal()) {
				if (heldItem.isEmpty()) {
					ItemStack journal = analyzer.extractJournal();
					player.setItemInHand(hand, journal);
					return InteractionResult.CONSUME;
				} else {
					return InteractionResult.FAIL;
				}
			}
			return InteractionResult.FAIL;
		} else {
			if (!level.isClientSide) {
				Platform.get().openMenu((ServerPlayer) player, analyzer);
			}
			return InteractionResult.CONSUME;
		}
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
		List<ItemStack> drops = super.getDrops(state, params);
		drops.add(ModBlocks.SEED_ANALYZER.get().asItem().getDefaultInstance());
		BlockEntity blockEntity = params.getParameter(LootContextParams.BLOCK_ENTITY);
		if (blockEntity instanceof SeedAnalyzerBlockEntity analyzer) {
			if (analyzer.hasSeed()) {
				drops.add(analyzer.getSeed());
			}
			if (analyzer.hasJournal()) {
				drops.add(analyzer.getJournal());
			}
		}
		return drops;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new SeedAnalyzerBlockEntity(pos, state);
	}

}
