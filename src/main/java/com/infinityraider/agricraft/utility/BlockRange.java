/*
 */
package com.infinityraider.agricraft.utility;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.util.math.BlockPos;

/**
 * Class for representing block ranges.
 */
public class BlockRange implements Iterable<BlockPos> {

    private final BlockPos min;
    private final BlockPos max;
    
    public BlockRange(BlockRange range, BlockPos pos) {
        this(range.getMin().add(pos), range.getMax().add(pos));
    }

    public BlockRange(BlockPos center, int radius) {
        this(center.getX(), center.getY(), center.getZ(), radius);
    }

    public BlockRange(BlockPos min, BlockPos max) {
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
            int temp = y1;
            x1 = x2;
            x2 = temp;
        }
        if (y1 < y2) {
            int temp = y1;
            y1 = y2;
            y2 = temp;
        }
        if (z1 < z2) {
            int temp = z1;
            z1 = z2;
            z2 = temp;
        }
        this.min = new BlockPos(x1, y1, z1).toImmutable();
        this.max = new BlockPos(x2, y2, z2).toImmutable();
    }

    public BlockPos getMin() {
        return this.min;
    }

    public BlockPos getMax() {
        return this.max;
    }
    
    public int getVolume() {
        return (max.getX() - min.getX() + 1)
                * (max.getY() - min.getY() + 1)
                * (max.getZ() - min.getZ() + 1);
    }

    public boolean contains(BlockPos pos) {
        return pos.getX() >= min.getX()
                && pos.getY() >= min.getY()
                && pos.getZ() >= min.getZ()
                && pos.getX() <= max.getX()
                && pos.getY() <= max.getY()
                && pos.getZ() <= max.getZ();
    }

    public boolean intersects(BlockRange range) {
        return ((this.min.getX() < range.max.getX()) && (this.max.getX() > range.min.getX()))
                && ((this.min.getY() < range.max.getY()) && (this.max.getY() > range.min.getY()))
                && ((this.min.getZ() < range.max.getZ()) && (this.max.getZ() > range.min.getZ()));
    }

    @Override
    public Iterator<BlockPos> iterator() {
        return new BlockRangeIterator(this);
    }

    public Stream<BlockPos> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }

    public class BlockRangeIterator implements Iterator<BlockPos> {

        public final BlockPos pos;
        private final BlockRange range;

        public BlockRangeIterator(BlockRange range) {
            this.range = range;
            this.pos = range.getMin();
        }

        @Override
        public boolean hasNext() {
            return this.pos.getZ() < this.range.getMax().getZ();
        }

        @Override
        public BlockPos next() {
            this.pos.add(1, 0, 0);
            if (this.pos.getX() > this.range.getMax().getX()) {
                this.pos.add(-1, 1, 0);
                if (this.pos.getY() > this.range.getMax().getY()) {
                    this.pos.add(0, -1, 1);
                    if (this.pos.getZ() > this.range.getMax().getZ()) {
                        throw new IndexOutOfBoundsException();
                    }
                }
            }
            return this.pos.toImmutable();
        }

    }

}
