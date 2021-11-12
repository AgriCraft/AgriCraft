package com.infinityraider.agricraft.plugins.industrialforegoing;

import com.buuz135.industrial.api.plant.PlantRecollectable;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@SuppressWarnings("unused")
public class IndustrialForegoingCompat {
    @SubscribeEvent
    public static void execute(RegistryEvent.Register<PlantRecollectable> event) {
        event.getRegistry().register(new AgriPlantRecollectable());
    }
}
