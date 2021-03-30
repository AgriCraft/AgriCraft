package com.infinityraider.agricraft.capability;

import com.google.common.collect.Maps;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationComponent;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationNetwork;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationNode;
import com.infinityraider.agricraft.impl.v1.irrigation.IrrigationNetwork;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.capability.IInfSerializableCapabilityImplementation;
import com.infinityraider.infinitylib.utility.ISerializable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Map;

/**
 * Capability to store and fetch references to IrrigationNetwork IDs on IrrigationNetworkComponents
 */
public class CapabilityIrrigationNetworkReference implements IInfSerializableCapabilityImplementation<TileEntity, CapabilityIrrigationNetworkReference.Impl> {
    private static final CapabilityIrrigationNetworkReference INSTANCE = new CapabilityIrrigationNetworkReference();

    public static CapabilityIrrigationNetworkReference getInstance() {
        return INSTANCE;
    }

    public static ResourceLocation KEY = new ResourceLocation(
            AgriCraft.instance.getModId().toLowerCase(), Names.Objects.IRRIGATION_NETWORK_COMPONENT);

    @CapabilityInject(Impl.class)
    public static final Capability<CapabilityIrrigationNetworkReference.Impl> CAPABILITY = null;

    public IAgriIrrigationNetwork getIrrigationNetwork(IAgriIrrigationComponent component, Direction side) {
        return this.getCapability(component.getTile(), side)
                .map(impl -> impl.getNetwork(side))
                .orElse(IrrigationNetwork.getInvalid());
    }

    public void setIrrigationNetwork(IAgriIrrigationComponent component, @Nullable Direction dir, int id) {
        this.getCapability(component.getTile(), dir).ifPresent(impl -> impl.setNetwork(dir, id));
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
        return CapabilityIrrigationComponent.getInstance().isIrrigationComponent(carrier);
    }

    @Override
    public Impl createNewValue(TileEntity carrier) {
        return CapabilityIrrigationComponent.getInstance().getIrrigationComponent(carrier)
                .map(Impl::new)
                .orElse(null);
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

        private final Map<IAgriIrrigationNode, Integer> ids;

        // Need to lazy-initialize these, as the TileEntities must have the chance to fully construct first
        private final Map<IAgriIrrigationNode, IAgriIrrigationNetwork> defaults;

        private Impl(IAgriIrrigationComponent component) {
            this.component = component;
            this.ids = Maps.newIdentityHashMap();
            this.defaults = Maps.newIdentityHashMap();
        }

        public void setNetwork(@Nullable Direction dir, int id) {
            this.getComponent().getNode(dir).map(node -> this.ids.put(node, id));
        }

        public int getNetworkId(@Nullable Direction dir) {
            return this.getComponent().getNode(dir).map(node -> this.ids.getOrDefault(node, -1)).orElse(-1);
        }

        @Nonnull
        public IAgriIrrigationNetwork getNetwork(@Nullable Direction dir) {
            World world = this.getWorld();
            if(world == null) {
                return IrrigationNetwork.getInvalid();
            }
            int id = this.getNetworkId(dir);
            if(id < 0) {
                return this.getDefault(dir);
            }
            return CapabilityIrrigationNetworkManager.getInstance().getNetwork(world, id);
        }

        protected IAgriIrrigationNetwork getDefault(@Nullable Direction dir) {
            return this.getComponent().getNode(dir)
                    .map(node ->  this.defaults.computeIfAbsent(node, (aNode) -> IrrigationNetwork.createSingleNodeNetwork(this.getComponent(), node)))
                    .orElse(IrrigationNetwork.getInvalid());
        }

        @Nonnull
        public IAgriIrrigationComponent getComponent() {
            return this.component;
        }

        @Nullable
        public World getWorld() {
            return this.getComponent().getTile().getWorld();
        }

        @Override
        public void readFromNBT(CompoundNBT tag) {
            if(tag.contains(AgriNBT.ENTRIES)) {
                ListNBT dirTags = tag.getList(AgriNBT.ENTRIES, 10);
                for(int i = 0; i < dirTags.size(); i++) {
                    CompoundNBT dirTag = dirTags.getCompound(i);
                    if(dirTag.contains(AgriNBT.KEY) && dirTag.contains(AgriNBT.DIRECTION)) {
                        int id = dirTag.getInt(AgriNBT.KEY);
                        if(id >= 0) {
                            Direction dir = Direction.byIndex(dirTag.getInt(AgriNBT.DIRECTION));
                            this.setNetwork(dir, id);
                            this.onIdDeserialized(dir, id);
                        }
                    }
                }
            }
        }

        protected void onIdDeserialized(@Nullable Direction dir, int id) {
            World world = this.getWorld();
            if(world == null) {
                // TODO
                throw new RuntimeException("Component deserialized before chunk is deserialized");
            } else {
                CapabilityIrrigationNetworkChunkData.getInstance().onComponentDeserialized(
                        world.getChunkAt(this.getComponent().getTile().getPos()),
                        this.getComponent(),
                        id, dir
                );
            }
        }

        @Override
        public CompoundNBT writeToNBT() {
            CompoundNBT tag = new CompoundNBT();
            ListNBT dirTags = new ListNBT();
            Arrays.stream(Direction.values()).forEach(dir -> {
                CompoundNBT dirTag = new CompoundNBT();
                dirTag.putInt(AgriNBT.KEY, this.getNetworkId(dir));
                dirTag.putInt(AgriNBT.DIRECTION, dir.ordinal());
                dirTags.add(dir.ordinal(), dirTag);
            });
            tag.put(AgriNBT.ENTRIES, dirTags);
            return tag;
        }
    }
}
