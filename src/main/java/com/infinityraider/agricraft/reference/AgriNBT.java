package com.infinityraider.agricraft.reference;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

import java.util.stream.Stream;

public interface AgriNBT {
    String CAPACITY = "agri_capacity";
    String CHUNK = "agri_chunk";
    String CONNECTIONS = "agri_connections";
    String DIRECTION = "agri_dir";
    String DOMINANT = "agri_dominant";
    String ENTRIES = "agri_entries";
    String FLAG = "agri_flag";
    String GENE = "agri_gene";
    String GENOME = "agri_genome";
    String GROWTH = "agri_growth";
    String KEY = "agri_key";
    String LEVEL = "agri_level";
    String LAYERS = "agri_layers";
    String NETWORK = "agri_network";
    String PLANT = "agri_plant";
    String RECESSIVE = "agri_recessive";
    String WEED = "agri_weed";
    String WEED_GROWTH = "agri_weed_growth";

    String UP = "agri_up";
    String DOWN = "agri_down";

    String X1 = "agri_x1";
    String Y1 = "agri_y1";
    String Z1 = "agri_z1";
    String X2 = "agri_x2";
    String Y2 = "agri_y2";
    String Z2 = "agri_z2";

    String U1 = "agri_u1";
    String V1 = "agri_v1";

    static Stream<CompoundNBT> stream(ListNBT list) {
        return list.stream().filter(tag -> tag instanceof CompoundNBT).map(tag -> (CompoundNBT) tag);
    }
}
