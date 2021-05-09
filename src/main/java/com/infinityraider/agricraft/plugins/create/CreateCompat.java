package com.infinityraider.agricraft.plugins.create;

import com.infinityraider.agricraft.AgriCraft;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllMovementBehaviours;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementBehaviour;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.Field;
import java.util.HashMap;

public class CreateCompat {
    /*
     * Create does not really provide a way to implement compatibility for more complex crops.
     * Therefore, in order to obtain compatibility, we must employ a rather aggressive approach,
     * which consists of overriding their default harvesting behaviour with our own.
     * This can be toggled in the config, so if people do not like this, they can disable it.
     * Until Create exposes harvesting logic in an API or via IMC, we must do it this way, unfortunately.
     */
    @SuppressWarnings("unchecked")
    static void injectAgriHarvesterMovementBehaviour() {
        try {
            Class<AllMovementBehaviours> clazz = AllMovementBehaviours.class;
            Field field = clazz.getDeclaredField("movementBehaviours");
            field.setAccessible(true);
            HashMap<ResourceLocation, MovementBehaviour> map = (HashMap<ResourceLocation, MovementBehaviour>) field.get(null);
            map.put(AllBlocks.MECHANICAL_HARVESTER.getId(), new AgriHarvesterMovementBehaviour());
        } catch(Exception e) {
            AgriCraft.instance.getLogger().error("Failed to inject agri harvester behaviour");
            AgriCraft.instance.getLogger().printStackTrace(e);
        }
    }
}
