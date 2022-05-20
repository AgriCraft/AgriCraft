package com.infinityraider.agricraft.content.world.greenhouse;

import com.google.common.collect.Maps;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.capability.CapabilityGreenHouseParts;
import com.infinityraider.agricraft.reference.AgriNBT;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
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
    private GreenHouseState state;

    protected GreenHousePart(int id, LevelChunk chunk, Map<BlockPos, GreenHouseBlock> blocks, GreenHouseState state) {
        this.id = id;
        this.blocks =blocks;
        CapabilityGreenHouseParts.addPart(chunk, this);
        this.state = state;
    }

    public GreenHousePart(CompoundTag tag) {
        this.id = tag.getInt(AgriNBT.KEY);
        this.blocks = Maps.newHashMap();
        AgriNBT.stream(tag, AgriNBT.ENTRIES).forEach(blockTag -> {
                    BlockPos pos = AgriNBT.readBlockPos1(blockTag);
                    GreenHouseBlock.Type type = GreenHouseBlock.Type.values()[blockTag.getInt(AgriNBT.INDEX)];
                    boolean ceiling = blockTag.getBoolean(AgriNBT.FLAG);
                    this.blocks.put(pos, new GreenHouseBlock(pos, type, ceiling));
                });
        this.state = tag.contains(AgriNBT.STATE) ? GreenHouseState.values()[tag.getInt(AgriNBT.STATE)] : GreenHouseState.COMPLETE;
    }

    public int getId() {
        return this.id;
    }

    public GreenHouseState getState() {
        return this.state;
    }

    protected void updateState(Level world, GreenHouseState state) {
        if(state != this.getState()) {
            this.state = state;
            this.replaceAirBlocks(world);
        }
    }

    public GreenHouseBlock.Type getType(BlockPos pos) {
        GreenHouseBlock block = this.getBlock(pos);
        return block == null ? GreenHouseBlock.Type.EXTERIOR : block.getType();
    }

    public boolean onBlockUpdated(Level world, BlockPos pos, GreenHouse greenHouse) {
        if(world.isClientSide()) {
            return false;
        }
        GreenHouseBlock oldBlock = this.getBlock(pos);
        if(oldBlock == null) {
            // shouldn't ever happen
            AgriCraft.instance.getLogger().error("Caught a block update for a greenhouse at a null position");
            return false;
        }
        // fetch new state and type
        BlockState newState = world.getBlockState(pos);
        GreenHouseBlock.Type newType = oldBlock.getType().getNewTypeForState(world, pos, newState, this.blocks::get);
        // no changes: return
        if(newType == oldBlock.getType()) {
            return false;
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
                greenHouse.newGap(world);
                // also check for ceiling updates (do this after gap as gap state is prioritized over glass fraction
                if(oldBlock.isCeiling() && oldBlock.isGlass()) {
                    greenHouse.decrementCeilingGlassCount(world);
                }
            }
            return true;
        }
        // check if an old gap has been closed
        if(oldBlock.isGap()) {
            if(newType.isGlass() && oldBlock.isCeiling()) {
                // first update the ceiling glass count (do this before gap as gap state is prioritized over glass fraction)
                greenHouse.incrementCeilingGlassCount(world);
            }
            // notify the greenhouse of a closed gap
            greenHouse.removeGap(world);
            return true;
        }
        // no gap has been closed nor opened; check for ceiling updates
        if(oldBlock.isCeiling()) {
            if(newType.isGlass()) {
                greenHouse.incrementCeilingGlassCount(world);
            } else {
                greenHouse.decrementCeilingGlassCount(world);
            }
            return true;
        }
        // check if an air block has been placed
        if(newType.isAir()) {
            world.setBlock(pos, this.getAirState(), 3);
        }
        return true;
    }

    @Nullable
    public GreenHouseBlock getBlock(BlockPos pos) {
        return this.blocks.getOrDefault(pos, null);
    }

    protected void onRemoved(Level world) {
        this.replaceAirBlocks(world, Blocks.AIR.defaultBlockState());
    }

    protected void replaceAirBlocks(Level world) {
        this.replaceAirBlocks(world, this.getAirState());
    }

    protected BlockState getAirState() {
        if(this.getState().isRemoved()) {
            return Blocks.AIR.defaultBlockState();
        }
        return BlockGreenHouseAir.withState(this.getState());
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
            blocks.add(blockTag);
        });
        tag.put(AgriNBT.ENTRIES, blocks);
        return tag;
    }

}
