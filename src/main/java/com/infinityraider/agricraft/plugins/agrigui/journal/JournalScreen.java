package com.infinityraider.agricraft.plugins.agrigui.journal;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.content.items.IAgriJournalItem;
import com.infinityraider.agricraft.handler.JournalViewPointHandler;
import com.infinityraider.agricraft.render.items.journal.JournalClientData;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class JournalScreen extends Screen {

	private static final ResourceLocation PAGE_BACKGROUND = new ResourceLocation(AgriCraft.instance.getModId(), "textures/gui/journal_background.png");
	private static final int PAGE_WIDTH = 292;
	private static final int PAGE_HEIGHT = 230;
	//where to place the arrow on the book
	private static final int ARROW_LEFT_X = 29;
	private static final int ARROW_LEFT_Y = 205;
	private static final int ARROW_RIGHT_X = 245;
	private static final int ARROW_RIGHT_Y = 205;
	private PageButton buttonNextPage;
	private PageButton buttonPreviousPage;

	private final JournalClientData journalData;
	private final ItemStack journal;
	private static final JournalScreenContext CONTEXT_RIGHT = new JournalScreenContext(145, 0);
	private static final JournalScreenContext CONTEXT_LEFT = new JournalScreenContext(8, 7);

	public JournalScreen(ITextComponent name, PlayerEntity player, Hand hand) {
		super(name);
		this.journal = player.getHeldItem(hand);
		this.journalData = new JournalClientData(player, hand);
	}

	@Override
	protected void init() {
		super.init();
		int renderX = (this.width - PAGE_WIDTH) / 2;
		int renderY = (this.height - PAGE_HEIGHT) / 2;
		this.buttonNextPage = this.addButton(new PageButton(renderX + ARROW_RIGHT_X, renderY + ARROW_RIGHT_Y, false, button -> this.nextPage()));
		this.buttonPreviousPage = this.addButton(new PageButton(renderX + ARROW_LEFT_X, renderY + ARROW_LEFT_Y, true, button -> this.previousPage()));
		this.updateButtons();
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getInstance().getTextureManager().bindTexture(PAGE_BACKGROUND);
		int renderX = (this.width - PAGE_WIDTH) / 2;
		int renderY = (this.height - PAGE_HEIGHT) / 2;
		AbstractGui.blit(matrixStack, renderX, renderY, this.getBlitOffset(), 0, 0, PAGE_WIDTH, PAGE_HEIGHT, PAGE_WIDTH, PAGE_WIDTH);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		IAgriJournalItem.IPage page = this.journalData.getCurrentPage();
		CONTEXT_RIGHT.setRenderXY(renderX, renderY);
		CONTEXT_LEFT.setRenderXY(renderX, renderY);
		JournalViewPointHandler.getPageDrawer(page).drawLeftSheet(page, CONTEXT_LEFT, matrixStack, journal, (IAgriJournalItem) journal.getItem());
		JournalViewPointHandler.getPageDrawer(page).drawRightSheet(page, CONTEXT_RIGHT, matrixStack, journal, (IAgriJournalItem) journal.getItem());
		JournalViewPointHandler.getPageDrawer(page).drawTooltipLeft(page, CONTEXT_LEFT, matrixStack, mouseX-renderX-8, mouseY-renderY-7);
		JournalViewPointHandler.getPageDrawer(page).drawTooltipRight(page, CONTEXT_RIGHT, matrixStack, mouseX-renderX-145, mouseY-renderY);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
		if (delta > 0) {
			this.previousPage();
		} else if (delta < 0) {
			this.nextPage();
		}
		return true;
	}

	private void previousPage() {
		this.journalData.decrementPage();
		this.journalData.getJournal().setCurrentPageIndex(this.journal, this.journalData.getPageIndex() - 1);
		this.updateButtons();
	}

	private void nextPage() {
		this.journalData.incrementPage();
		this.journalData.getJournal().setCurrentPageIndex(this.journal, this.journalData.getPageIndex() + 1);
		this.updateButtons();
	}

	private void updateButtons() {
		this.buttonNextPage.visible = this.journalData.getPageIndex() < this.journalData.getJournal().getPages(this.journal).size() - 1;
		this.buttonNextPage.active = this.buttonNextPage.visible;
		this.buttonPreviousPage.visible = journalData.getPageIndex() > 0;
		this.buttonPreviousPage.active = this.buttonPreviousPage.visible;
	}

	private static class PageButton extends Button {

		private static final int ARROW_WIDTH = 18;
		private static final int ARROW_HEIGHT = 10;

		private final boolean isPrevious;

		public PageButton(int x, int y, boolean isPrevious, IPressable onPress) {
			super(x, y, ARROW_WIDTH, ARROW_HEIGHT, StringTextComponent.EMPTY, onPress);
			this.isPrevious = isPrevious;
		}

		@Override
		public void renderWidget(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			Minecraft.getInstance().getTextureManager().bindTexture(PAGE_BACKGROUND);
			int xOffset = 0;
			if (this.isHovered()) {
				xOffset += 18;
			}
			int yOffset = 252;
			if (this.isPrevious) {
				yOffset += 10;
			}
			blit(matrixStack, this.x, this.y, this.getBlitOffset(), xOffset, yOffset, ARROW_WIDTH, ARROW_HEIGHT, PAGE_WIDTH, PAGE_WIDTH);
		}

		@Override
		public void playDownSound(SoundHandler handler) {
			handler.play(SimpleSound.master(SoundEvents.ITEM_BOOK_PAGE_TURN, 1.0F));
		}

	}

}
