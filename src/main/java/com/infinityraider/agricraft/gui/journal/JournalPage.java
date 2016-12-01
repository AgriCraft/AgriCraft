package com.infinityraider.agricraft.gui.journal;

import com.infinityraider.agricraft.gui.component.GuiComponent;
import com.infinityraider.agricraft.reference.Reference;
import java.util.Collections;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public interface JournalPage {

	ResourceLocation BACKGROUND = new ResourceLocation(Reference.MOD_ID, "textures/gui/journal/GuiJournalBackground.png");

	/**
	 * Gets the foreground texture
	 */
	ResourceLocation getForeground();

	/**
	 * Get the tooltip to render at this location, return null to render nothing
	 */
    default void addTooltip(int x, int y, List<String> lines) {
        // Nothing to do here...
    }

	/**
	 * Gets a list of text components to render on this page
	 */
    default List<GuiComponent> getComponents() {
        return Collections.EMPTY_LIST;
    }

}
