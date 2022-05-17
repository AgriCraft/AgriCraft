package com.infinityraider.agricraft.content.world.greenhouse;

import com.google.common.collect.Maps;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.capability.CapabilityGreenHouseParts;
import com.infinityraider.agricraft.reference.AgriNBT;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Map;

public class GreenHousePart {
    private final int id;
    private final Map<BlockPos, GreenHouseBlock> blocks;

    protected GreenHousePart(int id, LevelChunk chunk, Map<BlockPos, GreenHouseBlock> blocks) {
        this.id = id;
        this.blocks =blocks;
        CapabilityGreenHouseParts.addPart(chunk, this);
    }

    public GreenHousePart(ChunkPos chunk, CompoundTag tag) {
        this.id = tag.getInt(AgriNBT.KEY);
        this.blocks = Maps.newHashMap();
        tag.getList(AgriNBT.ENTRIES, Tag.TAG_COMPOUND).stream()
                .filter(blockTag -> blockTag instanceof CompoundTag)
                .map(blockTag -> (CompoundTag) blockTag)
                .forEach(blockTag -> {
                    BlockPos pos = AgriNBT.readBlockPos1(blockTag);
                    GreenHouseBlockType type = GreenHouseBlockType.values()[blockTag.getInt(AgriNBT.INDEX)];
                    boolean ceiling = blockTag.getBoolean(AgriNBT.FLAG);
                    this.blocks.put(pos, new GreenHouseBlock(pos, type, ceiling));
                });
    }

    public int getId() {
        return this.id;
    }

    public GreenHouseBlockType getType(BlockPos pos) {
        GreenHouseBlock block = this.getBlock(pos);
        return block == null ? GreenHouseBlockType.EXTERIOR : block.getType();
    }

    public void onBlockUpdated(Level world, BlockPos pos, GreenHouse greenHouse) {
        if(world.isClientSide()) {
            return;
        }
        GreenHouseBlock oldBlock = this.getBlock(pos);
        if(oldBlock == null) {
            // shouldn't ever happen
            AgriCraft.instance.getLogger().error("Caught a block update for a greenhouse at a null position");
            return;
        }
        // fetch new state and type
        BlockState newState = world.getBlockState(pos);
        GreenHouseBlockType newType = oldBlock.getType().getNewTypeForState(world, pos, newState, this.blocks::get);
        // no changes: return
        if(newType == oldBlock.getType()) {
            return;
        }
        // set the new block
        this.blocks.put(pos, new GreenHouseBlock(pos, newType, oldBlock.isCeiling()));
        // check if a new gap has been made
        if(newType.isGap()) {
            if(Arrays.stream(Direction.values()).map(dir -> this.getBlock(pos.relative(dir))).anyMatch(b -> b != null && b.isGap())) {
                // if there is a gap next to the new gap, remove the greenhouse
                greenHouse.remove(world);
            } else {
                // otherwise, notify the greenhouse of the new gap
                greenHouse.newGap();
                // also check for ceiling updates (do this after gap as gap state is prioritized over glass fraction
                if(oldBlock.isCeiling() && oldBlock.isGlass()) {
                    greenHouse.decrementCeilingGlassCount();
                }
            }
            return;
        }
        // check if an old gap has been closed
        if(oldBlock.isGap()) {
            if(newType.isGlass() && oldBlock.isCeiling()) {
                // first update the ceiling glass count (do this before gap as gap state is prioritized over glass fraction)
                greenHouse.incrementCeilingGlassCount();
            }
            // notify the greenhouse of a closed gap
            greenHouse.removeGap();
            return;
        }
        // no gap has been closed nor opened; check for ceiling updates
        if(oldBlock.isCeiling()) {
            if(newType.isGlass()) {
                greenHouse.incrementCeilingGlassCount();
            } else {
                greenHouse.decrementCeilingGlassCount();
            }
        }
    }

    @Nullable
    public GreenHouseBlock getBlock(BlockPos pos) {
        return this.blocks.getOrDefault(pos, null);
    }

    protected void onRemoved(Level world) {
        this.replaceAirBlocks(world, Blocks.AIR.defaultBlockState());
    }

    protected void replaceAirBlocks(Level world, GreenHouseState state) {
        BlockState air = state.isComplete()
                ? AgriApi.getAgriContent().getBlocks().getGreenHouseAirBlock().defaultBlockState()
                : Blocks.AIR.defaultBlockState();
        this.replaceAirBlocks(world, air);
    }

    protected void replaceAirBlocks(Level world, BlockState state) {
        this.blocks.values().stream()
                .filter(block -> block.getType().isAir())
                .map(GreenHouseBlock::getPos)
                .forEach(pos -> world.setBlock(pos, state, 3));
    }

    public CompoundTag writeToTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt(AgriNBT.KEY, this.id);
        ListTag blocks = new ListTag();
        this.blocks.forEach((pos, block) -> {
            CompoundTag blockTag = new CompoundTag();
            AgriNBT.writeBlockPos1(blockTag, pos);
            blockTag.putInt(AgriNBT.INDEX, block.getType().ordinal());
            blockTag.putBoolean(AgriNBT.FLAG, block.isCeiling());
        });
        tag.put(AgriNBT.ENTRIES, blocks);
        return tag;
    }

    public static class GreenHouseBlock {
        private final BlockPos pos;
        private final GreenHouseBlockType type;
        private final boolean ceiling;

        public GreenHouseBlock(BlockPos pos, GreenHouseBlockType type, boolean ceiling) {
            this.pos = pos;
            this.type = type;
            this.ceiling = ceiling;
        }

        public BlockPos getPos() {
            return this.pos;
        }

        public GreenHouseBlockType getType() {
            return this.type;
        }

        public boolean isInterior() {
            return this.getType().isInterior();
        }

        public boolean isExterior() {
            return this.getType().isExterior();
        }

        public boolean isBoundary() {
            return this.getType().isBoundary();
        }

        public boolean isGlass() {
            return this.getType().isGlass();
        }

        public boolean isAir() {
            return this.getType().isAir();
        }

        public boolean isGap() {
            return this.getType().isGap();
        }

        public boolean isCeiling() {
            return this.ceiling;
        }
    }
}
