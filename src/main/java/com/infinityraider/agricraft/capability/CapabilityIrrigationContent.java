package com.infinityraider.agricraft.capability;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationComponent;
import com.infinityraider.agricraft.api.v1.irrigation.IrrigationComponentCapability;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.capability.IInfSerializableCapabilityImplementation;
import com.infinityraider.infinitylib.utility.ISerializable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;

public class CapabilityIrrigationContent implements IInfSerializableCapabilityImplementation<TileEntity, CapabilityIrrigationContent.Impl> {
    private static final CapabilityIrrigationContent INSTANCE = new CapabilityIrrigationContent();

    public static CapabilityIrrigationContent getInstance() {
        return INSTANCE;
    }

    public static ResourceLocation KEY = new ResourceLocation(AgriCraft.instance.getModId().toLowerCase(), Names.Objects.IRRIGATION_NETWORK_DATA);

    @CapabilityInject(CapabilityIrrigationContent.Impl.class)
    public static final Capability<CapabilityIrrigationContent.Impl> CAPABILITY = null;

    private CapabilityIrrigationContent() {}

    @Override
    public Class<Impl> getCapabilityClass() {
        return Impl.class;
    }

    @Override
    public Capability<Impl> getCapability() {
        return CAPABILITY;
    }

    @Override
    public boolean shouldApplyCapability(TileEntity carrier) {
        return carrier instanceof IAgriIrrigationComponent || carrier.getCapability(IrrigationComponentCapability.getCapability()).isPresent();
    }

    @Override
    public Impl createNewValue(TileEntity carrier) {
        if(carrier instanceof IAgriIrrigationComponent) {
            return new Impl((IAgriIrrigationComponent) carrier);
        }
        return carrier.getCapability(IrrigationComponentCapability.getCapability())
                .map(Impl::new)
                .orElseThrow(() -> new IllegalArgumentException("Can not create irrigation layer capability for a tile which is not an irrigation component"));
    }

    @Override
    public ResourceLocation getCapabilityKey() {
        return KEY;
    }

    @Override
    public Class<TileEntity> getCarrierClass() {
        return TileEntity.class;
    }

    public static class Impl implements IFluidHandler, ISerializable {
        private final IAgriIrrigationComponent component;

        private Impl(IAgriIrrigationComponent component) {
            this.component = component;
        }

        public IAgriIrrigationComponent getComponent() {
            return this.component;
        }

        @Override
        public int getTanks() {
            return 1;
        }

        @Nonnull
        @Override
        public FluidStack getFluidInTank(int tank) {
            // TODO
            return FluidStack.EMPTY;
        }

        @Override
        public int getTankCapacity(int tank) {
            // TODO
            return 0;
        }

        @Override
        public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
            // TODO
            return false;
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            // TODO
            return 0;
        }

        @Nonnull
        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            // TODO
            return FluidStack.EMPTY;
        }

        @Nonnull
        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            // TODO
            return FluidStack.EMPTY;
        }

        @Override
        public void readFromNBT(CompoundNBT tag) {
            // TODO
        }

        @Override
        public CompoundNBT writeToNBT() {
            CompoundNBT tag = new CompoundNBT();
            // TODO
            return tag;
        }
    }
}
