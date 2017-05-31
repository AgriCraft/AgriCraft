/*
 */
package com.infinityraider.agricraft.api.v1.crop;

import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 *
 * @author Ryan
 */
public class CropPlantedEvent extends Event {

    @Nonnull
    final IAgriCrop crop;

    @Nonnull
    final AgriSeed seed;

    @Nullable
    final EntityPlayer player;

    public CropPlantedEvent(@Nonnull IAgriCrop crop, @Nonnull AgriSeed seed, @Nullable EntityPlayer player) {
        this.crop = Objects.requireNonNull(crop);
        this.seed = Objects.requireNonNull(seed);
        this.player = player;
    }

    @Nonnull
    public IAgriCrop getCrop() {
        return crop;
    }

    @Nonnull
    public AgriSeed getSeed() {
        return seed;
    }

    @Nullable
    public EntityPlayer getPlayer() {
        return player;
    }

}
