package com.infinityraider.agricraft.content;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.content.world.StructureGreenHouse;
import com.infinityraider.agricraft.content.world.StructureGreenHouseIrrigated;
import net.minecraft.util.ResourceLocation;

public class AgriStructureRegistry {
    private static final AgriStructureRegistry INSTANCE = new AgriStructureRegistry();

    public static AgriStructureRegistry getInstance() {
        return INSTANCE;
    }

    public final StructureGreenHouse standard_desert;
    public final StructureGreenHouse standard_plains;
    public final StructureGreenHouse standard_savanna;
    public final StructureGreenHouse standard_snowy;
    public final StructureGreenHouse standard_taiga;

    public final StructureGreenHouseIrrigated irrigated_desert;
    public final StructureGreenHouseIrrigated irrigated_plains;
    public final StructureGreenHouseIrrigated irrigated_savanna;
    public final StructureGreenHouseIrrigated irrigated_snowy;
    public final StructureGreenHouseIrrigated irrigated_taiga;

    private AgriStructureRegistry() {
        this.standard_desert = new StructureGreenHouse(GreenHouses.Standard.DESERT, Pools.DESERT);
        this.standard_plains = new StructureGreenHouse(GreenHouses.Standard.PLAINS, Pools.PLAINS);
        this.standard_savanna = new StructureGreenHouse(GreenHouses.Standard.SAVANNA, Pools.SAVANNA);
        this.standard_snowy = new StructureGreenHouse(GreenHouses.Standard.SNOWY, Pools.SNOWY);
        this.standard_taiga = new StructureGreenHouse(GreenHouses.Standard.TAIGA, Pools.TAIGA);

        // TODO: design irrigated greenhouses
        this.irrigated_desert =  null;//new StructureGreenHouseIrrigated(GreenHouses.Irrigated.DESERT, Pools.DESERT);
        this.irrigated_plains =  null;//new StructureGreenHouseIrrigated(GreenHouses.Irrigated.PLAINS, Pools.PLAINS);
        this.irrigated_savanna =  null;//new StructureGreenHouseIrrigated(GreenHouses.Irrigated.SAVANNA, Pools.SAVANNA);
        this.irrigated_snowy =  null;//new StructureGreenHouseIrrigated(GreenHouses.Irrigated.SNOWY, Pools.SNOWY);
        this.irrigated_taiga =  null;//new StructureGreenHouseIrrigated(GreenHouses.Irrigated.TAIGA, Pools.TAIGA);
    }

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
