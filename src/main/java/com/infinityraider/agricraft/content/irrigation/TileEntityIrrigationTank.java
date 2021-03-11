package com.infinityraider.agricraft.content.irrigation;

import com.infinityraider.agricraft.AgriCraft;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TileEntityIrrigationTank extends TileEntityIrrigationComponent {
    private final AutoSyncedField<BlockPos> min;
    private final AutoSyncedField<BlockPos> max;

    public TileEntityIrrigationTank() {
        super(AgriCraft.instance.getModTileRegistry().irrigation_tank);
        this.min = this.getAutoSyncedFieldBuilder(new BlockPos(0, 0, 0)).build();
        this.max = this.getAutoSyncedFieldBuilder(new BlockPos(0, 0, 0)).build();
    }

    @Override
    public void setWorldAndPos(World world, BlockPos pos) {
        super.setWorldAndPos(world, pos);
        this.min.set(pos);
        this.max.set(pos);
    }

    public BlockPos getMultiBlockMin() {
        return this.min.get();
    }

    public BlockPos getMultiBlockMax() {
        return this.max.get();
    }

    public void checkAndFormMultiBlock() {
        // Safety check
        if(this.getWorld() == null || this.getWorld().isRemote()) {
            return;
        }
        // Find potential min position
        BlockPos.Mutable min = new BlockPos.Mutable();
        min.setPos(this.getPos());
        min = this.checkDirection(min, Direction.WEST);
        min = this.checkDirection(min, Direction.DOWN);
        min = this.checkDirection(min, Direction.NORTH);
        BlockPos.Mutable max = new BlockPos.Mutable();
        // Find potential max position
        max.setPos(this.getPos());
        max = this.checkDirection(max, Direction.EAST);
        max = this.checkDirection(max, Direction.UP);
        max = this.checkDirection(max, Direction.SOUTH);
        // Fetch and check all tanks in the range
        TileEntityIrrigationTank[][][] tanks = this.getTanksInRange(min, max);
        if(tanks == null) {
            return;
        }
        // Form the multi-block
        this.formMultiBlock(tanks, min, max);
    }

    protected BlockPos.Mutable checkDirection(BlockPos.Mutable pos, Direction dir) {
        if(this.getWorld() == null) {
            return pos;
        }
        // Check the first offset
        pos.move(dir);
        // Check the tile
        TileEntity tile = this.getWorld().getTileEntity(pos);
        while(tile instanceof TileEntityIrrigationTank) {
            // The tank must be the same material
            TileEntityIrrigationTank tank = (TileEntityIrrigationTank) tile;
            if(!ItemStack.areItemsEqual(this.getMaterial(), tank.getMaterial())) {
                break;
            }
            // If the tank is already part of a multi-block, we will simply continue from its limit
            pos.setPos(dir.getAxisDirection().getOffset() > 0 ? tank.getMultiBlockMax() : tank.getMultiBlockMin());
            // Increment along the offset
            pos.move(dir);
            // Fetch tile at new position to check in the next iteration
            tile = this.getWorld().getTileEntity(pos);
        }
        // The last iteration failed, therefore, return one increment and return it
        pos.move(dir.getOpposite(), 1);
        return pos;
    }

    @Nullable
    protected TileEntityIrrigationTank[][][] getTanksInRange(BlockPos min, BlockPos max) {
        // Safety check
        if(this.getWorld() == null || this.getWorld().isRemote()) {
            return null;
        }
        int dx = max.getX() - min.getX() + 1;
        int dy = max.getY() - min.getY() + 1;
        int dz = max.getZ() - min.getZ() + 1;
        TileEntityIrrigationTank[][][] tanks = new TileEntityIrrigationTank[dx][dy][dz];
        // Iterate over all positions in the range and check if they are valid
        BlockPos.Mutable pos = new BlockPos.Mutable();
        for(int x = min.getX(); x <= max.getX(); x++) {
            for(int y = min.getY(); y <= max.getY(); y++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    pos.setPos(x, y, z);
                    if(pos.equals(this.getPos())) {
                        // The position is the own position, no need to check, it is valid by default
                        tanks[x - min.getX()][y - min.getY()][z - min.getZ()] = this;
                    } else {
                        // Check the position
                        TileEntity tile = this.getWorld().getTileEntity(pos);
                        if(!(tile instanceof TileEntityIrrigationTank)) {
                            // tile is not a tank, therefore not a valid multi-block
                            return null;
                        }
                        TileEntityIrrigationTank tank = (TileEntityIrrigationTank) tile;
                        if(!ItemStack.areItemsEqual(this.getMaterial(), tank.getMaterial())) {
                            // tank is of a different material, therefore not a valid multi-block
                            return null;
                        }
                        // Valid tank, add it to the map
                        tanks[x - min.getX()][y - min.getY()][z - min.getZ()] = tank;
                    }
                }
            }
        }
        return tanks;
    }

    protected void formMultiBlock(TileEntityIrrigationTank[][][] tanks, BlockPos min, BlockPos max) {
        // Safety check
        if(this.getWorld() == null || this.getWorld().isRemote()) {
            return;
        }
        for(int i = 0; i < tanks.length; i++) {
            for(int j = 0; j < tanks[i].length; j++) {
                for(int k = 0; k < tanks[i][j].length; k++) {
                    // Fetch tank
                    TileEntityIrrigationTank tank = tanks[i][j][k];
                    // Set the multi-block limits
                    tank.min.set(new BlockPos(min));
                    tank.max.set(new BlockPos(max));
                    // Fetch the block state
                    BlockState state = tank.getBlockState();
                    state = this.handleDirection(state, tank, Direction.WEST, i == 0);
                    state = this.handleDirection(state, tank, Direction.EAST, i == tanks.length - 1);
                    state = this.handleDirection(state, tank, Direction.DOWN, j == 0);
                    state = this.handleDirection(state, tank, Direction.NORTH, k == 0);
                    state = this.handleDirection(state, tank, Direction.SOUTH, k == tanks[i][j].length - 1);
                    // Update the block state
                    this.getWorld().setBlockState(tank.getPos(), state);
                }
            }
        }
    }

    protected BlockState handleDirection(final BlockState state, TileEntityIrrigationTank tank, Direction dir, boolean edge) {
        if (tank.getWorld() == null || tank.getWorld().isRemote()) {
            return state;
        }
        if (dir.getAxis().isVertical()) {
            return BlockIrrigationTank.DOWN.apply(state, !edge);
        }
        return BlockIrrigationTank.getConnection(dir)
                .map((connection) -> {
                    if (edge) {
                        BlockPos offset = tank.getPos().offset(dir);
                        TileEntity tile = tank.getWorld().getTileEntity(offset);
                        if (tile instanceof TileEntityIrrigationChannel) {
                            if (ItemStack.areItemsEqual(tank.getMaterial(), ((TileEntityIrrigationChannel) tile).getMaterial())) {
                                // The block is next to a channel, update channel block state
                                BlockIrrigationChannelAbstract.getConnection(dir.getOpposite()).ifPresent(channelCon ->
                                        tank.getWorld().setBlockState(offset, channelCon.apply(tile.getBlockState(), true)));
                                // And return the tank block state
                                return connection.apply(state, BlockIrrigationTank.Connection.CHANNEL);
                            }
                        }
                    } else {
                        // The block is not near an edge
                        return connection.apply(state, BlockIrrigationTank.Connection.TANK);
                    }
                    return connection.apply(state, BlockIrrigationTank.Connection.NONE);
                }).orElse(state);
    }

    @Override
    protected void writeTileNBT(@Nonnull CompoundNBT tag) {

    }

    @Override
    protected void readTileNBT(@Nonnull BlockState state, @Nonnull CompoundNBT tag) {

    }
}
