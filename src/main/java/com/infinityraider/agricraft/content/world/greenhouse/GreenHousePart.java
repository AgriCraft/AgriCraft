package com.infinityraider.agricraft.content.world.greenhouse;

import com.google.common.collect.Maps;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.capability.CapabilityGreenHouseParts;
import com.infinityraider.agricraft.handler.GreenHouseHandler;
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
    private final ChunkPos chunk;
    private final Map<BlockPos, GreenHouseBlock> blocks;

    protected GreenHousePart(int id, LevelChunk chunk, Map<BlockPos, GreenHouseBlock> blocks) {
        this.id = id;
        this.chunk = chunk.getPos();
        this.blocks =blocks;
        CapabilityGreenHouseParts.addPart(chunk, this);
    }

    public GreenHousePart(ChunkPos chunk, CompoundTag tag) {
        this.id = tag.getInt(AgriNBT.KEY);
        this.chunk = chunk;
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

    public ChunkPos getChunk() {
        return this.chunk;
    }

    public GreenHouseBlockType getType(BlockPos pos) {
        GreenHouseBlock block = this.getBlock(pos);
        return block == null ? GreenHouseBlockType.EXTERIOR : block.getType();
    }

    public void onBlockUpdated(Level world, BlockPos pos, GreenHouse greenHouse) {
        // WARNING: spaghetti code
        if(world.isClientSide()) {
            return;
        }
        GreenHouseBlock previous = this.getBlock(pos);
        if(previous == null) {
            // shouldn't ever happen
            AgriCraft.instance.getLogger().error("Caught a block update for a greenhouse at a null position");
            return;
        }
        BlockState newState = world.getBlockState(pos);
        if(newState.isAir()) {
            if(previous.isInterior()) {
                // an interior block has been set to air
                if(!previous.isAir()) {
                    this.blocks.put(pos, new GreenHouseBlock(pos, GreenHouseBlockType.INTERIOR_AIR, false));
                }
            } else if(previous.isBoundary()) {
                // boundary has been set to air
                greenHouse.remove(world);   // TODO: handle gaps
            }
        } else {
            if(previous.isInterior()) {
                // an interior block has been set to something which is not air
                if(previous.isAir()) {
                    this.blocks.put(pos, new GreenHouseBlock(pos, GreenHouseBlockType.INTERIOR_OTHER, false));
                }
            } else if(previous.isBoundary()) {
                if(previous.isGlass()) {
                    // glass block has been changed to non-glass
                    if(!GreenHouseHandler.isGreenHouseGlass(newState)) {
                        if(this.checkSolidness(world, pos, newState)) {
                            // glass has been changed to non-glass, but solid
                            this.blocks.put(pos, new GreenHouseBlock(pos, GreenHouseBlockType.BOUNDARY, previous.isCeiling()));
                            if(previous.isCeiling()) {
                                greenHouse.decrementCeilingGlassCount();
                            }
                        } else {
                            // boundary has been changed to something non-solid
                            greenHouse.remove(world);   // TODO: handle gaps
                        }
                    }
                } else {
                    if(GreenHouseHandler.isGreenHouseGlass(newState)) {
                        // non glass block has been changed to glass
                        this.blocks.put(pos, new GreenHouseBlock(pos, GreenHouseBlockType.GLASS, previous.isCeiling()));
                        if(previous.isCeiling()) {
                            greenHouse.incrementCeilingGlassCount();
                        }
                    } else {
                        if(!this.checkSolidness(world, pos, newState)) {
                            // non-glass has been changed to something else which is non-solid
                            greenHouse.remove(world);   // TODO: handle gaps
                        }
                    }
                }
            }
        }
    }

    private boolean checkSolidness(Level world, BlockPos pos, BlockState state) {
        return Arrays.stream(Direction.values()).allMatch(dir -> {
            GreenHouseBlock block = this.getBlock(pos.relative(dir));
            if(block == null) {
                return true;
            }
            if(block.isBoundary() || block.isExterior()) {
                return true;
            }
            if(block.isInterior()) {
                return GreenHouseHandler.isSolidBlock(world, state, pos, dir);
            }
            return false;
        });
    }

    @Nullable
    public GreenHouseBlock getBlock(BlockPos pos) {
        return this.blocks.getOrDefault(pos, null);
    }

    protected void replaceAirBlocks(Level world, GreenHouseState state) {
        BlockState air = state.isComplete()
                ? AgriApi.getAgriContent().getBlocks().getGreenHouseAirBlock().defaultBlockState()
                : Blocks.AIR.defaultBlockState();
        this.blocks.values().stream()
                .filter(block -> block.getType().isAir())
                .map(GreenHouseBlock::getPos)
                .forEach(pos -> world.setBlock(pos,air, 3));
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

        public boolean isCeiling() {
            return this.ceiling;
        }
    }
}
