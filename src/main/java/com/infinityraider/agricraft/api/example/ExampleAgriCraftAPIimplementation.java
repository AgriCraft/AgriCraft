package com.infinityraider.agricraft.api.example;

import com.infinityraider.agricraft.api.API;
import com.infinityraider.agricraft.api.APIBase;
import com.infinityraider.agricraft.api.v1.ISeedStats;
import com.infinityraider.agricraft.api.v1.APIv1;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class ExampleAgriCraftAPIimplementation extends ExampleAgriCraftAPIwrapper {
    private APIv1 api;
    private boolean ok;

    /**
     * Constructor grabs the wanted API version and checks if it is correctly initialized
     */
    protected ExampleAgriCraftAPIimplementation() {
        super();
        APIBase apiObj = API.getAPI(1);
        if(apiObj != null && (apiObj instanceof APIv1)) {
            api = (APIv1) apiObj;
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
    public SeedStatsExample exampleMethodGetSeedStats(World world, BlockPos pos) {
        if(isOk()) {
            ISeedStats stats = api.getStats(world, pos);
            return new SeedStatsExample(stats.getGrowth(), stats.getGain(), stats.getStrength(), stats.isAnalyzed());
        } else {
            return super.exampleMethodGetSeedStats(world, pos);
        }
    }

    /**
     * Example method, this one gets the stat cap imposed to agricraft
     */
    public short exampleMethodGetSeedStatsCap() {
        return isOk()?api.getStatCap():super.exampleMethodGetSeedStatsCap();
    }
}
