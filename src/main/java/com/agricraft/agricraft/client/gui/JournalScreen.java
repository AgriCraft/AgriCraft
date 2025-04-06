package com.agricraft.agricraft.client.gui;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.AgriClientApi;
import com.agricraft.agricraft.api.tools.journal.JournalData;
import com.agricraft.agricraft.api.tools.journal.JournalPage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

public class JournalScreen extends Screen {

	private static final ResourceLocation PAGE_BACKGROUND = ResourceLocation.fromNamespaceAndPath(AgriApi.MOD_ID, "textures/gui/journal/background.png");
	private static final int PAGE_WIDTH = 292;
	private static final int PAGE_HEIGHT = 230;
	private static final int OFFSET_LEFT_PAGE = 8;
	private static final int OFFSET_RIGHT_PAGE = 145;
	//where to place the arrow on the book
	private static final int ARROW_LEFT_X = 29;
	private static final int ARROW_LEFT_Y = 205;
	private static final int ARROW_RIGHT_X = 245;
	private static final int ARROW_RIGHT_Y = 205;
	private final JournalData journalData;
	private PageButton buttonNextPage;
	private PageButton buttonPreviousPage;
	private int index;

	public JournalScreen(JournalData journalData) {
		super(Component.translatable("screen.agricraft.journal"));
		this.journalData = journalData;
		this.index = 0;
	}

	@Override
	protected void init() {
		super.init();
		int renderX = (this.width - PAGE_WIDTH) / 2;
		int renderY = (this.height - PAGE_HEIGHT) / 2;
		this.buttonNextPage = this.addRenderableWidget(new PageButton(renderX + ARROW_RIGHT_X, renderY + ARROW_RIGHT_Y, false, button -> this.nextPage()));
		this.buttonPreviousPage = this.addRenderableWidget(new PageButton(renderX + ARROW_LEFT_X, renderY + ARROW_LEFT_Y, true, button -> this.previousPage()));
		this.updateButtons();
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		super.render(guiGraphics, mouseX, mouseY, partialTick);
		int journalX = (this.width - PAGE_WIDTH) / 2;
		int journalY = (this.height - PAGE_HEIGHT) / 2;
//		Lighting.setupForFlatItems();
		JournalPage page = this.journalData.getPage(this.index);
		AgriClientApi.get().getJournalPageDrawer(page).drawLeftSheet(guiGraphics, page, journalX + OFFSET_LEFT_PAGE, journalY, this.journalData);
		AgriClientApi.get().getJournalPageDrawer(page).drawRightSheet(guiGraphics, page, journalX + OFFSET_RIGHT_PAGE, journalY, this.journalData);
		AgriClientApi.get().getJournalPageDrawer(page).drawLeftTooltip(guiGraphics, page, journalX + OFFSET_LEFT_PAGE, journalY, mouseX - journalX - OFFSET_LEFT_PAGE, mouseY - journalY);
		AgriClientApi.get().getJournalPageDrawer(page).drawRightTooltip(guiGraphics, page, journalX + OFFSET_RIGHT_PAGE, journalY, mouseX - journalX - OFFSET_RIGHT_PAGE, mouseY - journalY);
	}

	@Override
	public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
		int journalX = (this.width - PAGE_WIDTH) / 2;
		int journalY = (this.height - PAGE_HEIGHT) / 2;
		guiGraphics.blit(PAGE_BACKGROUND, journalX, journalY, 0, 0, PAGE_WIDTH, PAGE_HEIGHT, 292, 292);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		if (scrollY > 0) {
			this.previousPage();
		} else if (scrollY < 0) {
			this.nextPage();
		}
		return true;
	}

	private void previousPage() {
		if (this.index > 0) {
			this.index--;
		}
		this.updateButtons();
	}

	private void nextPage() {
		if (this.index < this.journalData.size() - 1) {
			this.index++;
		}
		this.updateButtons();
	}

	private void updateButtons() {
		this.buttonNextPage.visible = this.index < this.journalData.size() - 1;
		this.buttonNextPage.active = this.buttonNextPage.visible;
		this.buttonPreviousPage.visible = this.index > 0;
		this.buttonPreviousPage.active = this.buttonPreviousPage.visible;
	}

	private static class PageButton extends Button {

		private static final int ARROW_WIDTH = 18;
		private static final int ARROW_HEIGHT = 10;

		private final boolean isPrevious;

		public PageButton(int x, int y, boolean isPrevious, OnPress onPress) {
			super(x, y, ARROW_WIDTH, ARROW_HEIGHT, Component.empty(), onPress, DEFAULT_NARRATION);
			this.isPrevious = isPrevious;
		}


		@Override
		public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
			int xOffset = 0;
			if (this.isHoveredOrFocused()) {
				xOffset += 18;
			}
			int yOffset = 252;
			if (this.isPrevious) {
				yOffset += 10;
			}
			guiGraphics.blit(PAGE_BACKGROUND, this.getX(), this.getY(), xOffset, yOffset, ARROW_WIDTH, ARROW_HEIGHT, PAGE_WIDTH, PAGE_WIDTH);
		}

		@Override
		public void playDownSound(SoundManager handler) {
			handler.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
		}

	}

}
