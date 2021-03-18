package com.infinityraider.agricraft.capability;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationComponent;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationNetwork;
import com.infinityraider.agricraft.impl.v1.irrigation.IrrigationNetwork;
import com.infinityraider.agricraft.impl.v1.irrigation.IrrigationNetworkInvalid;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.capability.IInfCapabilityImplementation;
import com.infinityraider.infinitylib.utility.ISerializable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapabilityIrrigationNetworkComponent implements IInfCapabilityImplementation<TileEntity, CapabilityIrrigationNetworkComponent.Impl> {
    private static final CapabilityIrrigationNetworkComponent INSTANCE = new CapabilityIrrigationNetworkComponent();

    public static CapabilityIrrigationNetworkComponent getInstance() {
        return INSTANCE;
    }

    public static ResourceLocation KEY = new ResourceLocation(
            AgriCraft.instance.getModId().toLowerCase(), Names.Objects.IRRIGATION_NETWORK_COMPONENT);

    @CapabilityInject(Impl.class)
    public static final Capability<CapabilityIrrigationNetworkComponent.Impl> CAPABILITY = null;

    public IAgriIrrigationNetwork getIrrigationNetwork(IAgriIrrigationComponent component) {
        return component.castToTile().getCapability(this.getCapability())
                .map(Impl::getNetwork)
                .orElse(IrrigationNetworkInvalid.getInstance());
    }

    public void setIrrigationNetwork(IAgriIrrigationComponent component, IrrigationNetwork network) {
        component.castToTile().getCapability(this.getCapability())
                .ifPresent(impl -> impl.setNetwork(network));
    }

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
        return carrier instanceof IAgriIrrigationComponent;
    }

    @Override
    public Impl createNewValue(TileEntity carrier) {
        return new Impl((IAgriIrrigationComponent) carrier);
    }

    @Override
    public ResourceLocation getCapabilityKey() {
        return KEY;
    }

    @Override
    public Class<TileEntity> getCarrierClass() {
        return TileEntity.class;
    }

    public static class Impl implements ISerializable {
        private final IAgriIrrigationComponent component;

        private int networkId;

        private Impl(IAgriIrrigationComponent component) {
            this.component = component;
            this.networkId = -1;
        }

        public int getNetworkId() {
            return this.networkId;
        }

        public void setNetwork(IrrigationNetwork network) {
            this.networkId = network.getId();
        }

        @Nonnull
        public IAgriIrrigationNetwork getNetwork() {
            World world = this.getWorld();
            if(world == null || this.getNetworkId() < 0) {
                return IrrigationNetworkInvalid.getInstance();
            }
            return CapabilityIrrigationNetworkManager.getInstance().getNetwork(world, this.getNetworkId());
        }

        @Nonnull
        public IAgriIrrigationComponent getComponent() {
            return this.component;
        }

        @Nullable
        public World getWorld() {
            return this.getComponent().castToTile().getWorld();
        }

        @Override
        public void readFromNBT(CompoundNBT tag) {
            this.networkId = tag.contains(AgriNBT.KEY) ? tag.getInt(AgriNBT.KEY) : -1;
        }

        @Override
        public CompoundNBT writeToNBT() {
            CompoundNBT tag = new CompoundNBT();
            tag.putInt(AgriNBT.KEY, this.getNetworkId());
            return tag;
        }
    }
}
