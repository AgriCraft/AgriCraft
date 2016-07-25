package com.infinityraider.agricraft.gui.journal;

import com.infinityraider.agricraft.gui.component.GuiComponent;
import com.infinityraider.agricraft.reference.Reference;
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
	public void addComponents(List<GuiComponent> components) {
	}

}
