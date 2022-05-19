package com.infinityraider.agricraft.plugins.agrigui.journal;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.content.items.IAgriJournalItem;
import com.infinityraider.agricraft.handler.JournalViewPointHandler;
import com.infinityraider.agricraft.render.items.journal.JournalClientData;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

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
	private final JournalScreenContext context_left;
	private final JournalScreenContext context_right;

	public JournalScreen(Component name, Player player, InteractionHand hand) {
		super(name);
		this.journal = player.getItemInHand(hand);
		this.journalData = new JournalClientData(player, hand);
		this.context_left = new JournalScreenContext(this,8, 7);
		this.context_right = new JournalScreenContext(this,145, 0);
	}

	@Override
	protected void init() {
		super.init();
		int renderX = (this.width - PAGE_WIDTH) / 2;
		int renderY = (this.height - PAGE_HEIGHT) / 2;
		this.buttonNextPage = this.addWidget(new PageButton(renderX + ARROW_RIGHT_X, renderY + ARROW_RIGHT_Y, false, button -> this.nextPage()));
		this.buttonPreviousPage = this.addWidget(new PageButton(renderX + ARROW_LEFT_X, renderY + ARROW_LEFT_Y, true, button -> this.previousPage()));
		this.updateButtons();
	}

	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, PAGE_BACKGROUND);
		int renderX = (this.width - PAGE_WIDTH) / 2;
		int renderY = (this.height - PAGE_HEIGHT) / 2;
		Screen.blit(matrixStack, renderX, renderY, this.getBlitOffset(), 0, 0, PAGE_WIDTH, PAGE_HEIGHT, PAGE_WIDTH, PAGE_WIDTH);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		IAgriJournalItem.IPage page = this.journalData.getCurrentPage();
		context_right.setRenderXY(renderX, renderY);
		context_left.setRenderXY(renderX, renderY);
		JournalViewPointHandler.getPageDrawer(page).drawLeftSheet(page, context_left, matrixStack, journal, (IAgriJournalItem) journal.getItem());
		JournalViewPointHandler.getPageDrawer(page).drawRightSheet(page, context_right, matrixStack, journal, (IAgriJournalItem) journal.getItem());
		JournalViewPointHandler.getPageDrawer(page).drawTooltipLeft(page, context_left, matrixStack, mouseX-renderX-8, mouseY-renderY-7);
		JournalViewPointHandler.getPageDrawer(page).drawTooltipRight(page, context_right, matrixStack, mouseX-renderX-145, mouseY-renderY);
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

		public PageButton(int x, int y, boolean isPrevious, OnPress onPress) {
			super(x, y, ARROW_WIDTH, ARROW_HEIGHT, TextComponent.EMPTY, onPress);
			this.isPrevious = isPrevious;
		}

		@Override
		public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.setShaderTexture(0, PAGE_BACKGROUND);
			int xOffset = 0;
			if (this.isHoveredOrFocused()) {
				xOffset += 18;
			}
			int yOffset = 252;
			if (this.isPrevious) {
				yOffset += 10;
			}
			blit(matrixStack, this.x, this.y, this.getBlitOffset(), xOffset, yOffset, ARROW_WIDTH, ARROW_HEIGHT, PAGE_WIDTH, PAGE_WIDTH);
		}

		@Override
		public void playDownSound(SoundManager handler) {
			handler.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
		}

	}

}
