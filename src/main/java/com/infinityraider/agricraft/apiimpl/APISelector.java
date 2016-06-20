package com.infinityraider.agricraft.apiimpl;

import com.infinityraider.agricraft.api.API;
import com.infinityraider.agricraft.api.APIBase;
import com.infinityraider.agricraft.api.APIStatus;
import com.infinityraider.agricraft.apiimpl.v1.APIimplv1;

public class APISelector implements APIBase {

	private APISelector() {
	}

	public static void init() {
		API.setAPI(new APISelector());
	}

	@Override
	public APIBase getAPI(int maxVersion) {
		if (maxVersion <= 0) {
			return this;
		} else {
			switch (maxVersion) {
				case 1:
					return APIimplv1.getInstance();
				default:
					return APIimplv1.getInstance();
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
