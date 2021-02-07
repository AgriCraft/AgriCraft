package com.infinityraider.agricraft.api.v1.plant;

import com.infinityraider.agricraft.api.v1.misc.IAgriRegistry;

public interface IAgriWeedRegistry extends IAgriRegistry<IAgriWeed> {
    /**
     * @return AgriCraft's placeholder IAgriWeed implementation representing the absence of a weed
     */
    IAgriWeed getNoWeed();
}
