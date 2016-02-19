package com.infinityraider.agricraft.utility.icon;

import com.infinityraider.agricraft.utility.LogHelper;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

/**
 * An enum for tracking commonly used icons.
 * 
 * Cached internally as to ensure fast access.
 *
 * @author RlonRyan
 */
public enum BaseIcons {
	
	WATER_STILL("minecraft:blocks/water_still"),
	WATER_FLOW("minecraft:blocks/water_flow"),
	OAK_PLANKS("minecraft:blocks/planks_oak"),
	IRON_BLOCK("minecraft:blocks/iron_block"),
	DEBUG_ICON("agricraft:items/debugger");
	
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
			LogHelper.debug("Attempting load of icon for: " + this.name());
			isLoaded = attemptLoad();
			LogHelper.debug("Load " + this.name() + ": " + (isLoaded ? "SUCEEDED!" : "FAILED!"));
		}
		return cachedIcon;
	}
	
	private boolean attemptLoad() {
		cachedIcon = IconUtil.getIcon(location);
		return (cachedIcon != IconUtil.getDefaultIcon());
	}

}
