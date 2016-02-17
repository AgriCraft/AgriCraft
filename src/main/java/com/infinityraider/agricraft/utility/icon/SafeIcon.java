/*
 * A safe way to deal with those pesky icons.
 */
package com.infinityraider.agricraft.utility.icon;

import com.infinityraider.agricraft.utility.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 *
 * @author RlonRyan
 */
@SideOnly(Side.CLIENT)
public final class SafeIcon {
		
		public static final int MAX_ATTEMPTS = 10;
		
		public final String ident;
		
		private TextureAtlasSprite icon;
		private int loadAttempts;

		public SafeIcon(Item item) {
			this(item.getRegistryName().replaceFirst(":", ":items/"));
		}
		
		public SafeIcon(Block block) {
			this(block.getRegistryName().replaceFirst(":", ":blocks/"));
		}
		
		public SafeIcon(String ident) {
			this.ident = ident;
			this.icon = null;
			this.loadAttempts = 0;
		}
		
		public boolean isLoaded() {
			return (this.icon == null);
		}
		
		public TextureAtlasSprite getIcon() {
			
			if(this.loadAttempts < MAX_ATTEMPTS && this.icon == null) {
				
				this.icon = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(ident);
				this.loadAttempts++;
				
				if (this.loadAttempts == MAX_ATTEMPTS) {
					LogHelper.debug("SafeIcon load failure: " + ident);
				}
			}
			
			return this.icon == null ? getDefault() : this.icon;
			
		}
		
		public static TextureAtlasSprite getDefault() {
			return Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
		}
		
	}
