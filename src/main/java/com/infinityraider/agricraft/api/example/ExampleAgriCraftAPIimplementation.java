package com.infinityraider.agricraft.api.example;

import com.infinityraider.agricraft.api.API;
import com.infinityraider.agricraft.api.APIBase;
import com.infinityraider.agricraft.api.v3.APIv3;

public class ExampleAgriCraftAPIimplementation extends ExampleAgriCraftAPIwrapper {
    private APIv3 api;
    private boolean ok;

    /**
     * Constructor grabs the wanted API version and checks if it is correctly initialized
     */
    protected ExampleAgriCraftAPIimplementation() {
        super();
        APIBase apiObj = API.getAPI(1);
        if(apiObj != null && (apiObj instanceof APIv3)) {
            api = (APIv3) apiObj;
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
	@Override
    public boolean isOk() {
        return ok;
    }

    /**
     * Example method, this one gets the stat cap imposed to agricraft
     */
	@Override
    public short exampleMethodGetSeedStatsCap() {
        return isOk()?api.getStatCap():super.exampleMethodGetSeedStatsCap();
    }
}
