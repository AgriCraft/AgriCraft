package com.infinityraider.agricraft.content.world;

import com.google.common.collect.Maps;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.reference.AgriNBT;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Map;

public class GreenHousePartHolder {
    private final ChunkPos chunk;
    private Part part;

    public GreenHousePartHolder(ChunkPos chunk) {
        this.chunk = chunk;
        this.part = Part.UNLOADED;
    }

    protected GreenHousePartHolder(ChunkPos chunk, Map<BlockPos, Block> blocks) {
        this.chunk = chunk;
        this.part = new Loaded(blocks);
    }

    public ChunkPos getChunk() {
        return this.chunk;
    }

    public boolean isLoaded() {
        return this.part.isLoaded();
    }

    public void unload() {
        this.part = this.part.unload();
    }

    public GreenHouse.BlockType getType(BlockPos pos) {
        return this.part.getType(pos);
    }

    @Nullable
    public Block getBlock(BlockPos pos) {
        return this.part.getBlock(pos);
    }

    protected void replaceAirBlocks(Level world) {
        this.part.replaceAirBlocks(world);
    }

    public CompoundTag writeToTag() {
        CompoundTag tag = new CompoundTag();
        this.part.writeToNBT(tag);
        return tag;
    }

    public void readFromTag(CompoundTag tag) {
        this.part = this.part.load(tag);
    }

    protected static class Block {
        private final BlockPos pos;
        private final GreenHouse.BlockType type;
        private final boolean ceiling;

        protected Block(BlockPos pos, GreenHouse.BlockType type, boolean ceiling) {
            this.pos = pos;
            this.type = type;
            this.ceiling = ceiling;
        }

        public BlockPos getPos() {
            return this.pos;
        }

        public GreenHouse.BlockType getType() {
            return this.type;
        }

        public boolean isCeiling() {
            return this.ceiling;
        }
    }

    private static class Part {
        private static final Part UNLOADED = new Part();

        private Part() {}

        public GreenHouse.BlockType getType(BlockPos pos) {
            return GreenHouse.BlockType.EXTERIOR;
        }

        @Nullable
        public Block getBlock(BlockPos pos) {
            return null;
        }

        public void replaceAirBlocks(Level world) {
            //Nope
        }

        public void writeToNBT(CompoundTag tag) {
            // nothing to write
            AgriCraft.instance.getLogger().error("ATTEMPTED TO WRITE UNLOADED GREENHOUSE PART TO NBT");
        }

        protected final boolean isLoaded() {
            return this != UNLOADED;
        }

        protected final Part load(CompoundTag tag) {
            Map<BlockPos, Block> blocks = Maps.newHashMap();
            tag.getList(AgriNBT.ENTRIES, Tag.TAG_COMPOUND).stream()
                    .filter(blockTag -> blockTag instanceof CompoundTag)
                    .map(blockTag -> (CompoundTag) blockTag)
                    .forEach(blockTag -> {
                        BlockPos pos = new BlockPos(blockTag.getInt(AgriNBT.X1), blockTag.getInt(AgriNBT.Y1), blockTag.getInt(AgriNBT.Z1));
                        GreenHouse.BlockType type = GreenHouse.BlockType.values()[blockTag.getInt(AgriNBT.INDEX)];
                        boolean ceiling = blockTag.getBoolean(AgriNBT.FLAG);
                        blocks.put(pos, new Block(pos, type, ceiling));
                    });
            return new Loaded(blocks);
        }

        protected final Part unload() {
            return UNLOADED;
        }
    }

    private static class Loaded extends Part {
        private final Map<BlockPos, Block> blocks;

        private Loaded(Map<BlockPos, Block> blocks) {
            this.blocks = blocks;
        }

        @Override
        public GreenHouse.BlockType getType(BlockPos pos) {
            Block block = this.getBlock(pos);
            return block == null ? GreenHouse.BlockType.EXTERIOR : block.getType();
        }

        @Nullable
        @Override
        public Block getBlock(BlockPos pos) {
            return this.blocks.getOrDefault(pos, null);
        }

        @Override
        public void replaceAirBlocks(Level world) {
            BlockState air = AgriApi.getAgriContent().getBlocks().getGreenHouseAirBlock().defaultBlockState();
            this.blocks.values().stream()
                    .filter(block -> block.getType().isAir())
                    .map(Block::getPos)
                    .forEach(pos -> world.setBlock(pos,air, 3));
        }

        @Override
        public void writeToNBT(CompoundTag tag) {
            ListTag blocks = new ListTag();
            this.blocks.forEach((pos, block) -> {
                CompoundTag blockTag = new CompoundTag();
                blockTag.putInt(AgriNBT.X1, pos.getX());
                blockTag.putInt(AgriNBT.Y1, pos.getY());
                blockTag.putInt(AgriNBT.Z1, pos.getZ());
                blockTag.putInt(AgriNBT.INDEX, block.getType().ordinal());
                blockTag.putBoolean(AgriNBT.FLAG, block.isCeiling());
            });
            tag.put(AgriNBT.ENTRIES, blocks);
        }
    }
}
