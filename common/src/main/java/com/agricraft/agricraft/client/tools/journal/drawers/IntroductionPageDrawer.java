package com.agricraft.agricraft.client.tools.journal.drawers;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.tools.journal.JournalData;
import com.agricraft.agricraft.api.tools.journal.JournalPageDrawer;
import com.agricraft.agricraft.common.item.journal.IntroductionPage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.IdMap;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class IntroductionPageDrawer implements JournalPageDrawer<IntroductionPage> {

	private static final Component INTRODUCTION = Component.translatable("agricraft.journal.introduction");
	private static final Component PARAGRAPH_1 = Component.translatable("agricraft.journal.introduction.paragraph_1");
	private static final Component PARAGRAPH_2 = Component.translatable("agricraft.journal.introduction.paragraph_2");
	private static final Component PARAGRAPH_3 = Component.translatable("agricraft.journal.introduction.paragraph_3");
	private static final Component ALPHA = Component.translatable("agricraft.journal.introduction.alpha").withStyle(ChatFormatting.DARK_GRAY);
	private static final Component ALPHA_NOTICE = Component.translatable("agricraft.journal.introduction.alpha.notice").withStyle(ChatFormatting.DARK_GRAY);
	private static final Component ALPHA_FEATURES = Component.translatable("agricraft.journal.introduction.alpha.features").withStyle(ChatFormatting.DARK_GRAY);
	private static final Component ALPHA_FEATURES_LIST = Component.translatable("agricraft.journal.introduction.alpha.features.list").withStyle(ChatFormatting.DARK_GRAY);
	private static final Component ALPHA_NOT_FEATURES = Component.translatable("agricraft.journal.introduction.alpha.not_features").withStyle(ChatFormatting.DARK_GRAY);
	private static final Component ALPHA_NOT_FEATURES_LIST = Component.translatable("agricraft.journal.introduction.alpha.not_features.list").withStyle(ChatFormatting.DARK_GRAY);
	private static final MutableComponent DISCOVERED = Component.translatable("agricraft.journal.introduction.discovered");

	@Override
	public void drawLeftSheet(GuiGraphics guiGraphics, IntroductionPage page, int pageX, int pageY, JournalData journalData) {
		// alpha/beta notice
		// this will be removed when the mod is out of alpha/beta
		float dy = pageY + 15;
		float dx = pageX + 6;
		dy += this.drawScaledText(guiGraphics, ALPHA, dx, dy, 0.90F);
		dy += 4;
		dy += this.drawScaledText(guiGraphics, ALPHA_NOTICE, dx, dy, 0.70F);
		dy += 8;
		dy += this.drawScaledText(guiGraphics, ALPHA_FEATURES, dx, dy, 0.80F);
		dy += 4;
		dy += this.drawScaledText(guiGraphics, ALPHA_FEATURES_LIST, dx, dy, 0.70F);
		dy += 8;
		dy += this.drawScaledText(guiGraphics, ALPHA_NOT_FEATURES, dx, dy, 0.80F);
		dy += 4;
		this.drawScaledText(guiGraphics, ALPHA_NOT_FEATURES_LIST, dx, dy, 0.70F);
	}

	@Override
	public void drawRightSheet(GuiGraphics guiGraphics, IntroductionPage page, int pageX, int pageY, JournalData journalData) {
		Font font = Minecraft.getInstance().font;
		float dy = pageY + 10;
		float dx = pageX + 6;
		float spacing = 4;
		// Title
		guiGraphics.drawString(font, INTRODUCTION, (int) dx, (int) dy, 0, false);
		dy += font.lineHeight;
		dy += spacing;
		// First paragraph
		dy += this.drawScaledText(guiGraphics, PARAGRAPH_1, dx, dy, 0.70F);
		dy += spacing;
		// Second paragraph
		dy += this.drawScaledText(guiGraphics, PARAGRAPH_2, dx, dy, 0.70F);
		dy += spacing;
		// Third paragraph
		dy += this.drawScaledText(guiGraphics, PARAGRAPH_3, dx, dy, 0.70F);
		dy += spacing;
		dy += spacing;
		// Final paragraph:
		this.drawScaledText(guiGraphics, DISCOVERED.plainCopy().append(": " + journalData.getDiscoveredSeeds().size() + " / " + AgriApi.getPlantRegistry().map(IdMap::size).orElse(0)), dx, dy, 0.70F);
	}

}
