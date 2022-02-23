package com.infinityraider.agricraft.plugins.agrigui.analyzer;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenePair;
import com.infinityraider.agricraft.impl.v1.genetics.GeneSpecies;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class SeedAnalyzerScreen extends ContainerScreen<SeedAnalyzerContainer> {

	private final ResourceLocation GUI = new ResourceLocation(AgriCraft.instance.getModId(), "textures/gui/seed_analyzer_gui.png");
	private final ITextComponent TEXT_SEPARATOR = new StringTextComponent("-");

	private int geneIndex;

	public SeedAnalyzerScreen(SeedAnalyzerContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
		this.xSize = 186;
		this.ySize = 186;
		this.playerInventoryTitleY = this.ySize - 94;
		this.geneIndex = 0;
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(GUI);
		int relX = (this.width - this.xSize) / 2;
		int relY = (this.height - this.ySize) / 2;
		// background
		this.blit(matrixStack, relX, relY, 0, 0, this.xSize, this.ySize);
		//journal slot
		this.blit(matrixStack, relX + 25, relY + 70, 186, 73, 18, 18);
		// magnifying glass
		this.blit(matrixStack, relX + 13, relY + 25, 0, 186, 56, 56);

		List<IAgriGenePair<?>> genes = container.getGeneToRender();
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
					ITextComponent domText = pair.getDominant().getTooltip();
					ITextComponent recText = pair.getRecessive().getTooltip();
					int domw = this.font.getStringWidth(domText.getString());
					int middle = relX + this.xSize / 2;
					int sepLength = this.font.getStringWidth(TEXT_SEPARATOR.getString());
					this.font.drawText(matrixStack, TEXT_SEPARATOR, middle - sepLength / 2F, relY + 16, 0);
					this.font.drawText(matrixStack, domText, middle - domw - sepLength / 2F - 1, relY + 16, 0);
					this.font.drawText(matrixStack, recText, middle + sepLength / 2F + 1, relY + 16, 0);
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
							((((int) (colors[j].getX() * 255)) & 0xFF) << 16) |
							((((int) (colors[j].getY() * 255)) & 0xFF) << 8) |
							((((int) (colors[j].getZ() * 255)) & 0xFF));
					for (int k = 0; k < lineAmount[lineIndex]; k++) {
						hLine(matrixStack, DNA_X + 9 * j, DNA_X + 9 + 8 * j, relY + 26 + lineStart[lineIndex] + k * 5, argb);
					}
				}
				// text of the gene
				ITextComponent geneText = pair.getGene().getGeneDescription();
				ITextComponent domText = pair.getDominant().getTooltip();
				ITextComponent recText = pair.getRecessive().getTooltip();
				int domw = this.font.getStringWidth(domText.getString());
				this.font.drawText(matrixStack, geneText, DNA_X + 36, yy, 0);
				this.font.drawText(matrixStack, domText, DNA_X - domw - 1, yy, 0);
				this.font.drawText(matrixStack, recText, DNA_X + 21, yy, 0);
				yy += this.font.FONT_HEIGHT + 4;
			}
			// shape of the dna
			this.minecraft.getTextureManager().bindTexture(GUI);
			this.blit(matrixStack, DNA_X, relY + 26, 186, 0, 19, 73);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
		this.font.drawText(matrixStack, this.title, (float) this.titleX + 5, (float) this.titleY, 4210752);
		this.font.drawText(matrixStack, this.playerInventory.getDisplayName(), (float) this.playerInventoryTitleX + 5, (float) this.playerInventoryTitleY, 4210752);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		List<IAgriGenePair<?>> genes = container.getGeneToRender();
		int maxIndex = genes.size() - 1;
		if (maxIndex > 6) {
			int startX = (this.width - this.xSize) / 2;
			int startY = (this.height - this.ySize) / 2;
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
		List<IAgriGenePair<?>> genes = container.getGeneToRender();
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
