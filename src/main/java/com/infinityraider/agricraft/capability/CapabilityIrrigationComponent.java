package com.infinityraider.agricraft.capability;

import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationComponent;
import com.infinityraider.agricraft.api.v1.irrigation.IrrigationComponentCapability;
import com.infinityraider.infinitylib.capability.IInfCapabilityImplementation;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;

public class CapabilityIrrigationComponent implements IInfCapabilityImplementation<TileEntity, IAgriIrrigationComponent> {
    private static final CapabilityIrrigationComponent INSTANCE = new CapabilityIrrigationComponent();

    private static final Serializer<IAgriIrrigationComponent> SERIALIZER = new Serializer<IAgriIrrigationComponent>() {
        @Override
        public CompoundNBT writeToNBT(IAgriIrrigationComponent object) {
            return new CompoundNBT();
        }

        @Override
        public void readFromNBT(IAgriIrrigationComponent object, CompoundNBT nbt) {}
    };

    public static CapabilityIrrigationComponent getInstance() {
        return INSTANCE;
    }

    private CapabilityIrrigationComponent() {}

    @Override
    public Class<IAgriIrrigationComponent> getCapabilityClass() {
        return IAgriIrrigationComponent.class;
    }

    @Override
    public Serializer<IAgriIrrigationComponent> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public void copyData(IAgriIrrigationComponent from, IAgriIrrigationComponent to) {
        // Not needed
    }

    @Override
    public Capability<IAgriIrrigationComponent> getCapability() {
        return IrrigationComponentCapability.getCapability();
    }

    @Override
    public boolean shouldApplyCapability(TileEntity carrier) {
        return false;
    }

    @Override
    public IAgriIrrigationComponent createNewValue(TileEntity carrier) {
        // Won't ever be called
        return null;
    }

    @Override
    public ResourceLocation getCapabilityKey() {
        return IrrigationComponentCapability.getKey();
    }

    @Override
    public Class<TileEntity> getCarrierClass() {
        return TileEntity.class;
    }
}
