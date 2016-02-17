package com.infinityraider.agricraft.utility;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

/**
 * Copied from MinecraftForge for 1.7.10 and added methods to convert between AgriForgeDirection and EnumFacing.
 * 
 * This class was renamed as to prevent conflict.
 */
public enum AgriForgeDirection {
    /** -Y */
    DOWN(0, -1, 0, EnumFacing.DOWN),

    /** +Y */
    UP(0, 1, 0, EnumFacing.UP),

    /** -Z */
    NORTH(0, 0, -1, EnumFacing.NORTH),

    /** +Z */
    SOUTH(0, 0, 1, EnumFacing.SOUTH),

    /** -X */
    WEST(-1, 0, 0, EnumFacing.WEST),

    /** +X */
    EAST(1, 0, 0, EnumFacing.EAST),

    /**
     * Used only by getOrientation, for invalid inputs
     */
    UNKNOWN(0, 0, 0, null);

    public final int offsetX;
    public final int offsetY;
    public final int offsetZ;
    private final EnumFacing enumFacing;
    public final int flag;
    public static final AgriForgeDirection[] VALID_DIRECTIONS = {DOWN, UP, NORTH, SOUTH, WEST, EAST};
    public static final int[] OPPOSITES = {1, 0, 3, 2, 5, 4, 6};
	public static final int[] ORDINALS = {2, 4, 3, 5};
    // Left hand rule rotation matrix for all possible axes of rotation
    public static final int[][] ROTATION_MATRIX = {
            {0, 1, 4, 5, 3, 2, 6},
            {0, 1, 5, 4, 2, 3, 6},
            {5, 4, 2, 3, 0, 1, 6},
            {4, 5, 2, 3, 1, 0, 6},
            {2, 3, 1, 0, 4, 5, 6},
            {3, 2, 0, 1, 4, 5, 6},
            {0, 1, 2, 3, 4, 5, 6},
    };

    private AgriForgeDirection(int x, int y, int z, EnumFacing enumFacing) {
        offsetX = x;
        offsetY = y;
        offsetZ = z;
        this.enumFacing = enumFacing;
        flag = 1 << ordinal();
    }

    public static AgriForgeDirection getOrientation(int id) {
        if (id >= 0 && id < VALID_DIRECTIONS.length) {
            return VALID_DIRECTIONS[id];
        }
        return UNKNOWN;
    }
	
	public static AgriForgeDirection getCardinal(int id) {
        return VALID_DIRECTIONS[ORDINALS[(id < 0 ? -id : id) % 4]];
    }

    public AgriForgeDirection getOpposite() {
        return getOrientation(OPPOSITES[ordinal()]);
    }

    public AgriForgeDirection getRotation(AgriForgeDirection axis){
        return getOrientation(ROTATION_MATRIX[axis.ordinal()][ordinal()]);
    }

    public EnumFacing getEnumFacing() {
        return enumFacing;
    }

    public static AgriForgeDirection getFromEnumFacing(EnumFacing facing) {
        if(facing == null) {
            return UNKNOWN;
        }
        return values()[facing.ordinal()];
    }

    public BlockPos offset(BlockPos pos) {
        return pos.add(this.offsetX, this.offsetY, this.offsetZ);
    }
}
