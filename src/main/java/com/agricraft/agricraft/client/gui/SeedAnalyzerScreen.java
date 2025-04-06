package com.agricraft.agricraft.client.gui;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.LangUtils;
import com.agricraft.agricraft.api.genetic.AgriGenome;
import com.agricraft.agricraft.api.genetic.Chromosome;
import com.agricraft.agricraft.common.inventory.container.SeedAnalyzerMenu;
import com.agricraft.agricraft.common.registry.AgriRegistries;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;
import java.util.Optional;

public class SeedAnalyzerScreen extends AbstractContainerScreen<SeedAnalyzerMenu> {

	private final ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(AgriApi.MOD_ID, "textures/gui/seed_analyzer.png");
	private final Component TEXT_SEPARATOR = Component.literal("-");

	private int geneIndex;

	public SeedAnalyzerScreen(SeedAnalyzerMenu screenContainer, Inventory inv, Component title) {
		super(screenContainer, inv, title);
		this.imageWidth = 186;
		this.imageHeight = 186;
		this.inventoryLabelY = this.imageHeight - 94;
		this.geneIndex = 0;
	}

	private static boolean hoverUpButton(int startX, int startY, int mouseX, int mouseY) {
		return startX + 67 <= mouseX && mouseX <= startX + 67 + 9 && startY + 26 <= mouseY && mouseY <= startY + 26 + 9;
	}

	private static boolean hoverDownButton(int startX, int startY, int mouseX, int mouseY) {
		return startX + 67 <= mouseX && mouseX <= startX + 67 + 9 && startY + 90 <= mouseY && mouseY <= startY + 90 + 9;
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		super.render(guiGraphics, mouseX, mouseY, partialTick);
		this.renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, GUI);
		int relX = (this.width - this.imageWidth) / 2;
		int relY = (this.height - this.imageHeight) / 2;
		// background
		guiGraphics.blit(GUI, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight);
		//journal slot
		guiGraphics.blit(GUI, leftPos + 25, topPos + 70, 186, 73, 18, 18);
		// magnifying glass
		guiGraphics.blit(GUI, leftPos + 13, topPos + 25, 0, 186, 56, 56);

		Optional<AgriGenome> optionalGenome = menu.getGenomeToRender();
		if (optionalGenome.isEmpty()) {
			return;
		}
		AgriGenome genome = optionalGenome.get();

		// up/down buttons if there are more than 6 stats genes
		if (genome.getStatChromosomes().size() > 6) {
			int upXOffset = hoverUpButton(leftPos, topPos, mouseX, mouseY) ? 195 : 186;
			guiGraphics.blit(GUI, leftPos + 67, topPos + 26, upXOffset, 91, 9, 9);
			int downXOffset = hoverDownButton(leftPos, topPos, mouseX, mouseY) ? 195 : 186;
			guiGraphics.blit(GUI, leftPos + 67, topPos + 90, downXOffset, 100, 9, 9);
		}

		// species gene
		Chromosome<String> species = genome.species();
		Component speciesDomText = LangUtils.plantName(species.dominant());
		Component speciesRecText = LangUtils.plantName(species.recessive());
		int domw = this.font.width(speciesDomText.getString());
		int middle = leftPos + this.imageWidth / 2;
		int sepLength = this.font.width(TEXT_SEPARATOR.getString());
		guiGraphics.drawString(this.font, TEXT_SEPARATOR, (int) (middle - sepLength / 2F), topPos + 16, 0, false);
		guiGraphics.drawString(this.font, speciesDomText, (int) (middle - domw - sepLength / 2F - 1), topPos + 16, 0, false);
		guiGraphics.drawString(this.font, speciesRecText, (int) (middle + sepLength / 2F + 1), topPos + 16, 0, false);
		// stats genes
		int DNA_X = leftPos + 90;
		int yy = topPos + 26;
		int[] lineAmount = {3, 2, 2, 3, 2, 3};
		int[] lineStart = {0, 15, 25, 35, 50, 60};
		List<Chromosome<Integer>> statGenes = genome.getStatChromosomes().stream().toList();
		for (int i = geneIndex, lineIndex = 0; i < geneIndex + 6; i++, lineIndex++) {
			Chromosome<Integer> chromosome = statGenes.get(i);
			// color lines of the gene
			for (int k = 0; k < lineAmount[lineIndex]; k++) {
				guiGraphics.hLine(DNA_X, DNA_X + 9, topPos + 26 + lineStart[lineIndex] + k * 5, chromosome.gene().getDominantColor());
				guiGraphics.hLine(DNA_X + 9, DNA_X + 9 + 8, topPos + 26 + lineStart[lineIndex] + k * 5, chromosome.gene().getRecessiveColor());
			}
			// text of the gene
			ResourceLocation key = AgriRegistries.GENES.getRegistry().get().getKey(chromosome.gene());
			Component geneText = AgriRegistries.STATS.getRegistry().get().getOptional(key).map(LangUtils::statName).orElse(Component.empty());
			Component domText = Component.literal("" + chromosome.dominant());
			Component recText = Component.literal("" + chromosome.recessive());
			int w = this.font.width(domText.getString());
			guiGraphics.drawString(this.font, geneText, DNA_X + 36, yy, 0, false);
			guiGraphics.drawString(this.font, domText, DNA_X - w - 1, yy, 0, false);
			guiGraphics.drawString(this.font, recText, DNA_X + 21, yy, 0, false);
			yy += this.font.lineHeight + 4;
		}
		// shape of the dna
		guiGraphics.blit(GUI, DNA_X, topPos + 26, 186, 0, 19, 73);
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.drawString(this.font, this.title, this.titleLabelX + 5, this.titleLabelY, 0x404040, false);
		guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX + 5, this.inventoryLabelY, 0x404040, false);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		Optional<AgriGenome> opt = menu.getGenomeToRender();
		if (opt.isEmpty()) {
			return super.mouseClicked(mouseX, mouseY, button);
		}
		int maxIndex = opt.get().getStatChromosomes().size() - 1;
		if (opt.map(agriGenome -> agriGenome.getStatChromosomes().size()).orElse(0) > 6) {
			int startX = (this.width - this.imageWidth) / 2;
			int startY = (this.height - this.imageHeight) / 2;
			if (hoverUpButton(startX, startY, (int) mouseX, (int) mouseY)) {
				if (geneIndex > 0) {
					geneIndex--;
				}
			}
			if (hoverDownButton(startX, startY, (int) mouseX, (int) mouseY)) {
				if (maxIndex - geneIndex > 6) {
					geneIndex++;
				}
			}
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		Optional<AgriGenome> opt = menu.getGenomeToRender();
		if (opt.isEmpty()) {
			return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
		}
		int maxIndex = opt.get().getStatChromosomes().size() - 1;
		if (maxIndex > 6) {
			if (scrollY < 0) {
				if (maxIndex - geneIndex > 6) {
					geneIndex++;
				}
			} else if (scrollY > 0) {
				if (geneIndex > 0) {
					geneIndex--;
				}
			}
		}
		return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
	}

}
