package com.agricraft.agricraft.api.tools.journal;

import com.agricraft.agricraft.api.AgriApi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public interface JournalPageDrawer<T extends JournalPage> {

	int PAGE_WIDTH = 125;
	int PAGE_HEIGHT = 180;
	ResourceLocation GUI_COMPONENTS = new ResourceLocation(AgriApi.MOD_ID, "textures/gui/gui_components.png");
	int[] HUMIDITY_OFFSETS = new int[]{0, 8, 16, 26, 36, 46, 53};
	int[] ACIDITY_OFFSETS = new int[]{0, 7, 15, 22, 30, 38, 46, 53};
	int[] NUTRIENTS_OFFSETS = new int[]{0, 6, 14, 23, 32, 43, 53};

	void drawLeftSheet(GuiGraphics guiGraphics, T page, int pageX, int pageY, JournalData journalData);

	void drawRightSheet(GuiGraphics guiGraphics, T page, int pageX, int pageY, JournalData journalData);

	default void drawLeftTooltip(GuiGraphics guiGraphics, T page, int pageX, int pageY, int mouseX, int mouseY) {
	}

	default void drawRightTooltip(GuiGraphics guiGraphics, T page, int pageX, int pageY, int mouseX, int mouseY) {
	}

	default int drawScaledText(GuiGraphics guiGraphics, Component component, float x, float y, float scale) {
		guiGraphics.pose().pushPose();
		guiGraphics.pose().scale(scale, scale, scale);
		guiGraphics.drawWordWrap(Minecraft.getInstance().font, component, (int) (x / scale), (int) (y / scale), (int) (PAGE_WIDTH / scale), 0);
		guiGraphics.pose().popPose();
		return (int) (Minecraft.getInstance().font.wordWrapHeight(component, (int) (PAGE_WIDTH / scale)) * scale);
	}

}
