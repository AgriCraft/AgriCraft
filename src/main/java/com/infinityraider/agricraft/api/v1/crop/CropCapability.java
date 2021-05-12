package com.infinityraider.agricraft.api.v1.crop;

import com.infinityraider.agricraft.api.v1.AgriApi;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public final class CropCapability {
    @CapabilityInject(IAgriCrop.class)
    public static final Capability<IAgriCrop> CAPABILITY = null;

    public static final ResourceLocation KEY = new ResourceLocation("agricraft", "crop");

    /** Capability reference for attaching IAgriCrop capabilities to TileEntities */
    public static Capability<IAgriCrop> getCapability() {
        return CAPABILITY;
    }

    /** Key for attaching IAgriCrop capabilities to TileEntities */
    public static ResourceLocation getKey() {
        return KEY;
    }

    /**
     * Registers a capability instance which AgriCraft will attach automatically to the respective tile entities
     *
     * @param instance the instance
     * @param <T> the exact type of the TileEntity to attach to
     * @param <C> the parent type of the IAgriCrop being attached
     */
    public static <T extends TileEntity, C extends IAgriCrop> void registerInstance(Instance<T, C> instance) {
        AgriApi.registerCapabilityCropInstance(instance);
    }

    /**
     * Interface to create IAgriCrop capability instances
     * @param <T> the exact type of the TileEntity to attach to
     * @param <C> the parent type of the IAgriCrop being attached
     */
    public interface Instance<T extends TileEntity, C extends IAgriCrop> {
        /**
         * Agricraft will automatically attach an IAgriCrop object to TileEntities of this class
         * @return the carrier class
         */
        Class<T> getCarrierClass();

        /**
         * This is the class your IAgriCrop objects must extend
         * @return the crop class
         */
        Class<C> getCropClass();

        /**
         * Method to construct the IAgriCrop object to attach to the given tile
         *
         * @param tile the TileEntity
         * @return the IAgriCrop object
         */
        C createCropFor(T tile);

        /**
         * Method to serialize the IAgriCrop objects
         * @param tag the tag to write to
         * @param crop the IAgriCrop object being serialized
         */
        void writeToNBT(CompoundNBT tag, C crop);

        /**
         * Method to deserialize the IAgriCrop objects
         * @param tag the tag to read from
         * @param crop the IAgriCrop object being deserialized
         */
        void readFromNBT(CompoundNBT tag, C crop);
    }


    // Thou shall not instantiate
    private CropCapability() {}
}
