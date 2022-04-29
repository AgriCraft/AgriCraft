package com.infinityraider.agricraft.content;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.content.world.StructureGreenHouse;
import com.infinityraider.agricraft.content.world.StructureGreenHouseIrrigated;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.resources.ResourceLocation;

public final class AgriStructureRegistry {
    public static final StructureGreenHouse STANDARD_DESERT = new StructureGreenHouse(GreenHouses.Standard.DESERT, Pools.DESERT);
    public static final StructureGreenHouse STANDARD_PLAINS = new StructureGreenHouse(GreenHouses.Standard.PLAINS, Pools.PLAINS, ProcessorLists.MOSSIFY_10_PERCENT);
    public static final StructureGreenHouse STANDARD_SAVANNA = new StructureGreenHouse(GreenHouses.Standard.SAVANNA, Pools.SAVANNA);
    public static final StructureGreenHouse STANDARD_SNOWY = new StructureGreenHouse(GreenHouses.Standard.SNOWY, Pools.SNOWY);
    public static final StructureGreenHouse STANDARD_TAIGA = new StructureGreenHouse(GreenHouses.Standard.TAIGA, Pools.TAIGA, ProcessorLists.MOSSIFY_10_PERCENT);

    // TODO: design irrigated greenhouses
    public static final StructureGreenHouseIrrigated IRRIGATED_DESERT = null;//new StructureGreenHouseIrrigated(GreenHouses.Irrigated.DESERT, Pools.DESERT);
    public static final StructureGreenHouseIrrigated IRRIGATED_PLAINS = null;//new StructureGreenHouseIrrigated(GreenHouses.Irrigated.PLAINS, Pools.PLAINS);
    public static final StructureGreenHouseIrrigated IRRIGATED_SAVANNA = null;//new StructureGreenHouseIrrigated(GreenHouses.Irrigated.SAVANNA, Pools.SAVANNA);
    public static final StructureGreenHouseIrrigated IRRIGATED_SNOWY = null;//new StructureGreenHouseIrrigated(GreenHouses.Irrigated.SNOWY, Pools.SNOWY);
    public static final StructureGreenHouseIrrigated IRRIGATED_TAIGA = null;//new StructureGreenHouseIrrigated(GreenHouses.Irrigated.TAIGA, Pools.TAIGA);

    public static final class GreenHouses {
        public static final class Standard {
            public static final ResourceLocation DESERT = new ResourceLocation(AgriCraft.instance.getModId(), "village/desert/greenhouse");
            public static final ResourceLocation PLAINS = new ResourceLocation(AgriCraft.instance.getModId(), "village/plains/greenhouse");
            public static final ResourceLocation SAVANNA = new ResourceLocation(AgriCraft.instance.getModId(), "village/savanna/greenhouse");
            public static final ResourceLocation SNOWY = new ResourceLocation(AgriCraft.instance.getModId(), "village/snowy/greenhouse");
            public static final ResourceLocation TAIGA = new ResourceLocation(AgriCraft.instance.getModId(), "village/taiga/greenhouse");

            private Standard() {}
        }

        public static final class Irrigated {
            public static final ResourceLocation DESERT =new ResourceLocation(AgriCraft.instance.getModId(), "village/desert/greenhouse_irrigated");
            public static final ResourceLocation PLAINS = new ResourceLocation(AgriCraft.instance.getModId(), "village/plains/greenhouse_irrigated");
            public static final ResourceLocation SAVANNA = new ResourceLocation(AgriCraft.instance.getModId(), "village/savanna/greenhouse_irrigated");
            public static final ResourceLocation SNOWY = new ResourceLocation(AgriCraft.instance.getModId(), "village/snowy/greenhouse_irrigated");
            public static final ResourceLocation TAIGA = new ResourceLocation(AgriCraft.instance.getModId(), "village/taiga/greenhouse_irrigated");

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
