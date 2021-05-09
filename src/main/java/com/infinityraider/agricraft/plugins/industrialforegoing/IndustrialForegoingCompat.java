package com.infinityraider.agricraft.plugins.industrialforegoing;

import com.buuz135.industrial.api.plant.PlantRecollectable;
import com.buuz135.industrial.registry.IFRegistries;
import com.infinityraider.agricraft.AgriCraft;
import net.minecraftforge.registries.DeferredRegister;

public class IndustrialForegoingCompat {
    static void execute() {
        DeferredRegister<PlantRecollectable> register = DeferredRegister.create(IFRegistries.PLANT_RECOLLECTABLES_REGISTRY, AgriCraft.instance.getModId());
        register.register(AgriCraft.instance.getModId(), AgriPlantRecollectable::new);
    }
}
