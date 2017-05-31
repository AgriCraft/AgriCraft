/*
 */
package com.infinityraider.agricraft.api.v1.crop;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 *
 * @author Ryan
 */
@Cancelable
public class CropHarvestEvent extends Event {

    @Nonnull
    private final IAgriCrop crop;

    @Nullable
    private final EntityPlayer player;

    public CropHarvestEvent(@Nonnull IAgriCrop crop, @Nullable EntityPlayer player) {
        this.crop = Objects.requireNonNull(crop);
        this.player = player;
    }

    @Nonnull
    public IAgriCrop getCrop() {
        return crop;
    }

    public EntityPlayer getPlayer() {
        return player;
    }

}
