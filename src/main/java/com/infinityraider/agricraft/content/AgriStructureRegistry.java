package com.infinityraider.agricraft.content;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.content.world.CropStickProcessor;
import com.infinityraider.agricraft.content.world.Mossify10Processor;
import com.infinityraider.infinitylib.utility.registration.ModStructureRegistry;
import com.infinityraider.infinitylib.world.IInfStructure;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

public final class AgriStructureRegistry extends ModStructureRegistry {
    private static final AgriStructureRegistry INSTANCE = new AgriStructureRegistry();

    public static AgriStructureRegistry getInstance() {
        return INSTANCE;
    }

    public final StructureProcessorType<CropStickProcessor> cropStickProcessor;

    public final Holder<StructureProcessorList> pListDesertStandard;
    public final Holder<StructureProcessorList> pListPlainsStandard;
    public final Holder<StructureProcessorList> pListSavannaStandard;
    public final Holder<StructureProcessorList> pListSnowyStandard;
    public final Holder<StructureProcessorList> pListTaigaStandard;

    public final IInfStructure desertStandard;
    public final IInfStructure plainsStandard;
    public final IInfStructure savannaStandard;
    public final IInfStructure snowyStandard;
    public final IInfStructure taigaStandard;

    // TODO: design irrigated greenhouses
    public final IInfStructure IRRIGATED_DESERT = null;//new StructureGreenHouseIrrigated(GreenHouses.Irrigated.DESERT, Pools.DESERT);
    public final IInfStructure IRRIGATED_PLAINS = null;//new StructureGreenHouseIrrigated(GreenHouses.Irrigated.PLAINS, Pools.PLAINS);
    public final IInfStructure IRRIGATED_SAVANNA = null;//new StructureGreenHouseIrrigated(GreenHouses.Irrigated.SAVANNA, Pools.SAVANNA);
    public final IInfStructure IRRIGATED_SNOWY = null;//new StructureGreenHouseIrrigated(GreenHouses.Irrigated.SNOWY, Pools.SNOWY);
    public final IInfStructure IRRIGATED_TAIGA = null;//new StructureGreenHouseIrrigated(GreenHouses.Irrigated.TAIGA, Pools.TAIGA);

    protected AgriStructureRegistry() {
        // processors
        this.cropStickProcessor = this.processor(id("crop_sticks"), () -> CropStickProcessor.CODEC);

        // processor lists
        this.pListDesertStandard = this.processorList(id("greenhouse_standard_desert"), new CropStickProcessor(GreenHouses.Standard.DESERT));
        this.pListPlainsStandard = this.processorList(id("greenhouse_standard_plains"), new CropStickProcessor(GreenHouses.Standard.PLAINS), Mossify10Processor.getInstance());
        this.pListSavannaStandard = this.processorList(id("greenhouse_standard_savanna"), new CropStickProcessor(GreenHouses.Standard.SAVANNA));
        this.pListSnowyStandard = this.processorList(id("greenhouse_standard_snowy"), new CropStickProcessor(GreenHouses.Standard.SNOWY));
        this.pListTaigaStandard = this.processorList(id("greenhouse_standard_taiga"), new CropStickProcessor(GreenHouses.Standard.TAIGA), Mossify10Processor.getInstance());

        // greenhouses
        this.desertStandard = this.structure(
                GreenHouses.Standard.DESERT,
                Pools.DESERT,
                AgriCraft.instance.getConfig().getIrrigatedGreenHouseSpawnWeight(),
                pListDesertStandard,
                StructureTemplatePool.Projection.RIGID
        );

        this.plainsStandard = this.structure(
                GreenHouses.Standard.PLAINS,
                Pools.PLAINS,
                AgriCraft.instance.getConfig().getIrrigatedGreenHouseSpawnWeight(),
                pListPlainsStandard,
                StructureTemplatePool.Projection.RIGID
        );

        this.savannaStandard = this.structure(
                GreenHouses.Standard.SAVANNA,
                Pools.SAVANNA,
                AgriCraft.instance.getConfig().getIrrigatedGreenHouseSpawnWeight(),
                pListSavannaStandard,
                StructureTemplatePool.Projection.RIGID
        );

        this.snowyStandard = this.structure(
                GreenHouses.Standard.SNOWY,
                Pools.SNOWY,
                AgriCraft.instance.getConfig().getIrrigatedGreenHouseSpawnWeight(),
                pListSnowyStandard,
                StructureTemplatePool.Projection.RIGID
        );

        this.taigaStandard = this.structure(
                GreenHouses.Standard.TAIGA,
                Pools.TAIGA,
                AgriCraft.instance.getConfig().getIrrigatedGreenHouseSpawnWeight(),
                pListTaigaStandard,
                StructureTemplatePool.Projection.RIGID
        );
    }

    private static ResourceLocation id(String name) {
        return new ResourceLocation(AgriCraft.instance.getModId(), name);
    }

    public static final class GreenHouses {
        public static final class Standard {
            public static final ResourceLocation DESERT = id("village/desert/greenhouse");
            public static final ResourceLocation PLAINS = id("village/plains/greenhouse");
            public static final ResourceLocation SAVANNA = id("village/savanna/greenhouse");
            public static final ResourceLocation SNOWY = id("village/snowy/greenhouse");
            public static final ResourceLocation TAIGA = id("village/taiga/greenhouse");

            private Standard() {}
        }

        public static final class Irrigated {
            public static final ResourceLocation DESERT = id("village/desert/greenhouse_irrigated");
            public static final ResourceLocation PLAINS =  id("village/plains/greenhouse_irrigated");
            public static final ResourceLocation SAVANNA =  id("village/savanna/greenhouse_irrigated");
            public static final ResourceLocation SNOWY =  id("village/snowy/greenhouse_irrigated");
            public static final ResourceLocation TAIGA =  id("village/taiga/greenhouse_irrigated");

            private Irrigated() {}
        }

        private GreenHouses() {}
    }

    public static final class Pools {
        public static final ResourceLocation DESERT = new ResourceLocation("village/desert/houses");
        public static final ResourceLocation PLAINS = new ResourceLocation("village/plains/houses");
        public static final ResourceLocation SAVANNA = new ResourceLocation("village/savanna/houses");
        public static final ResourceLocation SNOWY = new ResourceLocation("village/snowy/houses");
        public static final ResourceLocation TAIGA = new ResourceLocation("village/taiga/houses");

        private Pools() {}
    }
}
