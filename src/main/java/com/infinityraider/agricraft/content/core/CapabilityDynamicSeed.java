package com.infinityraider.agricraft.content.core;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.impl.v1.plant.NoPlant;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.infinitylib.capability.ICapabilityImplementation;
import com.infinityraider.infinitylib.utility.ISerializable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import javax.annotation.Nonnull;

public class CapabilityDynamicSeed implements ICapabilityImplementation<ItemStack, CapabilityDynamicSeed.Impl> {
    private static final CapabilityDynamicSeed INSTANCE = new CapabilityDynamicSeed();

    public static CapabilityDynamicSeed getInstance() {
        return INSTANCE;
    }

    public static ResourceLocation KEY = new ResourceLocation(AgriCraft.instance.getModId(), "plant");

    public static IAgriPlant getPlant(ItemStack stack) {
        return stack.getCapability(CAPABILITY).map(Impl::getPlant).orElse(NoPlant.getInstance());
    }

    public static void setSeed(IAgriPlant plant, ItemStack seed) {
        if(seed.getItem() instanceof ItemDynamicAgriSeed) {
            seed.getCapability(CAPABILITY).ifPresent(impl -> impl.setPlant(plant));
        }
    }

    @CapabilityInject(value = Impl.class)
    public static Capability<Impl> CAPABILITY = null;

    private CapabilityDynamicSeed() {}

    @Override
    public Capability<Impl> getCapability() {
        return CAPABILITY;
    }

    @Override
    public boolean shouldApplyCapability(ItemStack carrier) {
        return carrier.getItem() instanceof ItemDynamicAgriSeed;
    }

    @Override
    public Impl createNewValue(ItemStack carrier) {
        return new Impl();
    }

    @Override
    public ResourceLocation getCapabilityKey() {
        return KEY;
    }

    @Override
    public Class<ItemStack> getCarrierClass() {
        return ItemStack.class;
    }

    @Override
    public Class<Impl> getCapabilityClass() {
        return Impl.class;
    }

    public static class Impl implements ISerializable {
        private IAgriPlant plant;

        private Impl() {
            this.plant = NoPlant.getInstance();
        }

        @Nonnull
        public IAgriPlant getPlant() {
            return this.plant;
        }

        private void setPlant(@Nonnull IAgriPlant plant) {
            this.plant = plant;
        }

        @Override
        public void readFromNBT(CompoundNBT tag) {
            this.plant = tag.contains(AgriNBT.PLANT)
                    ? AgriApi.getPlantRegistry().get(tag.getString(AgriNBT.PLANT)).orElse(NoPlant.getInstance())
                    : NoPlant.getInstance();
        }

        @Override
        public CompoundNBT writeToNBT() {
            CompoundNBT tag = new CompoundNBT();
            if(this.plant != null && this.plant.isPlant()) {
                tag.putString(AgriNBT.PLANT, this.plant.getId());
            }
            return tag;
        }
    }
}
