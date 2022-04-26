package com.infinityraider.agricraft.content;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.content.IAgriContent;
import com.infinityraider.agricraft.content.core.*;
import com.infinityraider.agricraft.content.decoration.*;
import com.infinityraider.agricraft.content.irrigation.*;
import com.infinityraider.agricraft.content.tools.*;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.utility.registration.ModContentRegistry;
import com.infinityraider.infinitylib.utility.registration.RegistryInitializer;

import javax.annotation.Nullable;

public final class AgriItemRegistry extends ModContentRegistry implements IAgriContent.Items {
    private static final AgriItemRegistry INSTANCE = new AgriItemRegistry();

    public static AgriItemRegistry getInstance() {
        return INSTANCE;
    }

    public final RegistryInitializer<ItemDebugger> debugger;

    public final RegistryInitializer<ItemSeedAnalyzer> seed_analyzer;
    public final RegistryInitializer<ItemJournal> journal;

    public final RegistryInitializer<ItemDynamicAgriSeed> seed;

    public final RegistryInitializer<ItemIrrigationTank> tank;
    public final RegistryInitializer<ItemIrrigationChannel> channel;
    public final RegistryInitializer<ItemIrrigationChannelHollow> channel_hollow;
    public final RegistryInitializer<ItemSprinkler> sprinkler;
    public final RegistryInitializer<ItemChannelValve> valve;

    public final RegistryInitializer<ItemClipper> clipper;
    public final RegistryInitializer<ItemMagnifyingGlass> magnifying_glass;
    public final RegistryInitializer<ItemRake> rake_wood;
    public final RegistryInitializer<ItemRake> rake_iron;
    public final RegistryInitializer<ItemTrowel> trowel;
    public final RegistryInitializer<ItemSeedBag> seed_bag;

    public final RegistryInitializer<ItemAgriNugget> nugget_copper;
    public final RegistryInitializer<ItemAgriNugget> nugget_coal;
    public final RegistryInitializer<ItemAgriNugget> nugget_diamond ;
    public final RegistryInitializer<ItemAgriNugget> nugget_emerald ;
    public final RegistryInitializer<ItemAgriNugget> nugget_quartz;
    public final RegistryInitializer<ItemAgriNugget> netherite_sliver;
    public final RegistryInitializer<ItemAgriNugget> amathyllis_petal;

    public final RegistryInitializer<ItemGrate> grate;

    private AgriItemRegistry() {
        super();

        this.debugger = this.item(ItemDebugger::new);

        this.seed_analyzer = this.item(ItemSeedAnalyzer::new);
        this.journal = this.item(ItemJournal::new);

        this.seed = this.item(ItemDynamicAgriSeed::new);

        this.tank = this.item(ItemIrrigationTank::new);
        this.channel = this.item(ItemIrrigationChannel::new);
        this.channel_hollow = this.item(ItemIrrigationChannelHollow::new);
        this.sprinkler = this.item(ItemSprinkler::new);
        this.valve = this.item(ItemChannelValve::new);

        this.clipper = this.item(ItemClipper::new);
        this.magnifying_glass = this.item(ItemMagnifyingGlass::new);
        this.rake_wood = this.item(() -> new ItemRake(ItemRake.WOOD_LOGIC));
        this.rake_iron = this.item(() -> new ItemRake(ItemRake.IRON_LOGIC));
        this.trowel = this.item(ItemTrowel::new);
        this.seed_bag = this.item(ItemSeedBag::new);

        this.nugget_copper = AgriCraft.instance.getConfig().enableCopperNugget() ? this.item(() -> new ItemAgriNugget(Names.Items.NUGGET + "_" + Names.Nuggets.COPPER)) : null;
        this.nugget_coal = AgriCraft.instance.getConfig().enableCoalNugget() ? this.item(() -> new ItemAgriNugget.Burnable(Names.Items.NUGGET + "_" + Names.Nuggets.COAL)) : null;
        this.nugget_diamond = AgriCraft.instance.getConfig().enableDiamondNugget() ? this.item(() -> new ItemAgriNugget(Names.Items.NUGGET + "_" + Names.Nuggets.DIAMOND)) : null;
        this.nugget_emerald = AgriCraft.instance.getConfig().enableEmeraldNugget() ? this.item(() -> new ItemAgriNugget(Names.Items.NUGGET + "_" + Names.Nuggets.EMERALD)) : null;
        this.nugget_quartz = AgriCraft.instance.getConfig().enableQuartzNugget() ? this.item(() -> new ItemAgriNugget(Names.Items.NUGGET + "_" + Names.Nuggets.QUARTZ)) : null;
        this.netherite_sliver = AgriCraft.instance.getConfig().enableNetheriteSliver() ? this.item(() -> new ItemAgriNugget(Names.Nuggets.NETHERITE_SLIVER)) : null;
        this.amathyllis_petal = AgriCraft.instance.getConfig().enableAmathyllisPetal() ? this.item(() -> new ItemAgriNugget(Names.Nuggets.AMATHYLLIS_PETAL)) : null;

        this.grate = this.item(ItemGrate::new);

        CropStickVariant.initItems(this::item);
    }

    @Override
    public ItemDebugger getDebuggerItem() {
        return debugger.get();
    }

    @Override
    public ItemCropSticks getWoodCropSticksItem() {
        return CropStickVariant.WOOD.getItem();
    }

    @Override
    public ItemCropSticks getIronCropSticksItem() {
        return CropStickVariant.IRON.getItem();
    }

    @Override
    public ItemCropSticks getObsidianCropSticksItem() {
        return CropStickVariant.OBSIDIAN.getItem();
    }

    @Override
    public ItemSeedAnalyzer getSeedAnalyzerItem() {
        return seed_analyzer.get();
    }

    @Override
    public ItemJournal getJournalItem() {
        return journal.get();
    }

    @Override
    public ItemDynamicAgriSeed getSeedItem() {
        return seed.get();
    }

    @Override
    public ItemIrrigationTank getIrrigationTankItem() {
        return tank.get();
    }

    @Override
    public ItemIrrigationChannel getIrrigationChannelItem() {
        return channel.get();
    }

    @Override
    public ItemIrrigationChannelHollow getHollowIrrigationChannelItem() {
        return channel_hollow.get();
    }

    @Override
    public ItemSprinkler getSprinklerItem() {
        return sprinkler.get();
    }

    @Override
    public ItemChannelValve getValveItem() {
        return valve.get();
    }

    @Override
    public ItemClipper getClipperItem() {
        return clipper.get();
    }

    @Override
    public ItemMagnifyingGlass getMagnifyingGlassItem() {
        return magnifying_glass.get();
    }

    @Override
    public ItemRake getWoodenRakeItem() {
        return rake_wood.get();
    }

    @Override
    public ItemRake getIronRakeItem() {
        return rake_iron.get();
    }

    @Override
    public ItemTrowel getTrowelItem() {
        return trowel.get();
    }

    @Override
    public ItemSeedBag getSeedBagItem() {
        return seed_bag.get();
    }

    @Override
    public ItemGrate getGrateItem() {
        return grate.get();
    }

    @Nullable
    @Override
    public ItemAgriNugget getCopperNuggetItem() {
        return nugget_copper == null ? null : this.nugget_copper.get();
    }

    @Nullable
    @Override
    public ItemAgriNugget getCoalNuggetItem() {
        return nugget_coal == null ? null : this.nugget_coal.get();
    }

    @Nullable
    @Override
    public ItemAgriNugget getDiamondNuggetItem() {
        return nugget_diamond == null ? null : this.nugget_diamond.get();
    }

    @Nullable
    @Override
    public ItemAgriNugget getEmeraldNuggetItem() {
        return nugget_emerald == null ? null : this.nugget_emerald.get();
    }

    @Nullable
    @Override
    public ItemAgriNugget getQuartzNuggetItem() {
        return nugget_quartz == null ? null : this.nugget_quartz.get();
    }

    @Nullable
    @Override
    public ItemAgriNugget getNetheriteSliverItem() {
        return netherite_sliver == null ? null : this.netherite_sliver.get();
    }

    @Nullable
    @Override
    public ItemAgriNugget getAmathyllisPetalItem() {
        return amathyllis_petal == null ? null : this.amathyllis_petal.get();
    }
}
