package com.agricraft.agricraft.client.tools.journal.drawers;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.AgriClientApi;
import com.agricraft.agricraft.api.LangUtils;
import com.agricraft.agricraft.api.tools.journal.JournalData;
import com.agricraft.agricraft.api.tools.journal.JournalPageDrawer;
import com.agricraft.agricraft.common.item.journal.MutationsPage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class MutationPageDrawer implements JournalPageDrawer<MutationsPage> {

	@Override
	public void drawLeftSheet(GuiGraphics guiGraphics, MutationsPage page, int pageX, int pageY, JournalData journalData) {
		int dy = 6;
		for (List<ResourceLocation> plants : page.getMutationsLeft()) {
			this.drawMutation(guiGraphics, plants, pageX + 10, pageY + dy);
			dy += 20;
		}
	}

	@Override
	public void drawRightSheet(GuiGraphics guiGraphics, MutationsPage page, int pageX, int pageY, JournalData journalData) {
		int dy = 6;
		for (List<ResourceLocation> plants : page.getMutationsRight()) {
			this.drawMutation(guiGraphics, plants, pageX + 10, pageY + dy);
			dy += 20;
		}
	}

	public void drawMutation(GuiGraphics guiGraphics, List<ResourceLocation> plants, int pageX, int pageY) {
		guiGraphics.blit(GUI_COMPONENTS, pageX + 10, pageY + 24, 0, 76, 86, 18, 128, 128);
		TextureAtlasSprite parent1 = AgriClientApi.get().getPlantModel(plants.get(0), AgriApi.get().getPlant(plants.get(0)).map(plant -> plant.getInitialGrowthStage().total() - 1).orElse(0)).getParticleIcon();
		TextureAtlasSprite parent2 = AgriClientApi.get().getPlantModel(plants.get(1), AgriApi.get().getPlant(plants.get(1)).map(plant -> plant.getInitialGrowthStage().total() - 1).orElse(0)).getParticleIcon();
		TextureAtlasSprite child = AgriClientApi.get().getPlantModel(plants.get(2), AgriApi.get().getPlant(plants.get(2)).map(plant -> plant.getInitialGrowthStage().total() - 1).orElse(0)).getParticleIcon();
		guiGraphics.blit(pageX + 11, pageY + 25, 1, 16, 16, parent1);
		guiGraphics.blit(pageX + 45, pageY + 25, 1, 16, 16, parent2);
		guiGraphics.blit(pageX + 79, pageY + 25, 1, 16, 16, child);
	}

	@Override
	public void drawLeftTooltip(GuiGraphics guiGraphics, MutationsPage page, int pageX, int pageY, int mouseX, int mouseY) {
		int dy = 6;
		for (List<ResourceLocation> plants : page.getMutationsLeft()) {
			Component component = getComponent(mouseX, mouseY, dy, plants);
			if (component != null) {
				guiGraphics.renderTooltip(Minecraft.getInstance().font, component, mouseX + pageX, mouseY + pageY);
			}
			dy += 20;
		}
	}

	@Override
	public void drawRightTooltip(GuiGraphics guiGraphics, MutationsPage page, int pageX, int pageY, int mouseX, int mouseY) {
		int dy = 6;
		for (List<ResourceLocation> plants : page.getMutationsRight()) {
			Component component = getComponent(mouseX, mouseY, dy, plants);
			if (component != null) {
				guiGraphics.renderTooltip(Minecraft.getInstance().font, component, mouseX + pageX, mouseY + pageY);
			}
			dy += 20;
		}
	}

	private Component getComponent(int mouseX, int mouseY, int y, List<ResourceLocation> plants) {
		if (11 <= mouseX && mouseX <= 27 && y + 25 <= mouseY && mouseY <= y + 41) {
			return LangUtils.plantName(plants.get(0).toString());
		} else if (45 <= mouseX && mouseX <= 61 && y + 25 <= mouseY && mouseY <= y + 41) {
			return LangUtils.plantName(plants.get(1).toString());
		} else if (79 <= mouseX && mouseX <= 95 && y + 25 <= mouseY && mouseY <= y + 41) {
			return LangUtils.plantName(plants.get(2).toString());
		}
		return null;
	}

}
