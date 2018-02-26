/*
 */
package com.infinityraider.agricraft.api.v1.util;

import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import net.minecraft.util.math.BlockPos;

/**
 * Class for representing block ranges.
 */
public class BlockRange implements Iterable<BlockPos> {

    private final int minX, minY, minZ;
    private final int maxX, maxY, maxZ;

    public BlockRange(@Nonnull BlockRange range, @Nonnull BlockPos pos) {
        this(range.getMin().add(pos), range.getMax().add(pos));
    }

    public BlockRange(@Nonnull BlockPos center, int radius) {
        this(center.getX(), center.getY(), center.getZ(), radius);
    }

    public BlockRange(@Nonnull BlockPos min, @Nonnull BlockPos max) {
        this(
                min.getX(), min.getY(), min.getZ(),
                max.getX(), max.getY(), max.getZ()
        );
    }

    public BlockRange(int x, int y, int z, int radius) {
        this(
                x - radius, y - radius, z - radius,
                x + radius, y + radius, z + radius
        );
    }

    public BlockRange(int x1, int y1, int z1, int x2, int y2, int z2) {
        if (x1 < x2) {
            this.minX = x1;
            this.maxX = x2;
        } else {
            this.minX = x2;
            this.maxX = x1;
        }
        if (y1 < y2) {
            this.minY = y1;
            this.maxY = y2;
        } else {
            this.minY = y2;
            this.maxY = y1;
        }
        if (z1 < z2) {
            this.minZ = z1;
            this.maxZ = z2;
        } else {
            this.minZ = z2;
            this.maxZ = z1;
        }
    }

    @Nonnull
    public BlockPos getMin() {
        return new BlockPos(minX, minY, minZ);
    }

    @Nonnull
    public BlockPos getMax() {
        return new BlockPos(maxX, maxY, maxZ);
    }

    public int getMinX() {
        return minX;
    }

    public int getMinY() {
        return minY;
    }

    public int getMinZ() {
        return minZ;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getMaxZ() {
        return maxZ;
    }

    public int getVolume() {
        return (maxX - minX + 1)
                * (maxY - minY + 1)
                * (maxZ - minZ + 1);
    }

    public boolean contains(BlockPos pos) {
        return pos.getX() >= minX
                && pos.getY() >= minY
                && pos.getZ() >= minZ
                && pos.getX() <= maxX
                && pos.getY() <= maxY
                && pos.getZ() <= maxZ;
    }

    public boolean intersects(BlockRange range) {
        return ((this.minX < range.maxX) && (this.maxX > range.minX))
                && ((this.minY < range.maxY) && (this.maxY > range.minY))
                && ((this.minZ < range.maxZ) && (this.maxZ > range.minZ));
    }

    @Nonnull
    @Override
    public Iterator<BlockPos> iterator() {
        return new BlockRangeIterator(this);
    }

    @Nonnull
    public Stream<BlockPos> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }

    /**
     * An iterator over a block range.
     *
     * Thank you findbugs for noting that this should have been static.
     */
    public static class BlockRangeIterator implements Iterator<BlockPos> {

        private int x, y, z;
        private final BlockRange range;

        public BlockRangeIterator(@Nonnull BlockRange range) {
            this.range = Objects.requireNonNull(range, "You cannot iterate over a range that doesn't exist!");
            this.x = range.minX;
            this.y = range.minY;
            this.z = range.minZ;
        }

        @Override
        public boolean hasNext() {
            return this.x <= this.range.getMaxX()
                    && this.y <= this.range.getMaxY()
                    && this.z <= this.range.getMaxZ();
        }

        @Nonnull
        @Override
        public BlockPos next() {
            // Ensure haven't fallen out of bounds.
            if (!hasNext()) {
                throw new IndexOutOfBoundsException();
            }

            // Create point to return.
            final BlockPos pos = new BlockPos(x, y, z);

            // Post-Increment
            this.x = this.x + 1;
            if (this.x > this.range.maxX) {
                this.x = this.range.minX;
                this.y = this.y + 1;
                if (this.y > this.range.maxY) {
                    this.y = this.range.minY;
                    this.z = this.z + 1;
                }
            }

            // Return point.
            return pos;
        }

    }

}
