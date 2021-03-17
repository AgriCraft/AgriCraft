package com.infinityraider.agricraft.capability;

import com.google.common.collect.Maps;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.impl.v1.irrigation.IrrigationNetwork;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.capability.IInfCapabilityImplementation;
import com.infinityraider.infinitylib.utility.ISerializable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import java.util.Map;

public class CapabilityIrrigationNetworkData implements IInfCapabilityImplementation<World, CapabilityIrrigationNetworkData.Impl> {
    private static final CapabilityIrrigationNetworkData INSTANCE = new CapabilityIrrigationNetworkData();

    public static CapabilityIrrigationNetworkData getInstance() {
        return INSTANCE;
    }

    public static ResourceLocation KEY = new ResourceLocation(AgriCraft.instance.getModId().toLowerCase(), Names.Blocks.TANK);

    @CapabilityInject(CapabilityIrrigationNetworkData.Impl.class)
    public static final Capability<CapabilityIrrigationNetworkData.Impl> CAPABILITY = null;

    public int addNetworkToWorld(IrrigationNetwork network) {
        return network.getWorld().getCapability(this.getCapability()).map(impl -> impl.addNetwork(network)).orElse(-1);
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
    public boolean shouldApplyCapability(World carrier) {
        return true;
    }

    @Override
    public Impl createNewValue(World world) {
        return new Impl(world);
    }

    @Override
    public ResourceLocation getCapabilityKey() {
        return KEY;
    }

    @Override
    public Class<World> getCarrierClass() {
        return World.class;
    }

    public static class Impl implements ISerializable {
        private final World world;
        private final Map<Integer, IrrigationNetwork> networks;

        private int nextId;

        private Impl(World world) {
            this.world = world;
            this.networks = Maps.newHashMap();
            this.nextId = 0;
        }

        public int addNetwork(IrrigationNetwork network) {
            int id = this.nextId;
            this.nextId++;
            this.networks.put(id, network);
            return id;
        }

        @Override
        public void readFromNBT(CompoundNBT tag) {

        }

        @Override
        public CompoundNBT writeToNBT() {
            CompoundNBT tag = new CompoundNBT();
            return tag;
        }
    }
}
