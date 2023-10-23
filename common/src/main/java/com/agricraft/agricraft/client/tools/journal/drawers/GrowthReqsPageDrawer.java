package com.agricraft.agricraft.client.tools.journal.drawers;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.codecs.AgriSoilCondition;
import com.agricraft.agricraft.api.codecs.AgriSoilValue;
import com.agricraft.agricraft.api.requirement.AgriSeason;
import com.agricraft.agricraft.api.tools.journal.JournalData;
import com.agricraft.agricraft.api.tools.journal.JournalPageDrawer;
import com.agricraft.agricraft.common.item.journal.GrowthReqsPage;
import com.agricraft.agricraft.common.util.LangUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class GrowthReqsPageDrawer implements JournalPageDrawer<GrowthReqsPage> {

	private final Component GROWTH_REQS = Component.translatable("agricraft.journal.growth_reqs");
	private final Component PARAGRAPH_L_1 = Component.translatable("agricraft.journal.growth_reqs.paragraph_1");
	private final Component BRIGHTNESS = Component.translatable("agricraft.journal.growth_reqs.brightness");
	private final Component PARAGRAPH_BRIGHTNESS = Component.translatable("agricraft.journal.growth_reqs.brightness.desc");
	private final Component HUMIDITY = Component.translatable("agricraft.journal.growth_reqs.humidity");
	private final Component PARAGRAPH_HUMIDITY = Component.translatable("agricraft.journal.growth_reqs.humidity.desc");
	private final Component ACIDITY = Component.translatable("agricraft.journal.growth_reqs.acidity");
	private final Component PARAGRAPH_ACIDITY = Component.translatable("agricraft.journal.growth_reqs.acidity.desc");
	private final Component NUTRIENTS = Component.translatable("agricraft.journal.growth_reqs.nutrients");
	private final Component PARAGRAPH_NUTRIENTS = Component.translatable("agricraft.journal.growth_reqs.nutrients.desc");
	private final Component SEASONS = Component.translatable("agricraft.journal.growth_reqs.seasons");
	private final Component PARAGRAPH_SEASONS = Component.translatable("agricraft.journal.growth_reqs.seasons.desc");

	@Override
	public void drawLeftSheet(GuiGraphics guiGraphics, GrowthReqsPage page, int pageX, int pageY, JournalData journalData) {
		Font font = Minecraft.getInstance().font;
		float dx = pageX + 6;
		float dy = pageY + 10;
		float spacing = 4;

		// Title
		guiGraphics.drawString(font, GROWTH_REQS, (int) dx, (int) dy, 0, false);
		dy += font.lineHeight;
		dy += spacing;
		// First paragraph
		dy += this.drawScaledText(guiGraphics, PARAGRAPH_L_1, dx, dy, 0.65F);
		dy += spacing;

		// Brightness
		dy += this.drawScaledText(guiGraphics, BRIGHTNESS, dx, dy, 0.65F);
		guiGraphics.blit(GUI_COMPONENTS, (int) dx, (int) dy, 0, 36, 66, 8, 128, 128);
		dy += spacing + 6;
		dy += this.drawScaledText(guiGraphics, PARAGRAPH_BRIGHTNESS, dx, dy, 0.50F);
		dy += spacing;

		// Humidity
		dy += this.drawScaledText(guiGraphics, HUMIDITY, dx, dy, 0.65F);
		dy += this.drawScaledText(guiGraphics, PARAGRAPH_HUMIDITY, dx, dy, 0.50F);
		this.drawSoilProperties(guiGraphics, dx, dy, spacing, "humidity", AgriSoilCondition.Humidity.values(), HUMIDITY_OFFSETS, 0);
	}

	@Override
	public void drawRightSheet(GuiGraphics guiGraphics, GrowthReqsPage page, int pageX, int pageY, JournalData journalData) {
		float dx = pageX + 6;
		float dy = pageY + 10;
		float spacing = 4;

		// Acidity
		dy += this.drawScaledText(guiGraphics, ACIDITY, dx, dy, 0.65F);
		dy += this.drawScaledText(guiGraphics, PARAGRAPH_ACIDITY, dx, dy, 0.50F);
		dy = this.drawSoilProperties(guiGraphics, dx, dy, spacing, "acidity", AgriSoilCondition.Acidity.values(), ACIDITY_OFFSETS, 12);

		// Nutrients
		dy += this.drawScaledText(guiGraphics, NUTRIENTS, dx, dy, 0.65F);
		dy += this.drawScaledText(guiGraphics, PARAGRAPH_NUTRIENTS, dx, dy, 0.50F);
		dy = this.drawSoilProperties(guiGraphics, dx, dy, spacing, "nutrients", AgriSoilCondition.Nutrients.values(), NUTRIENTS_OFFSETS, 24);

		// Seasons
		if (AgriApi.getSeasonLogic().isActive()) {
			dy += this.drawScaledText(guiGraphics, SEASONS, dx, dy, 0.65F);
			dy += this.drawScaledText(guiGraphics, PARAGRAPH_SEASONS, dx, dy, 0.50F);

			guiGraphics.blit(GUI_COMPONENTS, (int) dx, (int) (dy), 0, 44, 10, 12, 128, 128);
			int offset = this.drawScaledText(guiGraphics, LangUtils.seasonName(AgriSeason.SPRING), dx + 12, dy + 5, 0.50F);
			guiGraphics.blit(GUI_COMPONENTS, (int) dx, (int) (dy + offset + 9), 10, 44, 10, 12, 128, 128);
			this.drawScaledText(guiGraphics, LangUtils.seasonName(AgriSeason.SUMMER), dx + 12, dy + offset + 12, 0.50F);

			guiGraphics.blit(GUI_COMPONENTS, (int) dx + 45, (int) (dy), 20, 44, 10, 12, 128, 128);
			offset = this.drawScaledText(guiGraphics, LangUtils.seasonName(AgriSeason.AUTUMN), dx + 45 + 12, dy + 5, 0.50F);
			guiGraphics.blit(GUI_COMPONENTS, (int) dx + 45, (int) (dy + offset + 9), 30, 44, 10, 12, 128, 128);
			this.drawScaledText(guiGraphics, LangUtils.seasonName(AgriSeason.WINTER), dx + 45 + 12, dy + offset + 12, 0.50F);
		}
	}

	protected float drawSoilProperties(GuiGraphics guiGraphics, float dx, float dy, float spacing, String property, AgriSoilValue[] properties, int[] offsets, int textureOffsetY) {
		for (int i = 0; i < properties.length - 1; i++) {
			int width = offsets[i + 1] - offsets[i];
			guiGraphics.blit(GUI_COMPONENTS, (int) dx, (int) (dy), offsets[i], textureOffsetY, width, 12, 128, 128);
			this.drawScaledText(guiGraphics, LangUtils.soilPropertyName(property, properties[i]), dx + 12, dy + 5, 0.50F);
			dy += 10;
		}
		return dy + spacing;
	}

}
