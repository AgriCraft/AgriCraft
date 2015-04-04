package com.InfinityRaider.AgriCraft.api.v0;

import com.InfinityRaider.AgriCraft.api.APIBase;
import com.InfinityRaider.AgriCraft.api.APIStatus;

/**
 * Filler object to represent the API until Agricraft had the chance to
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
