package com.infinityraider.agricraft.capability;

import com.google.common.collect.Sets;
import com.infinityraider.agricraft.api.v1.crop.CropCapability;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.infinitylib.capability.IInfCapabilityImplementation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;

import java.util.Set;

public class CapabilityCrop implements IInfCapabilityImplementation<BlockEntity, IAgriCrop> {
    private static final CapabilityCrop INSTANCE = new CapabilityCrop();
    private static final CropSerializer SERIALIZER = new CropSerializer();

    public static CapabilityCrop getInstance() {
        return INSTANCE;
    }

    private final Set<CropCapability.Instance<?,?>> instances;

    private CapabilityCrop() {
        this.instances = Sets.newConcurrentHashSet();
    }

    public <T extends BlockEntity, C extends IAgriCrop> void registerInstance(CropCapability.Instance<T, C> instance) {
        this.instances.add(instance);
    }

    @Override
    public Class<IAgriCrop> getCapabilityClass() {
        return IAgriCrop.class;
    }

    @Override
    public Serializer<IAgriCrop> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public void copyData(IAgriCrop from, IAgriCrop to) {
        this.getSerializer().readFromNBT(to, this.getSerializer().writeToNBT(from));
    }

    @Override
    public Capability<IAgriCrop> getCapability() {
        return CropCapability.CAPABILITY;
    }

    @Override
    public boolean shouldApplyCapability(BlockEntity carrier) {
        return this.instances.stream().anyMatch(instance -> instance.getCarrierClass().equals(carrier.getClass()));
    }

    @Override
    public IAgriCrop createNewValue(BlockEntity carrier) {
        return this.instances.stream()
                .filter(instance -> instance.getCarrierClass().equals(carrier.getClass()))
                .findAny()
                .map(instance -> this.createInstance(carrier, instance))
                .orElseThrow(() -> new IllegalArgumentException("Can not attach IAgriCrop capability to an unregistered TileEntity"));
    }

    @SuppressWarnings("unchecked")
    private <T extends BlockEntity, C extends IAgriCrop> C createInstance(BlockEntity carrier, CropCapability.Instance<T, C> instance) {
        return instance.createCropFor((T) carrier);
    }

    @Override
    public ResourceLocation getCapabilityKey() {
        return CropCapability.KEY;
    }

    @Override
    public Class<BlockEntity> getCarrierClass() {
        return BlockEntity.class;
    }

    private static class CropSerializer implements Serializer<IAgriCrop> {
        private CropSerializer() {}

        @Override
        public CompoundTag writeToNBT(IAgriCrop crop) {
            CompoundTag tag = new CompoundTag();
            this.fetchInstance(crop).writeToNBT(tag, crop);
            return tag;
        }

        @Override
        public void readFromNBT(IAgriCrop crop, CompoundTag tag) {
            this.fetchInstance(crop).readFromNBT(tag, crop);
        }

        @SuppressWarnings("unchecked")
        private <T extends BlockEntity, C extends IAgriCrop> CropCapability.Instance<T,C> fetchInstance(IAgriCrop crop) {
            return (CropCapability.Instance<T,C>) CapabilityCrop.getInstance().instances.stream()
                    .filter(instance -> instance.getCropClass().isAssignableFrom(crop.getClass()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Can not (de)serialize an unregistered crop capability"));
        }
    }
}
