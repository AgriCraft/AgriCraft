package com.infinityraider.agricraft.content;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.content.IAgriContent;
import com.infinityraider.agricraft.content.core.*;
import com.infinityraider.agricraft.content.decoration.ItemGrate;
import com.infinityraider.agricraft.content.irrigation.*;
import com.infinityraider.agricraft.content.tools.*;
import com.infinityraider.agricraft.reference.Names;
import net.minecraft.item.Item;

import javax.annotation.Nullable;

public class AgriItemRegistry implements IAgriContent.Items {

    private static final AgriItemRegistry INSTANCE = new AgriItemRegistry();

    public static AgriItemRegistry getInstance() {
        return INSTANCE;
    }

    public final ItemDebugger debugger;

    public final ItemCropSticks crop_sticks_wood;
    public final ItemCropSticks crop_sticks_iron;
    public final ItemCropSticks crop_sticks_obsidian;

    public final ItemSeedAnalyzer seed_analyzer;
    public final ItemJournal journal;

    public final ItemDynamicAgriSeed seed;

    public final ItemIrrigationTank tank;
    public final ItemIrrigationChannel channel;
    public final ItemIrrigationChannelHollow channel_hollow;
    public final ItemSprinkler sprinkler;
    public final ItemChannelValve valve;

    public final ItemClipper clipper;
    public final ItemMagnifyingGlass magnifying_glass;
    public final ItemRake rake_wood;
    public final ItemRake rake_iron;
    public final ItemTrowel trowel;
    public final ItemSeedBag seed_bag;

    public final ItemAgriNugget nugget_coal;
    public final ItemAgriNugget nugget_diamond;
    public final ItemAgriNugget nugget_emerald;
    public final ItemAgriNugget nugget_quartz;

    public final ItemGrate grate;

    @SuppressWarnings("deprecation")
    private AgriItemRegistry() {
        this.debugger = new ItemDebugger();

        this.crop_sticks_wood = new ItemCropSticks(CropStickVariant.WOOD);
        this.crop_sticks_iron = new ItemCropSticks(CropStickVariant.IRON);
        this.crop_sticks_obsidian = new ItemCropSticks(CropStickVariant.OBSIDIAN);

        this.seed_analyzer = new ItemSeedAnalyzer();
        this.journal = new ItemJournal();

        this.seed = new ItemDynamicAgriSeed();

        this.tank = new ItemIrrigationTank();
        this.channel = new ItemIrrigationChannel();
        this.channel_hollow = new ItemIrrigationChannelHollow();
        this.sprinkler = new ItemSprinkler();
        this.valve = new ItemChannelValve();

        this.clipper = new ItemClipper();
        this.magnifying_glass = new ItemMagnifyingGlass();
        this.rake_wood = new ItemRake(ItemRake.WOOD_LOGIC);
        this.rake_iron = new ItemRake(ItemRake.IRON_LOGIC);
        this.trowel = new ItemTrowel();
        this.seed_bag = new ItemSeedBag();

        this.nugget_coal = AgriCraft.instance.getConfig().enableCoalNugget()
                ? new ItemAgriNugget.Burnable(Names.Nuggets.COAL)
                : null;
        this.nugget_diamond = AgriCraft.instance.getConfig().enableDiamondNugget()
                ? new ItemAgriNugget(Names.Nuggets.DIAMOND)
                : null;
        this.nugget_emerald = AgriCraft.instance.getConfig().enableEmeraldNugget()
                ? new ItemAgriNugget(Names.Nuggets.EMERALD)
                : null;
        this.nugget_quartz = AgriCraft.instance.getConfig().enableQuartzNugget()
                ? new ItemAgriNugget(Names.Nuggets.QUARTZ)
                : null;

        this.grate = new ItemGrate();
    }

    @Override
    public Item getDebuggerItem() {
        return this.debugger;
    }

    @Override
    public Item getWoodCropSticksItem() {
        return this.crop_sticks_wood;
    }

    @Override
    public Item getIronCropSticksItem() {
        return this.crop_sticks_iron;
    }

    @Override
    public Item getObsidianCropSticksItem() {
        return this.crop_sticks_obsidian;
    }

    @Override
    public Item getSeedAnalyzerItem() {
        return this.seed_analyzer;
    }

    @Override
    public Item getJournalItem() {
        return this.journal;
    }

    @Override
    public Item getSeedItem() {
        return this.seed;
    }

    @Override
    public Item getIrrigationTankItem() {
        return this.tank;
    }

    @Override
    public Item getIrrigationChannelItem() {
        return this.channel;
    }

    @Override
    public Item getHollowIrrigationChannelItem() {
        return this.channel_hollow;
    }

    @Override
    public Item getSprinklerItem() {
        return this.sprinkler;
    }

    @Override
    public Item getValveItem() {
        return this.valve;
    }

    @Override
    public Item getClipperItem() {
        return this.clipper;
    }

    @Override
    public Item getMagnifyingGlassItem() {
        return this.magnifying_glass;
    }

    @Override
    public Item getWoodenRakeItem() {
        return this.rake_wood;
    }

    @Override
    public Item getIronRakeItem() {
        return this.rake_iron;
    }

    @Override
    public Item getTrowelItem() {
        return this.trowel;
    }

    @Override
    public Item getSeedBagItem() {
        return this.seed_bag;
    }

    @Override
    public Item getGrateItem() {
        return this.grate;
    }

    @Nullable
    @Override
    public Item getCoalNuggetItem() {
        return this.nugget_coal;
    }

    @Nullable
    @Override
    public Item getDiamondNuggetItem() {
        return this.nugget_diamond;
    }

    @Nullable
    @Override
    public Item getEmeraldNuggetItem() {
        return this.nugget_emerald;
    }

    @Nullable
    @Override
    public Item getQuartzNuggetItem() {
        return this.nugget_quartz;
    }
}
