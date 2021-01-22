package com.infinityraider.agricraft.utility;

import com.agricraft.agricore.core.AgriCore;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * An enum for tracking commonly used icons.
 *
 * Cached internally as to ensure fast access.
 */
@SideOnly(Side.CLIENT)
public enum BaseIcons {

    WATER_STILL("minecraft:blocks/water_still"),
    WATER_FLOW("minecraft:blocks/water_flow"),
    OAK_PLANKS("minecraft:blocks/planks_oak"),
    IRON_BLOCK("minecraft:blocks/iron_block"),
    DEBUG("agricraft:items/debugger"),
    DIRT("minecraft:blocks/dirt"),
    VINE("minecraft:blocks/vine");

    @Nonnull
    public final String location;

    @Nonnull
    private TextureAtlasSprite cachedIcon;
    @Nonnull
    private boolean isLoaded;

    BaseIcons(String location) {
        this.location = location;
        this.cachedIcon = IconHelper.getDefaultIcon();
        this.isLoaded = false;
    }

    public TextureAtlasSprite getIcon() {
        if (!isLoaded) {
            AgriCore.getLogger("agricraft").debug("Load Icon " + this.name() + " STARTED...");
            isLoaded = attemptLoad();
            AgriCore.getLogger("agricraft").debug("Load Icon " + this.name() + ": " + (isLoaded ? "SUCEEDED!" : "FAILED!"));
}
        return cachedIcon;
    }

    private boolean attemptLoad() {
        cachedIcon = IconHelper.getIcon(location);
        return (cachedIcon != IconHelper.getDefaultIcon());
    }

}
