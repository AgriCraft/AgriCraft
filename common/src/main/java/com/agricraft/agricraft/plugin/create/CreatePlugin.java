package com.agricraft.agricraft.plugin.create;

import com.simibubi.create.AllMovementBehaviours;
import net.minecraft.resources.ResourceLocation;

public class CreatePlugin {

    /*
     * Create does not really provide a way to implement compatibility for more complex crops.
     * Therefore, in order to obtain compatibility, we must employ a rather aggressive approach,
     * which consists of overriding their default harvesting behaviour with our own.
     * This can be toggled in the config, so if people do not like this, they can disable it.
     * Until Create exposes harvesting logic in an API or via IMC, we must do it this way, unfortunately.
     */
    public static void init() {
        AllMovementBehaviours.registerBehaviour(new ResourceLocation("create", "mechanical_harvester"), new AgriHarvesterMovementBehaviour());
    }
}
