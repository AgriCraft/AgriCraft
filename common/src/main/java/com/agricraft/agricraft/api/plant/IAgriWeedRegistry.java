package com.agricraft.agricraft.api.plant;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.util.IAgriRegistry;

public interface IAgriWeedRegistry extends IAgriRegistry<IAgriWeed> {

	/**
	 * @return the AgriCraft IAgriWeedRegistry instance
	 */
	@SuppressWarnings("unused")
	static IAgriWeedRegistry getInstance() {
		return AgriApi.getWeedRegistry();
	}

	/**
	 * @return AgriCraft's placeholder IAgriWeed implementation representing the absence of a weed
	 */
	IAgriWeed getNoWeed();

}
