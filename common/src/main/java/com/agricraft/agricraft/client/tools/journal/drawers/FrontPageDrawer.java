package com.agricraft.agricraft.client.tools.journal.drawers;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.tools.journal.JournalData;
import com.agricraft.agricraft.api.tools.journal.JournalPageDrawer;
import com.agricraft.agricraft.common.item.journal.FrontPage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class FrontPageDrawer implements JournalPageDrawer<FrontPage> {

	private static final ResourceLocation BACKGROUND_FRONT_RIGHT = new ResourceLocation(AgriApi.MOD_ID, "textures/gui/journal/front_page.png");

	@Override
	public void drawLeftSheet(GuiGraphics guiGraphics, FrontPage page, int pageX, int pageY, JournalData journalData) {

	}

	@Override
	public void drawRightSheet(GuiGraphics guiGraphics, FrontPage page, int pageX, int pageY, JournalData journalData) {
		guiGraphics.blit(BACKGROUND_FRONT_RIGHT, pageX + 8, pageY + 25, 0, 0, 128, 192, 128, 192);
	}

}
