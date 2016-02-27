package com.InfinityRaider.AgriCraft.compatibility.hydraulicraft;

import k4unl.minecraft.Hydraulicraft.api.HCApi;
import k4unl.minecraft.Hydraulicraft.api.ITrolleyRegistrar;

public class HydraulicraftAPI extends HydraulicraftAPIWrapper {
    private final HCApi.IHCApi hydraulicraftApi;

    protected  HydraulicraftAPI() {
        this.hydraulicraftApi = HCApi.getInstance();
    }

    @Override
    protected void registerTrolley() {
        if(hydraulicraftApi == null) {
            return;
        }
        ITrolleyRegistrar registrar = hydraulicraftApi.getTrolleyRegistrar();
        if(registrar == null) {
            return;
        }
        registrar.registerTrolley(new AgriCraftTrolley());
    }
}
