package com.infinityraider.agricraft.utility.icon;

import com.agricraft.agricore.core.AgriCore;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * An enum for tracking commonly used icons.
 * 
 * Cached internally as to ensure fast access.
 *
 * @author RlonRyan
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

	private BaseIcons(String location) {
		this.location = location;
		this.cachedIcon = IconUtil.getDefaultIcon();
		this.isLoaded = false;
	}

	public TextureAtlasSprite getIcon() {
		if (!isLoaded) {
			AgriCore.getLogger("AgriCraft").debug("Load Icon " + this.name() + " STARTED...");
			isLoaded = attemptLoad();
			AgriCore.getLogger("AgriCraft").debug("Load Icon " + this.name() + ": " + (isLoaded ? "SUCEEDED!" : "FAILED!"));
		}
		return cachedIcon;
	}
	
	private boolean attemptLoad() {
		cachedIcon = IconUtil.getIcon(location);
		return (cachedIcon != IconUtil.getDefaultIcon());
	}

}
