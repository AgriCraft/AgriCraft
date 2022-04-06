package com.infinityraider.agricraft.content.decoration;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.content.items.IAgriClipperItem;
import com.infinityraider.agricraft.content.AgriItemRegistry;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.block.BlockDynamicTexture;
import com.infinityraider.infinitylib.block.property.InfProperty;
import com.infinityraider.infinitylib.block.property.InfPropertyConfiguration;
import com.infinityraider.infinitylib.reference.Constants;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockGrate extends BlockDynamicTexture<TileEntityGrate> {
    // Properties
    public static final InfProperty<Direction.Axis> AXIS = InfProperty.Creators.create("direction", Direction.Axis.X);
    public static final InfProperty<Offset> OFFSET = InfProperty.Creators.create("offset", Offset.class, Offset.MID);
    public static final InfProperty<Vines> VINES = InfProperty.Creators.create("vines", Vines.class, Vines.NONE);

    private static final InfPropertyConfiguration PROPERTIES = InfPropertyConfiguration.builder()
            .add(AXIS)
            .add(OFFSET)
            .add(VINES)
            .waterloggable()
            .build();

    // TileEntity factory
    private static final BiFunction<BlockPos, BlockState, TileEntityGrate> TILE_FACTORY = TileEntityGrate::new;

    // VoxelShapes
    protected static VoxelShape getShape(Direction.Axis axis, Offset offset) {
        switch (axis) {
            case X: return getShape(X_SHAPES, offset);
            case Y: return getShape(Y_SHAPES, offset);
            case Z: return getShape(Z_SHAPES, offset);
            // Should never happen
            default: return Shapes.empty();
        }
    }

    public static final VoxelShape X_DEFAULT = Stream.of(
            Block.box(7, 1, 0, 9, 3, 16),
            Block.box(7, 5, 0, 9, 7, 16),
            Block.box(7, 9, 0, 9, 11, 16),
            Block.box(7, 13, 0, 9, 15, 16),
            Block.box(7, 0, 1, 9, 16, 3),
            Block.box(7, 0, 5, 9, 16, 7),
            Block.box(7, 0, 9, 9, 16, 11),
            Block.box(7, 0, 13, 9, 16, 15)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape X_NEAR = X_DEFAULT.move(Offset.NEAR.getOffset(), 0, 0);

    public static final VoxelShape X_FAR = X_DEFAULT.move(Offset.FAR.getOffset(), 0, 0);

    public static final VoxelShape[] X_SHAPES = {X_NEAR, X_DEFAULT, X_FAR};

    public static final VoxelShape Y_DEFAULT = Stream.of(
            Block.box(1, 7, 0, 3, 9, 16),
            Block.box(5, 7, 0, 7, 9, 16),
            Block.box(9, 7, 0, 11, 9, 16),
            Block.box(13, 7, 0, 15, 9, 16),
            Block.box(0, 7, 1, 16, 9, 3),
            Block.box(0, 7, 5, 16, 9, 7),
            Block.box(0, 7, 9, 16, 9, 11),
            Block.box(0, 7, 13, 16, 9, 15)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape Y_NEAR = Y_DEFAULT.move(0, Offset.NEAR.getOffset(), 0);

    public static final VoxelShape Y_FAR = Y_DEFAULT.move(0, Offset.FAR.getOffset(), 0);

    public static final VoxelShape[] Y_SHAPES = {Y_NEAR, Y_DEFAULT, Y_FAR};

    public static final VoxelShape Z_DEFAULT = Stream.of(
            Block.box(1, 0, 7, 3, 16, 9),
            Block.box(5, 0, 7, 7, 16, 9),
            Block.box(9, 0, 7, 11, 16, 9),
            Block.box(13, 0, 7, 15, 16, 9),
            Block.box(0, 1, 7, 16, 3, 9),
            Block.box(0, 5, 7, 16, 7, 9),
            Block.box(0, 9, 7, 16, 11, 9),
            Block.box(0, 13, 7, 16, 15, 9)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape Z_NEAR = Z_DEFAULT.move(0, 0, Offset.NEAR.getOffset());

    public static final VoxelShape Z_FAR = Z_DEFAULT.move(0, 0, Offset.FAR.getOffset());

    public static final VoxelShape[] Z_SHAPES = {Z_NEAR, Z_DEFAULT, Z_FAR};

    public BlockGrate() {
        super(Names.Blocks.GRATE, Properties.of(Material.WOOD)
                .noOcclusion()
        );
    }

    @Override
    protected InfPropertyConfiguration getPropertyConfiguration() {
        return PROPERTIES;
    }

    @Override
    public BiFunction<BlockPos, BlockState, TileEntityGrate> getTileEntityFactory() {
        return TILE_FACTORY;
    }

    @Nonnull
    @Override
    public ItemGrate asItem() {
        return AgriItemRegistry.GRATE;
    }

    @Override
    public void setPlacedBy(@Nonnull Level world, @Nonnull BlockPos pos, @Nonnull BlockState state,
                            @Nullable LivingEntity placer, @Nonnull ItemStack stack, @Nullable BlockEntity tile) {
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockState current = world.getBlockState(pos);
        return current.getMaterial().isReplaceable();
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = this.defaultBlockState();
        if (state.canSurvive(context.getLevel(), context.getClickedPos())) {
            BlockPos clicked = context.getClickedPos().relative(context.getClickedFace().getOpposite());
            BlockState target = context.getLevel().getBlockState(clicked);
            // If a grate is clicked on a grate, mimic its placement if a side face is clicked
            if (target.getBlock() instanceof BlockGrate) {
                if (context.getClickedFace().getAxis() != AXIS.fetch(target)) {
                    return AXIS.apply(
                            OFFSET.apply(
                                    this.fluidlog(state, context.getLevel(), context.getClickedPos()),
                                    OFFSET.fetch(target)
                            ),
                            AXIS.fetch(target)
                    );
                }
            }
            // Determine the axis according to the face the player clicked and his look vector
            Vec3 hit = context.getClickLocation();
            double offset;
            if (context.getClickedFace().getAxis() == Direction.Axis.Y) {
                // The player clicked a horizontal face, determine the axis based on the player's orientation
                if (context.getHorizontalDirection().getAxis() == Direction.Axis.X) {
                    // player is looking in the X direction
                    state = AXIS.apply(state, Direction.Axis.X);
                    offset = hit.x() - ((int) hit.x());
                } else {
                    // player is looking in the Z direction
                    state = AXIS.apply(state, Direction.Axis.Z);
                    offset = hit.z() - ((int) hit.z());
                }
            } else {
                // The player clicked a vertical face, the axis will be Y
                state = AXIS.apply(state, Direction.Axis.Y);
                offset = hit.y() - ((int) hit.y());
            }
            // Finally, determine the offset by how far along the block the player clicked
            offset += offset < 0 ? 1 : 0;
            if (offset >= 11 * Constants.UNIT) {
                return OFFSET.apply(
                        this.fluidlog(state, context.getLevel(), context.getClickedPos()),
                        Offset.FAR);
            } else if (offset <= 5 * Constants.UNIT) {
                return OFFSET.apply(
                        this.fluidlog(state, context.getLevel(), context.getClickedPos()),
                        Offset.NEAR);
            } else {
                return OFFSET.apply(
                        this.fluidlog(state, context.getLevel(), context.getClickedPos()),
                        Offset.MID);
            }
        }
        return null;
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if(!world.isClientSide()) {
            ItemStack item = player.getItemInHand(hand);
            if(!item.isEmpty()) {
                if(item.getItem() == Items.VINE) {
                    Vines vines = VINES.fetch(state);
                    if (!vines.hasVines(hit)) {
                        world.setBlock(pos, VINES.apply(state, vines.addVines(hit)), 3);
                        if(!player.isCreative()) {
                            item.shrink(1);
                        }
                    }
                } else if(item.getItem() instanceof IAgriClipperItem) {
                    Vines vines = VINES.fetch(state);
                    if (vines.hasVines(hit)) {
                        world.setBlock(pos, VINES.apply(state, vines.removeVines(hit)), 3);
                        if(!player.isCreative()) {
                            this.addToInventoryOrDrop(new ItemStack(Items.VINE, 1), world, pos, player);
                        }
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean isLadder(BlockState state, LevelReader world, BlockPos pos, LivingEntity entity) {
        return AgriCraft.instance.getConfig().areGratesClimbable() && (AXIS.fetch(state) != Direction.Axis.Y);
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
        return getShape(AXIS.fetch(state), OFFSET.fetch(state));
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
        return this.getShape(state, world, pos, context);
    }

    protected static VoxelShape getShape(VoxelShape[] shapes, Offset offset) {
        return shapes[offset.ordinal()];
    }

    @Override
    public void addDrops(Consumer<ItemStack> dropAcceptor, BlockState state, TileEntityGrate tile, LootContext.Builder context) {
        int vineCount = VINES.fetch(state).getVineCount();
        if(vineCount > 0) {
            dropAcceptor.accept(new ItemStack(Items.VINE, vineCount));
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public RenderType getRenderType() {
        return RenderType.cutout();
    }

    @Nullable
    @Override
    @OnlyIn(Dist.CLIENT)
    public BlockColor getColor() {
        return (state, reader, pos, color) -> {
            if(color == 0) {
                return 0xFFFFFF;
            }
            return reader != null && pos != null ? BiomeColors.getAverageFoliageColor(reader, pos) : FoliageColor.getDefaultColor();
        };
    }

    public enum Offset implements StringRepresentable {
        NEAR(-6.99*Constants.UNIT),
        MID(0),
        FAR(6.99*Constants.UNIT);

        private final double offset;

        Offset(double offset) {
            this.offset = offset;
        }

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase();
        }

        public double getOffset() {
            return this.offset;
        }
    }

    public enum Vines implements StringRepresentable {
        NONE(0),
        FRONT(1),
        BACK(1),
        BOTH(2);

        private final int vineCount;

        Vines(int vineCount) {
            this.vineCount = vineCount;
        }

        public int getVineCount() {
            return this.vineCount;
        }

        public boolean hasVines(BlockHitResult hit) {
            return this.hasVines(hit.getDirection().getOpposite());
        }

        public boolean hasVines(Direction direction) {
            return this.hasVines(direction.getAxisDirection());
        }

        public boolean hasVines(Direction.AxisDirection direction) {
            switch (this) {
                case FRONT: return direction == Direction.AxisDirection.POSITIVE;
                case BACK: return direction == Direction.AxisDirection.NEGATIVE;
                case BOTH: return true;
            }
            return false;
        }

        public Vines addVines(BlockHitResult hit) {
            return this.addVines(hit.getDirection().getOpposite());
        }

        public Vines addVines(Direction direction) {
            return this.addVines(direction.getAxisDirection());
        }

        public Vines addVines(Direction.AxisDirection direction) {
            switch (this) {
                case NONE: return direction == Direction.AxisDirection.POSITIVE ? FRONT : BACK;
                case FRONT: return direction == Direction.AxisDirection.POSITIVE ? FRONT : BOTH;
                case BACK: return direction == Direction.AxisDirection.POSITIVE ? BOTH : BACK;
            }
            return BOTH;
        }

        public Vines removeVines(BlockHitResult hit) {
            return this.removeVines(hit.getDirection().getOpposite());
        }

        public Vines removeVines(Direction direction) {
            return this.removeVines(direction.getAxisDirection());
        }

        public Vines removeVines(Direction.AxisDirection direction) {
            switch (this) {
                case FRONT: return direction == Direction.AxisDirection.POSITIVE ? NONE : FRONT;
                case BACK: return direction == Direction.AxisDirection.POSITIVE ? BACK : NONE;
                case BOTH: return direction == Direction.AxisDirection.POSITIVE ? BACK : FRONT;
            }
            return NONE;
        }

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase();
        }
    }
}
