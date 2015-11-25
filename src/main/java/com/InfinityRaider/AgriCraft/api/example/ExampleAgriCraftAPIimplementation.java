package com.InfinityRaider.AgriCraft.api.example;

import com.InfinityRaider.AgriCraft.api.API;
import com.InfinityRaider.AgriCraft.api.APIBase;
import com.InfinityRaider.AgriCraft.api.v2.ISeedStats;
import com.InfinityRaider.AgriCraft.api.v2.APIv2;
import net.minecraft.world.World;

public class ExampleAgriCraftAPIimplementation extends ExampleAgriCraftAPIwrapper {
    private APIv2 api;
    private boolean ok;

    /**
     * Constructor grabs the wanted API version and checks if it is correctly initialized
     */
    protected ExampleAgriCraftAPIimplementation() {
        super();
        APIBase apiObj = API.getAPI(1);
        if(apiObj != null && (apiObj instanceof APIv2)) {
            api = (APIv2) apiObj;
            ok = api.getStatus().isOK();
        } else {
            ok = false;
        }
    }


    /**
     * Here are the actual implementations for the methods you need to call API methods
     * Note the isOk() calls, this is not necessary, but guarantees that your implementation will not start returning values you don't expect,
     * for instance when the api version you use becomes {@Link APIStatus.ERROR} or {@Link APIStatus.BACKLEVEL_UNSUPPORTED}
     */

    /**
     * Method to check if the API is properly wrapped
     */
    public boolean isOk() {
        return ok;
    }

    /**
     * Example method, this one gets the stats of a crop
     */
    public SeedStatsExample exampleMethodGetSeedStats(World world, int x, int y, int z) {
        if(isOk()) {
            ISeedStats stats = api.getStats(world, x, y ,z);
            return new SeedStatsExample(stats.getGrowth(), stats.getGain(), stats.getStrength(), stats.isAnalyzed());
        } else {
            return super.exampleMethodGetSeedStats(world, x, y, z);
        }
    }

    /**
     * Example method, this one gets the stat cap imposed to agricraft
     */
    public short exampleMethodGetSeedStatsCap() {
        return isOk()?api.getStatCap():super.exampleMethodGetSeedStatsCap();
    }
}
