package com.infinityraider.agricraft.content;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.content.world.CropStickProcessor;
import com.infinityraider.agricraft.content.world.Mossify10Processor;
import com.infinityraider.infinitylib.utility.registration.ModStructureRegistry;
import com.infinityraider.infinitylib.world.IInfStructure;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraftforge.registries.RegistryObject;

public final class AgriStructureRegistry extends ModStructureRegistry {
    private static final AgriStructureRegistry INSTANCE = new AgriStructureRegistry();

    public static AgriStructureRegistry getInstance() {
        return INSTANCE;
    }

    public final RegistryObject<StructureProcessorList> cropStickProcessor;
    public final RegistryObject<StructureProcessorList> cropSticksAndMossify10PercentProcessor;

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
        // super constructor
        super(AgriCraft.instance);

        // processors
        this.cropStickProcessor = this.processor("crop_sticks", CropStickProcessor.getInstance());
        this.cropSticksAndMossify10PercentProcessor = this.processor("crop_sticks_and_mossify", CropStickProcessor.getInstance(), Mossify10Processor.getInstance());

        // greenhouses
        this.desertStandard = this.structure(
                GreenHouses.Standard.DESERT,
                Pools.DESERT,
                AgriCraft.instance.getConfig().getIrrigatedGreenHouseSpawnWeight(),
                cropStickProcessor,
                StructureTemplatePool.Projection.RIGID
        );

        this.plainsStandard = this.structure(
                GreenHouses.Standard.PLAINS,
                Pools.PLAINS,
                AgriCraft.instance.getConfig().getIrrigatedGreenHouseSpawnWeight(),
                cropSticksAndMossify10PercentProcessor,
                StructureTemplatePool.Projection.RIGID
        );

        this.savannaStandard = this.structure(
                GreenHouses.Standard.SAVANNA,
                Pools.SAVANNA,
                AgriCraft.instance.getConfig().getIrrigatedGreenHouseSpawnWeight(),
                cropStickProcessor,
                StructureTemplatePool.Projection.RIGID
        );

        this.snowyStandard = this.structure(
                GreenHouses.Standard.SNOWY,
                Pools.SNOWY,
                AgriCraft.instance.getConfig().getIrrigatedGreenHouseSpawnWeight(),
                cropStickProcessor,
                StructureTemplatePool.Projection.RIGID
        );

        this.taigaStandard = this.structure(
                GreenHouses.Standard.TAIGA,
                Pools.TAIGA,
                AgriCraft.instance.getConfig().getIrrigatedGreenHouseSpawnWeight(),
                cropSticksAndMossify10PercentProcessor,
                StructureTemplatePool.Projection.RIGID
        );
    }

    public static final class GreenHouses {
        public static final class Standard {
            public static final String DESERT = "village/desert/greenhouse";
            public static final String PLAINS = "village/plains/greenhouse";
            public static final String SAVANNA = "village/savanna/greenhouse";
            public static final String SNOWY = "village/snowy/greenhouse";
            public static final String TAIGA = "village/taiga/greenhouse";

            private Standard() {}
        }

        public static final class Irrigated {
            public static final String DESERT = "village/desert/greenhouse_irrigated";
            public static final String PLAINS =  "village/plains/greenhouse_irrigated";
            public static final String SAVANNA =  "village/savanna/greenhouse_irrigated";
            public static final String SNOWY =  "village/snowy/greenhouse_irrigated";
            public static final String TAIGA =  "village/taiga/greenhouse_irrigated";

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
