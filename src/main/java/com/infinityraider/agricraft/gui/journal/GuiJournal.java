package com.infinityraider.agricraft.gui.journal;

import com.infinityraider.agricraft.items.ItemJournal;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import com.infinityraider.agricraft.api.plant.IAgriPlant;
import com.infinityraider.agricraft.gui.GuiBase;
import com.infinityraider.agricraft.gui.component.IComponent;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiJournal extends GuiBase {

	/**
	 * Some dimensions and constants
	 */
	private static final int MINIMUM_PAGES = 2;

	private final int xSize;
	private final int ySize;

	private int guiLeft;
	private int guiTop;

	/**
	 * Current page
	 */
	private int currentPageNumber;
	private JournalPage currentPage;

	/**
	 * Stuff to render
	 */
	final List<IComponent> components = new ArrayList<>();

	private final ItemStack journal;

	public GuiJournal(ItemStack journal) {
		super();
		this.journal = journal;
		int pageWidth = 128;
		this.xSize = pageWidth * 2;
		this.ySize = pageWidth * 3 / 2;
	}

	@Override
	public void initGui() {
		//half of the screen size minus the gui size to centre the gui, the -16 is to ignore the players item bar
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - 16 - this.ySize) / 2;
		currentPage = getCurrentPage();
		this.components.clear();
		currentPage.addComponents(components);
	}

	@Override
	public int getAnchorX() {
		return this.guiLeft;
	}

	@Override
	public int getAnchorY() {
		return this.guiTop;
	}

	@Override
	public void drawScreen(int x, int y, float opacity) {
		//draw background
		drawBackground(0);
		//draw foreground
		drawTexture(currentPage.getForeground());
		//draw navigation arrows
		drawNavigationArrows(x, y);
		//draw tooltip
		final int mouseX = x - this.guiLeft;
		final int mouseY = y - this.guiTop;
		List<String> toolTip = new ArrayList<>();
		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();
		GlStateManager.translate(this.guiLeft, this.guiTop, 0);
		components.stream()
				// Render Components
				.peek(c -> c.renderComponent(this))
				// Filter ToolTips
				.filter(c -> c.isOverComponent(mouseX, mouseY))
				// Add ToolTips
				.forEach(c -> c.addToolTip(toolTip, Minecraft.getMinecraft().thePlayer));
		currentPage.addTooltip(mouseX, mouseY, toolTip);
		GlStateManager.popAttrib();
		this.drawTooltip(toolTip, mouseX, mouseY);
		GlStateManager.popMatrix();
	}

	@Override
	public void mouseClicked(int x, int y, int rightClick) {
		//find number of pages to browse
		int pageIncrement = 0;
		//clicked for next page or previous page
		if (y > this.guiTop + 172 && y <= this.guiTop + 172 + 16 && rightClick == 0) {
			if (x > this.guiLeft + 221 && x <= this.guiLeft + 221 + 16) {
				//next page
				pageIncrement = 1;
			} else if (x > this.guiLeft + 19 && x <= this.guiLeft + 19 + 16 && this.currentPageNumber > 0) {
				//prev page
				pageIncrement = -1;
			}
			//clicked to browse from within the page
		} else {
			pageIncrement = getCurrentPage().getPagesToBrowseOnMouseClick(x - this.guiLeft, y - this.guiTop);
		}
		//go to new page
		int newPage = currentPageNumber + pageIncrement;
		newPage = Math.max(0, newPage); //don't go negative
		newPage = Math.min(newPage, getNumberOfPages() - 1); //don't go outside array bounds
		if (newPage != currentPageNumber) {
			this.currentPageNumber = newPage;
			this.initGui();
		}
	}

	private JournalPage getCurrentPage() {
		switch (currentPageNumber) {
			case 0:
				return new JournalPageTitle();
			case 1:
				return new JournalPageIntroduction();
		}
		return new JournalPageSeed(getDiscoveredSeeds(), currentPageNumber - MINIMUM_PAGES);
	}

	private List<IAgriPlant> getDiscoveredSeeds() {
		if (journal != null && journal.getItem() instanceof ItemJournal) {
			return ((ItemJournal) journal.getItem()).getDiscoveredSeeds(journal);
		} else {
			return new ArrayList<>();
		}
	}

	private int getNumberOfPages() {
		return MINIMUM_PAGES + getDiscoveredSeeds().size();
	}

	@Override
	public void drawBackground(int i) {
		this.drawTexture(JournalPage.getBackground());
	}

	private void drawTexture(ResourceLocation texture) {
		GlStateManager.pushAttrib();
		GlStateManager.color(1, 1, 1, 1);
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
		GlStateManager.popAttrib();
	}

	private void drawNavigationArrows(int x, int y) {
		GlStateManager.pushAttrib();
		if (y > this.guiTop + 172 && y <= this.guiTop + 172 + 16) {
			if (x > this.guiLeft + 221 && x <= this.guiLeft + 221 + 16) {
				Minecraft.getMinecraft().getTextureManager().bindTexture(JournalPage.getBackground());
				drawTexturedModalRect(this.guiLeft + 223, this.guiTop + 178, 224, 239, 32, 17);
			} else if (x > this.guiLeft + 19 && x <= this.guiLeft + 19 + 16 && this.currentPageNumber > 0) {
				Minecraft.getMinecraft().getTextureManager().bindTexture(JournalPage.getBackground());
				drawTexturedModalRect(this.guiLeft + 1, this.guiTop + 178, 0, 239, 32, 17);
			}
		}
		GlStateManager.popAttrib();
	}

	private void drawTooltip(List<String> toolTip, int x, int y) {
		drawHoveringText(toolTip, x, y, fontRendererObj);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

}
