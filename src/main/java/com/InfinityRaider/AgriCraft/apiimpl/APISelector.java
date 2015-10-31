package com.InfinityRaider.AgriCraft.apiimpl;

import com.InfinityRaider.AgriCraft.api.API;
import com.InfinityRaider.AgriCraft.api.APIBase;
import com.InfinityRaider.AgriCraft.api.APIStatus;
import com.InfinityRaider.AgriCraft.apiimpl.v1.APIimplv1;
import com.InfinityRaider.AgriCraft.apiimpl.v2.APIimplv2;

public class APISelector implements APIBase {

	private APISelector() {}
	
	public static void init() {
		API.setAPI(new APISelector());
	}
	
	@Override
	public APIBase getAPI(int maxVersion) {
		if (maxVersion <= 0) {
			return this;
		} else {
			switch(maxVersion) {
				case 1:
					return new APIimplv1(1, APIStatus.BACKLEVEL_OK);
				default:
					return new APIimplv2(2, APIStatus.OK);
			}
		}
	}

	@Override
	public APIStatus getStatus() {
		return APIStatus.ERROR;
	}

	@Override
	public int getVersion() {
		return 0;
	}

}
