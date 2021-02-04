package com.infinityraider.agricraft.content;

import com.infinityraider.agricraft.content.core.BlockCropSticks;
import com.infinityraider.agricraft.content.core.BlockSeedAnalyzer;
import com.infinityraider.agricraft.content.core.CropStickVariant;
import com.infinityraider.infinitylib.block.BlockBase;

public class AgriBlockRegistry {

    private static final AgriBlockRegistry INSTANCE = new AgriBlockRegistry();

    public static AgriBlockRegistry getInstance() {
        return INSTANCE;
    }

    // crop sticks
    public final BlockBase crop_sticks_wood;
    public final BlockBase crop_sticks_iron;
    public final BlockBase crop_sticks_obsidian;

    // analyzer
    public final BlockBase seed_analyzer;

    // irrigation
    /*
    public final BlockBase tank;
    public final BlockBase channel;
    public final BlockBase channel_valve;
    public final BlockBase channel_hollow;
    public final BlockBase channel_hollow_valve;
    public final BlockBase sprinkler;
    */

    // storage
    /*
    public final BlockBase seed_storage;
    */

    // Decoration
    /*
    public final BlockBase grate;
    */

    private AgriBlockRegistry() {
        this.crop_sticks_wood = new BlockCropSticks(CropStickVariant.WOOD);
        this.crop_sticks_iron = new BlockCropSticks(CropStickVariant.IRON);
        this.crop_sticks_obsidian = new BlockCropSticks(CropStickVariant.OBSIDIAN);

        this.seed_analyzer = new BlockSeedAnalyzer();
        /*

        this.tank = new BlockWaterTank();
        this.channel = new BlockWaterChannel();
        this.channel_valve = new BlockWaterChannelValve();
        this.channel_hollow = new BlockWaterChannelHollow();
        this.channel_hollow_valve = new BlockWaterChannelHollowValve();
        this.sprinkler = new BlockSprinkler();

        this.seed_storage = new BlockSeedStorage();
        this.grate = new BlockGrate();
        */
    }

}
