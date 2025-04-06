package com.agricraft.agricraft.client.tools.journal.drawers;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.AgriClientApi;
import com.agricraft.agricraft.api.LangUtils;
import com.agricraft.agricraft.api.codecs.AgriSoilCondition;
import com.agricraft.agricraft.api.requirement.AgriSeason;
import com.agricraft.agricraft.api.tools.journal.JournalData;
import com.agricraft.agricraft.api.tools.journal.JournalPageDrawer;
import com.agricraft.agricraft.common.item.AgriSeedItem;
import com.agricraft.agricraft.common.item.journal.PlantPage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class PlantPageDrawer implements JournalPageDrawer<PlantPage> {

	private static final Component GROWTH_STAGES = Component.translatable("agricraft.journal.growth_stages");
	private static final Component GROWTH_REQUIREMENTS = Component.translatable("agricraft.journal.growth_reqs");
	private static final Component PRODUCTS = Component.translatable("agricraft.journal.products");
	private static final Component MUTATIONS = Component.translatable("agricraft.journal.mutations");

	@Override
	public void drawLeftSheet(GuiGraphics guiGraphics, PlantPage page, int pageX, int pageY, JournalData journalData) {
		Font font = Minecraft.getInstance().font;
		// Title
		guiGraphics.blit(GUI_COMPONENTS, pageX + 4, pageY + 9, 0, 56, 128, 20, 128, 128);
		String plantId = page.getPlant().getId().map(ResourceLocation::toString).orElse("");
		Component plantName = LangUtils.plantName(plantId);
		Component seedName = LangUtils.seedName(plantId);
		int titleWidth = font.width(plantName);
		if (titleWidth > 74) {
			float scale = 0.8F;
			while (font.width(seedName) * scale > 74) {
				scale -= 0.1F;
			}
			this.drawScaledText(guiGraphics, seedName, pageX + 30, pageY + 15, scale);
		} else {
			guiGraphics.drawString(font, seedName, pageX + 30, pageY + 15, 0, false);
		}
		// Seed
		guiGraphics.renderItem(AgriSeedItem.toStack(page.getPlant()), pageX + 8, pageY + 11);
		// Description
		Component plantDescription = LangUtils.plantDescription(plantId);
		float offset = 0.0F;
		if (plantDescription != null) {
			offset = this.drawScaledText(guiGraphics, plantDescription, pageX + 10, pageY + 30, 0.70F);
		}
		// Growth requirements
		pageY = this.drawGrowthRequirements(guiGraphics, page, pageX, pageY, 35 + offset);
		// Products
		this.drawProducts(guiGraphics, page, pageX, pageY);
	}

	@Override
	public void drawRightSheet(GuiGraphics guiGraphics, PlantPage page, int pageX, int pageY, JournalData journalData) {
		// Mutations
		this.drawMutations(guiGraphics, page, pageX, pageY);
	}

	protected int drawGrowthRequirements(GuiGraphics guiGraphics, PlantPage page, int pageX, int pageY, float yOffset) {
		float dy = pageY + Math.max(yOffset, 60);
		dy += this.drawScaledText(guiGraphics, GROWTH_REQUIREMENTS, pageX + 10, dy, 0.80F) + 1;
		// Light level
		guiGraphics.blit(GUI_COMPONENTS, pageX + 10, (int) dy, 0, 36, 66, 8, 128, 128);

		for (int i = 0; i < page.brightnessMask().length; i++) {
			boolean current = page.brightnessMask()[i];
			if (current) {
				boolean prev = i > 0 && page.brightnessMask()[i - 1];
				boolean next = i < (page.brightnessMask().length - 1) && page.brightnessMask()[i + 1];
				guiGraphics.blit(GUI_COMPONENTS, pageX + 10 + 4 * i + 1, (int) dy, 4, 8, 67, 36, 2, 8, 128, 128);
				if (!prev) {
					guiGraphics.blit(GUI_COMPONENTS, pageX + 10 + 4 * i, (int) dy, 66, 36, 1, 8, 128, 128);
				}
				if (!next) {
					guiGraphics.blit(GUI_COMPONENTS, pageX + 10 + 4 * i + 5, (int) dy, 66, 36, 1, 8, 128, 128);
				}
			}
		}
		dy += 9;
		// Seasons
		if (AgriApi.get().getSeasonLogic().isActive()) {
			for (int i = 0; i < page.seasonMask().length; i++) {
				int x = pageX + 75 + i % 2 * 12;
				int y = (int) (dy + 6 + i / 2 * 12);
				if (page.seasonMask()[i]) {
					guiGraphics.blit(GUI_COMPONENTS, x, y, i * 10, 44, 10, 12, 128, 128);
				} else {
					guiGraphics.blit(GUI_COMPONENTS, x, y, 40 + i * 10, 44, 10, 12, 128, 128);
				}
			}
		}
		// Humidity
		for (int i = 0; i < page.humidityMask().length; i++) {
			int dx = HUMIDITY_OFFSETS[i];
			int width = HUMIDITY_OFFSETS[i + 1] - HUMIDITY_OFFSETS[i];
			if (page.humidityMask()[i]) {
				guiGraphics.blit(GUI_COMPONENTS, pageX + dx + 10, (int) dy, dx, 0, width, 12, 128, 128);
			} else {
				guiGraphics.blit(GUI_COMPONENTS, pageX + dx + 10, (int) dy, dx + 53, 0, width, 12, 128, 128);
			}
		}
		dy += 13;
		// Acidity
		for (int i = 0; i < page.acidityMask().length; i++) {
			int dx = ACIDITY_OFFSETS[i];
			int width = ACIDITY_OFFSETS[i + 1] - ACIDITY_OFFSETS[i];
			if (page.acidityMask()[i]) {
				guiGraphics.blit(GUI_COMPONENTS, pageX + dx + 10, (int) dy, dx, 12, width, 12, 128, 128);
			} else {
				guiGraphics.blit(GUI_COMPONENTS, pageX + dx + 10, (int) dy, dx + 53, 12, width, 12, 128, 128);
			}
		}
		dy += 13;
		// Nutrients
		for (int i = 0; i < page.nutrientsMask().length; i++) {
			int dx = NUTRIENTS_OFFSETS[i];
			int width = NUTRIENTS_OFFSETS[i + 1] - NUTRIENTS_OFFSETS[i];
			if (page.nutrientsMask()[i]) {
				guiGraphics.blit(GUI_COMPONENTS, pageX + dx + 10, (int) dy, dx, 24, width, 12, 128, 128);
			} else {
				guiGraphics.blit(GUI_COMPONENTS, pageX + dx + 10, (int) dy, dx + 53, 24, width, 12, 128, 128);
			}
		}
		return (int) (dy + 13);
	}

	protected void drawProducts(GuiGraphics guiGraphics, PlantPage page, int pageX, int pageY) {
		this.drawScaledText(guiGraphics, PRODUCTS, pageX + 10, pageY + 10, 0.80F);
		for (int i = 0; i < page.getProducts().size(); i++) {
			guiGraphics.blit(GUI_COMPONENTS, pageX + 10 + i * 20, pageY + 19, 0, 76, 18, 18, 128, 128);
			guiGraphics.renderItem(page.getProducts().get(i), pageX + 11 + i * 20, pageY + 20);
		}
	}

	protected void drawMutations(GuiGraphics guiGraphics, PlantPage page, int pageX, int pageY) {
		this.drawScaledText(guiGraphics, MUTATIONS, pageX + 10, pageY + 15, 0.80F);
		for (List<ResourceLocation> plants : page.getMutationsOnPage()) {
			guiGraphics.blit(GUI_COMPONENTS, pageX + 10, pageY + 24, 0, 76, 86, 18, 128, 128);
			TextureAtlasSprite parent1 = AgriClientApi.get().getPlantModel(plants.get(0), AgriApi.get().getPlant(plants.get(0)).map(plant -> plant.getInitialGrowthStage().total() - 1).orElse(0)).getParticleIcon();
			TextureAtlasSprite parent2 = AgriClientApi.get().getPlantModel(plants.get(1), AgriApi.get().getPlant(plants.get(1)).map(plant -> plant.getInitialGrowthStage().total() - 1).orElse(0)).getParticleIcon();
			TextureAtlasSprite child = AgriClientApi.get().getPlantModel(plants.get(2), AgriApi.get().getPlant(plants.get(2)).map(plant -> plant.getInitialGrowthStage().total() - 1).orElse(0)).getParticleIcon();
			guiGraphics.blit(pageX + 11, pageY + 25, 1, 16, 16, parent1);
			guiGraphics.blit(pageX + 45, pageY + 25, 1, 16, 16, parent2);
			guiGraphics.blit(pageX + 79, pageY + 25, 1, 16, 16, child);
			pageY += 20;
		}
	}

	@Override
	public void drawLeftTooltip(GuiGraphics guiGraphics, PlantPage page, int pageX, int pageY, int mouseX, int mouseY) {
		Font font = Minecraft.getInstance().font;
		String plantId = page.getPlant().getId().map(ResourceLocation::toString).orElse("");
		Component plantName = LangUtils.plantName(plantId);
		Component plantDescription = LangUtils.plantDescription(plantId);
		// seed item
		if (8 <= mouseX && mouseX <= 24 && 11 <= mouseY && mouseY <= 27) {
			guiGraphics.renderTooltip(font, plantName, mouseX + pageX, mouseY + pageY);
			return;
		}

		// Growth requirements

		float offset = 35;
		if (plantDescription != null) {
			offset += this.drawScaledText(guiGraphics, plantDescription, -1000, -1000, 0.70F);  // draw offscreen to get offset
		}
		float dy = Math.max(offset, 60);
		dy += this.drawScaledText(guiGraphics, GROWTH_REQUIREMENTS, -1000, -1000, 0.80F) + 1;  // draw offscreen to get offset
		// Light level
		for (int i = 0; i < page.brightnessMask().length; i++) {
			if (10 + 4 * i <= mouseX && mouseX <= 10 + 4 * i + 4 && dy + 1 <= mouseY && mouseY <= dy + 9) {
				guiGraphics.renderTooltip(font, Component.translatable("agricraft.tooltip.light").append(" " + i), mouseX + pageX, mouseY + pageY);
				return;
			}
		}
		dy += 9;
		// Seasons
		if (AgriApi.get().getSeasonLogic().isActive()) {
			for (int i = 0; i < page.seasonMask().length; i++) {
				int x = 75 + i % 2 * 12;
				int y = (int) (dy + 6 + i / 2 * 12);
				if (x <= mouseX && mouseX <= x + 10 && y <= mouseY && mouseY <= y + 12) {
					guiGraphics.renderTooltip(font, LangUtils.seasonName(AgriSeason.values()[i]), mouseX + pageX, mouseY + pageY);
					return;
				}
			}
		}
		// Humidity
		for (int i = 0; i < page.humidityMask().length; i++) {
			int dx = HUMIDITY_OFFSETS[i] + 10;
			int w = HUMIDITY_OFFSETS[i + 1] - HUMIDITY_OFFSETS[i];
			if (dx <= mouseX && mouseX <= dx + w && dy <= mouseY && mouseY <= dy + 12) {
				guiGraphics.renderTooltip(font, LangUtils.soilPropertyName("humidity", AgriSoilCondition.Humidity.values()[i]), mouseX + pageX, mouseY + pageY);
				return;
			}
		}
		dy += 13;
		// Acidity
		for (int i = 0; i < page.acidityMask().length; i++) {
			int dx = ACIDITY_OFFSETS[i] + 10;
			int w = ACIDITY_OFFSETS[i + 1] - ACIDITY_OFFSETS[i];
			if (dx <= mouseX && mouseX <= dx + w && dy <= mouseY && mouseY <= dy + 12) {
				guiGraphics.renderTooltip(font, LangUtils.soilPropertyName("acidity", AgriSoilCondition.Acidity.values()[i]), mouseX + pageX, mouseY + pageY);
				return;
			}
		}
		dy += 13;
		// Nutrients
		for (int i = 0; i < page.nutrientsMask().length; i++) {
			int dx = NUTRIENTS_OFFSETS[i] + 10;
			int w = NUTRIENTS_OFFSETS[i + 1] - NUTRIENTS_OFFSETS[i];
			if (dx <= mouseX && mouseX <= dx + w && dy <= mouseY && mouseY <= dy + 12) {
				guiGraphics.renderTooltip(font, LangUtils.soilPropertyName("nutrients", AgriSoilCondition.Nutrients.values()[i]), mouseX + pageX, mouseY + pageY);
				return;
			}
		}
		dy += 33;
		// products tooltips
		for (int i = 0; i < page.getProducts().size(); i++) {
			if (11 + i * 20 <= mouseX && mouseX <= 11 + i * 20 + 16 && dy <= mouseY && mouseY <= dy + 16) {
				guiGraphics.renderTooltip(font, page.getProducts().get(i), mouseX + pageX, mouseY + pageY);
			}
		}

	}

	@Override
	public void drawRightTooltip(GuiGraphics guiGraphics, PlantPage page, int pageX, int pageY, int mouseX, int mouseY) {
		Font font = Minecraft.getInstance().font;
		// mutation tooltips
		int y = 0;
		for (List<ResourceLocation> plants : page.getMutationsOnPage()) {
			if (11 <= mouseX && mouseX <= 27 && y + 25 <= mouseY && mouseY <= y + 41) {
				guiGraphics.renderTooltip(font, LangUtils.plantName(plants.get(0).toString()), mouseX + pageX, mouseY + pageY);
			} else if (45 <= mouseX && mouseX <= 61 && y + 25 <= mouseY && mouseY <= y + 41) {
				guiGraphics.renderTooltip(font, LangUtils.plantName(plants.get(1).toString()), mouseX + pageX, mouseY + pageY);
			} else if (79 <= mouseX && mouseX <= 95 && y + 25 <= mouseY && mouseY <= y + 41) {
				guiGraphics.renderTooltip(font, LangUtils.plantName(plants.get(2).toString()), mouseX + pageX, mouseY + pageY);
			}
			y += 20;
		}
	}

}
