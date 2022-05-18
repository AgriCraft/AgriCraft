package com.infinityraider.agricraft.reference;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.ChunkPos;

import java.util.stream.Stream;

public interface AgriNBT {
    String CAPACITY = "agri_capacity";
    String CHUNK = "agri_chunk";
    String CONNECTIONS = "agri_contents";
    String CONTENTS = "agri_connections";
    String DIRECTION = "agri_dir";
    String DOMINANT = "agri_dominant";
    String ENTRIES = "agri_entries";
    String FLAG = "agri_flag";
    String FREE = "agri_free";
    String GENE = "agri_gene";
    String GENOME = "agri_genome";
    String GROWTH = "agri_growth";
    String INDEX = "agri_index";
    String KEY = "agri_key";
    String LEVEL = "agri_level";
    String LAYERS = "agri_layers";
    String NETWORK = "agri_network";
    String PLANT = "agri_plant";
    String RECESSIVE = "agri_recessive";
    String REMOVED = "agri_removed";
    String STATE = "agri_state";
    String WEED = "agri_weed";
    String WEED_GROWTH = "agri_weed_growth";

    String X1 = "agri_x1";
    String Y1 = "agri_y1";
    String Z1 = "agri_z1";
    String X2 = "agri_x2";
    String Y2 = "agri_y2";
    String Z2 = "agri_z2";

    String U1 = "agri_u1";
    String V1 = "agri_v1";

    static void writeBlockPos1(CompoundTag tag, BlockPos pos) {
        tag.putInt(X1, pos.getX());
        tag.putInt(Y1, pos.getY());
        tag.putInt(Z1, pos.getZ());
    }

    static void writeBlockPos2(CompoundTag tag, BlockPos pos) {
        tag.putInt(X2, pos.getX());
        tag.putInt(Y2, pos.getY());
        tag.putInt(Z2, pos.getZ());
    }

    static void writeChunkPos1(CompoundTag tag, ChunkPos pos) {
        tag.putInt(X1, pos.x);
        tag.putInt(Z1, pos.z);
    }

    static BlockPos readBlockPos1(CompoundTag tag) {
        return new BlockPos(tag.getInt(X1), tag.getInt(Y1), tag.getInt(Z1));
    }

    static BlockPos readBlockPos2(CompoundTag tag) {
        return new BlockPos(tag.getInt(X2), tag.getInt(Y2), tag.getInt(Z2));
    }

    static ChunkPos readChunkPos1(CompoundTag tag) {
        return new ChunkPos(tag.getInt(X1), tag.getInt(Z1));
    }

    static Stream<CompoundTag> stream(CompoundTag tag, String key) {
        if(tag.contains(key, Tag.TAG_COMPOUND)) {
            return stream(tag.getList(key, Tag.TAG_COMPOUND));
        }
        return Stream.empty();
    }

    static Stream<CompoundTag> stream(ListTag list) {
        return list.stream().filter(tag -> tag instanceof CompoundTag).map(tag -> (CompoundTag) tag);
    }
}
