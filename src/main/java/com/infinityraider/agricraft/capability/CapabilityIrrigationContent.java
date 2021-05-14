package com.infinityraider.agricraft.capability;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationComponent;
import com.infinityraider.agricraft.api.v1.irrigation.IrrigationComponentCapability;
import com.infinityraider.agricraft.handler.IrrigationSystemHandler;
import com.infinityraider.agricraft.impl.v1.irrigation.IrrigationLayer;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.capability.IInfSerializableCapabilityImplementation;
import com.infinityraider.infinitylib.utility.ISerializable;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.List;

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

        public List<IrrigationLayer> layers() {
            return IrrigationSystemHandler.getInstance().getLayers(this.getComponent().world(), this.component.position());
        }

        public boolean isValid() {
            return this.layers() != null && this.layers().stream().allMatch(IrrigationLayer::isFullyLoaded);
        }

        @Override
        public int getTanks() {
            return 1;
        }

        @Nonnull
        @Override
        public FluidStack getFluidInTank(int tank) {
            if(this.isValid()) {
                int amount = 0;
                for (IrrigationLayer layer : this.layers()) {
                    amount += layer.getContent();
                }
                return amount <= 0 ? FluidStack.EMPTY : new FluidStack(Fluids.WATER, amount);
            }
            return FluidStack.EMPTY;
        }

        @Override
        public int getTankCapacity(int tank) {
            if(this.isValid()) {
                int capacity = 0;
                for (IrrigationLayer layer : this.layers()) {
                    capacity += layer.getCapacity();
                }
                return capacity;
            }
            return 0;
        }

        @Override
        public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
            return tank == 0 && stack.getFluid() == Fluids.WATER && this.isValid();
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            if(resource.getFluid() == Fluids.WATER) {
                return this.fill(resource.getAmount(), action);
            }
            return 0;
        }

        public int fill(int maxFill, FluidAction action) {
            // fill from top layer
            return this.layers().get(0).fill(maxFill, action);
        }

        @Nonnull
        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            if(resource.getFluid() == Fluids.WATER) {
                return this.drain(resource.getAmount(), action);
            }
            return FluidStack.EMPTY;
        }

        @Nonnull
        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            // drain from bottom layer
            int drain = this.layers().get(this.layers().size() - 1).drain(maxDrain, action);
            return drain <= 0 ? FluidStack.EMPTY : new FluidStack(Fluids.WATER, drain);
        }

        @Override
        public void readFromNBT(CompoundNBT tag) {
            // read the layers
            IrrigationSystemHandler.getInstance().loadLayers(this.getComponent(), tag.getList(AgriNBT.ENTRIES, 10));
        }

        @Override
        public CompoundNBT writeToNBT() {
            // write layers to each component
            CompoundNBT tag = new CompoundNBT();
            ListNBT layersTag = new ListNBT();
            this.layers().forEach(layer -> layersTag.add(layer.writeToTag()));
            tag.put(AgriNBT.ENTRIES, layersTag);
            return tag;
        }
    }
}
