package com.infinityraider.agricraft.api.v1.irrigation;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public final class IrrigationComponentCapability {
    @CapabilityInject(IAgriIrrigationComponent.class)
    public static final Capability<IAgriIrrigationComponent> CAPABILITY = null;

    public static final ResourceLocation KEY = new ResourceLocation("agricraft", "irrigation_component");

    /** Capability reference for attaching IAgriIrrigationComponent capabilities to TileEntities */
    public static Capability<IAgriIrrigationComponent> getCapability() {
        return CAPABILITY;
    }

    /** Key for attaching IAgriIrrigationComponent capabilities to TileEntities */
    public static ResourceLocation getKey() {
        return KEY;
    }


    // Thou shall not instantiate
    private IrrigationComponentCapability() {}
}
