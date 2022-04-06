package com.infinityraider.agricraft.plugins.agrigui.analyzer;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenePair;
import com.infinityraider.agricraft.impl.v1.genetics.GeneSpecies;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class SeedAnalyzerScreen extends AbstractContainerScreen<SeedAnalyzerContainer> {

	private final ResourceLocation GUI = new ResourceLocation(AgriCraft.instance.getModId(), "textures/gui/seed_analyzer_gui.png");
	private final Component TEXT_SEPARATOR = new TextComponent("-");

	private int geneIndex;

	public SeedAnalyzerScreen(SeedAnalyzerContainer screenContainer, Inventory inv, Component title) {
		super(screenContainer, inv, title);
		this.imageWidth = 186;
		this.imageHeight = 186;
		this.titleLabelY = this.imageHeight - 94;
		this.geneIndex = 0;
	}

	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		this.renderTooltip(matrixStack, mouseX, mouseY);
	}

	@Override
	protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindForSetup(GUI);
		int relX = (this.width - this.imageWidth) / 2;
		int relY = (this.height - this.imageHeight) / 2;
		// background
		this.blit(matrixStack, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
		//journal slot
		this.blit(matrixStack, relX + 25, relY + 70, 186, 73, 18, 18);
		// magnifying glass
		this.blit(matrixStack, relX + 13, relY + 25, 0, 186, 56, 56);

		List<IAgriGenePair<?>> genes = menu.getGeneToRender();
		int geneSize = genes.size();

		// up/down buttons
		if (geneSize > 7) {
			int upXOffset = hoverUpButton(relX, relY, mouseX, mouseY) ? 195 : 186;
			this.blit(matrixStack, relX + 67, relY + 26, upXOffset, 91, 9, 9);
			int downXOffset = hoverDownButton(relX, relY, mouseX, mouseY) ? 195 : 186;
			this.blit(matrixStack, relX + 67, relY + 90, downXOffset, 100, 9, 9);
		}

		if (!genes.isEmpty()) {
			int DNA_X = relX + 90;
			int yy = relY + 26;
			// species gene
			for (int i = 0; i < geneSize; i++) {
				if (genes.get(i).getGene() instanceof GeneSpecies) {
					IAgriGenePair<?> pair = genes.remove(i);
					Component domText = pair.getDominant().getTooltip();
					Component recText = pair.getRecessive().getTooltip();
					int domw = this.font.width(domText.getString());
					int middle = relX + this.imageWidth / 2;
					int sepLength = this.font.width(TEXT_SEPARATOR.getString());
					this.font.draw(matrixStack, TEXT_SEPARATOR, middle - sepLength / 2F, relY + 16, 0);
					this.font.draw(matrixStack, domText, middle - domw - sepLength / 2F - 1, relY + 16, 0);
					this.font.draw(matrixStack, recText, middle + sepLength / 2F + 1, relY + 16, 0);
					break;
				}
			}
			// stats genes
			int[] lineAmount = {3, 2, 2, 3, 2, 3};
			int[] lineStart = {0, 15, 25, 35, 50, 60};
			for (int i = geneIndex, lineIndex = 0; i < geneIndex + 6; i++, lineIndex++) {
				IAgriGenePair<?> pair = genes.get(i);
				// color of the gene
				Vector3f[] colors = {pair.getGene().getDominantColor(), pair.getGene().getRecessiveColor()};
				for (int j = 0; j < 2; j++) {
					int argb = ((0xFF) << 24) |
							((((int) (colors[j].x() * 255)) & 0xFF) << 16) |
							((((int) (colors[j].y() * 255)) & 0xFF) << 8) |
							((((int) (colors[j].z() * 255)) & 0xFF));
					for (int k = 0; k < lineAmount[lineIndex]; k++) {
						hLine(matrixStack, DNA_X + 9 * j, DNA_X + 9 + 8 * j, relY + 26 + lineStart[lineIndex] + k * 5, argb);
					}
				}
				// text of the gene
				Component geneText = pair.getGene().getGeneDescription();
				Component domText = pair.getDominant().getTooltip();
				Component recText = pair.getRecessive().getTooltip();
				int domw = this.font.width(domText.getString());
				this.font.draw(matrixStack, geneText, DNA_X + 36, yy, 0);
				this.font.draw(matrixStack, domText, DNA_X - domw - 1, yy, 0);
				this.font.draw(matrixStack, recText, DNA_X + 21, yy, 0);
				yy += this.font.lineHeight + 4;
			}
			// shape of the dna
			this.minecraft.getTextureManager().bindForSetup(GUI);
			this.blit(matrixStack, DNA_X, relY + 26, 186, 0, 19, 73);
		}
	}

	@Override
	protected void renderLabels(PoseStack matrixStack, int x, int y) {
		this.font.draw(matrixStack, this.title, (float) this.titleLabelX + 5, (float) this.titleLabelY, 4210752);
		this.font.draw(matrixStack, this.playerInventoryTitle, (float) this.titleLabelX + 5, (float) this.titleLabelY, 4210752);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		List<IAgriGenePair<?>> genes = menu.getGeneToRender();
		int maxIndex = genes.size() - 1;
		if (maxIndex > 6) {
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
	public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
		List<IAgriGenePair<?>> genes = menu.getGeneToRender();
		int maxIndex = genes.size() - 1;
		if (maxIndex > 6) {
			if (delta < 0) {
				if (maxIndex - geneIndex > 6) {
					geneIndex++;
				}
			} else if (delta > 0) {
				if (geneIndex > 0) {
					geneIndex--;
				}
			}
		}
		return super.mouseScrolled(mouseX, mouseY, delta);
	}

	private static boolean hoverUpButton(int startX, int startY, int mouseX, int mouseY) {
		return startX + 67 <= mouseX && mouseX <= startX + 67 + 9 && startY + 26 <= mouseY && mouseY <= startY + 26 + 9;
	}

	private static boolean hoverDownButton(int startX, int startY, int mouseX, int mouseY) {
		return startX + 67 <= mouseX && mouseX <= startX + 67 + 9 && startY + 90 <= mouseY && mouseY <= startY + 90 + 9;
	}

}
