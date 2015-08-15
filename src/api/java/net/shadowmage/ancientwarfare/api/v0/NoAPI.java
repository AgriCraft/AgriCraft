package net.shadowmage.ancientwarfare.api.v0;

import net.shadowmage.ancientwarfare.api.APIBase;
import net.shadowmage.ancientwarfare.api.APIStatus;

/**
 * Filler object to represent the API until Ancient Warfare had the chance to
 * initialize itself.
 *
 */
public class NoAPI implements APIBase {

    @Override
    public APIStatus getStatus() {
        return APIStatus.API_NOT_INITIALIZED;
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public APIBase getAPI(int maxVersion) {
        return this;
    }

}
