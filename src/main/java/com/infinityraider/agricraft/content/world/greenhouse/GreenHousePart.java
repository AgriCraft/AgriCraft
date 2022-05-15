package com.infinityraider.agricraft.content.world.greenhouse;

import com.google.common.collect.Maps;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.capability.CapabilityGreenHouseParts;
import com.infinityraider.agricraft.reference.AgriNBT;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

import javax.annotation.Nullable;
import java.util.Map;

public class GreenHousePart {
    private final int id;
    private final ChunkPos chunk;
    private final Map<BlockPos, Block> blocks;

    protected GreenHousePart(int id, LevelChunk chunk, Map<BlockPos, Block> blocks) {
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
                    this.blocks.put(pos, new Block(pos, type, ceiling));
                });
    }

    public int getId() {
        return this.id;
    }

    public ChunkPos getChunk() {
        return this.chunk;
    }

    public GreenHouseBlockType getType(BlockPos pos) {
        Block block = this.getBlock(pos);
        return block == null ? GreenHouseBlockType.EXTERIOR : block.getType();
    }

    @Nullable
    public Block getBlock(BlockPos pos) {
        return this.blocks.getOrDefault(pos, null);
    }

    protected void replaceAirBlocks(Level world, GreenHouseState state) {
        BlockState air = state.isComplete()
                ? AgriApi.getAgriContent().getBlocks().getGreenHouseAirBlock().defaultBlockState()
                : Blocks.AIR.defaultBlockState();
        this.blocks.values().stream()
                .filter(block -> block.getType().isAir())
                .map(Block::getPos)
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

    public static class Block {
        private final BlockPos pos;
        private final GreenHouseBlockType type;
        private final boolean ceiling;

        public Block(BlockPos pos, GreenHouseBlockType type, boolean ceiling) {
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

        public boolean isCeiling() {
            return this.ceiling;
        }
    }
}
