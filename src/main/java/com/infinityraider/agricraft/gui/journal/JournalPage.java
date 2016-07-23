package com.infinityraider.agricraft.gui.journal;

import com.infinityraider.agricraft.gui.component.Component;
import com.infinityraider.agricraft.gui.component.ComponentItem;
import com.infinityraider.agricraft.gui.component.ComponentText;
import com.infinityraider.agricraft.gui.component.ComponentTexture;
import com.infinityraider.agricraft.reference.Reference;
import java.util.ArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public abstract class JournalPage {

	private static final ResourceLocation BACKGROUND = new ResourceLocation(Reference.MOD_ID, "textures/gui/journal/GuiJournalBackground.png");

	/**
	 * Gets the background texture
	 */
	public static ResourceLocation getBackground() {
		return BACKGROUND;
	}

	/**
	 * Gets the foreground texture
	 */
	public abstract ResourceLocation getForeground();

	/**
	 * Get the tooltip to render at this location, return null to render nothing
	 */
	public void addTooltip(int x, int y, List<String> lines) {
	}

	/**
	 * Gets a list of text components to render on this page
	 */
	public void addTextComponents(List<ComponentText> components) {
	}

	/**
	 * Gets a list of item components to render on this page
	 */
	public void addItemComponents(List<ComponentItem> components) {
	}

	/**
	 * Gets a list of texture components to render on this page
	 */
	public void addTextureComponents(List<ComponentTexture> components) {
	}

	/**
	 * Gets the increment to the page number on a mouseclick, >0 means browse
	 * forwards, <0 means browse backwards and 0 means stay on this page
	 */
	public abstract int getPagesToBrowseOnMouseClick(int x, int y);
}
