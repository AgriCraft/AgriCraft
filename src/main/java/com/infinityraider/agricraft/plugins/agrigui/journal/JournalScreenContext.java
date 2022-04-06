package com.infinityraider.agricraft.plugins.agrigui.journal;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.client.IJournalDataDrawer;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public class JournalScreenContext implements IJournalDataDrawer.IPageRenderContext, IRenderUtilities {

	private static final int PAGE_WIDTH = 125;
	private static final int PAGE_HEIGHT = 180;
	private static final ResourceLocation FONT = new ResourceLocation(AgriCraft.instance.getModId(), "unicode_font");

	private final JournalScreen screen;
	private final int baseX;
	private final int baseY;
	private int renderX;
	private int renderY;

	public JournalScreenContext(JournalScreen screen, int baseX, int baseY) {
		this.screen = screen;
		this.baseX = baseX;
		this.baseY = baseY;
	}
	public void setRenderXY(int x, int y) {
		this.renderX = x;
		this.renderY = y;
	}

	@Override
	public void draw(PoseStack transforms, TextureAtlasSprite texture, float x, float y, float w, float h, float r, float g, float b, float a) {
		this.bindTextureAtlas();
		this.draw(transforms, x, y, w, h, texture.getU0(), texture.getV0(), texture.getU1(), texture.getV1(), r, g, b, a);
	}

	@Override
	public TextureAtlasSprite getSprite(ResourceLocation texture) {
		return IRenderUtilities.super.getSprite(texture);
	}

	@Override
	public int getPageWidth() {
		return PAGE_WIDTH;
	}

	@Override
	public int getPageHeight() {
		return PAGE_HEIGHT;
	}

	@Override
	public void drawFullPageTexture(PoseStack transforms, ResourceLocation texture) {
		this.draw(transforms, texture, 8, 25, 128, 192);
	}

	@Override
	public void draw(PoseStack transforms, ResourceLocation texture, float x, float y, float w, float h, float u1, float v1, float u2, float v2) {
		this.bindTexture(texture);
		this.draw(transforms, x, y, w, h, u1, v1, u2, v2, 1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void draw(PoseStack transforms, float x, float y, float w, float h, float u1, float v1, float u2, float v2, float r, float g, float b, float a) {
		this.drawColored(transforms, x, x + w, y, y + h, u1, u2, v1, v2, r, g, b, a);
	}

	@Override
	public float drawText(PoseStack matrixStack, Component text, float x, float y, float scale) {
		scale+=0.3F;
		matrixStack.pushPose();
		Font fontRenderer = this.getFontRenderer();
		matrixStack.scale(scale, scale, scale);
		float[] dy = {0.0F};
		float finalScale = scale;
		fontRenderer.split(text.copy().withStyle(Style.EMPTY.withFont(FONT)), (int) (PAGE_WIDTH/scale)).forEach(line -> {
			fontRenderer.draw(matrixStack, line, (baseX+renderX + x)/ finalScale, (baseY+renderY+y + dy[0]) / finalScale, 0);
			dy[0] +=6.2F;
		});
		matrixStack.popPose();
		return dy[0]*scale;
	}

	@Override
	public void drawItem(PoseStack transforms, ItemStack item, float x, float y) {
		Minecraft.getInstance().getItemRenderer().renderGuiItem(item, (int) x+baseX+renderX, (int) y+baseY+renderY);
	}

	@Override
	public void drawTooltip(PoseStack transforms, List<Component> textLines, float x, float y) {
		this.screen.renderTooltip(transforms, textLines, Optional.empty(), (int) (x+baseX+renderX), (int) y+baseY+renderY, this.getFontRenderer());
	}

	private void drawColored(PoseStack matrixStack, float x1, float x2, float y1, float y2, float minU, float maxU, float minV, float maxV, float r, float g, float b, float a) {
		x1+=baseX + renderX;
		x2+=baseX + renderX;
		y1+=baseY + renderY;
		y2+=baseY + renderY;
		BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
		Matrix4f matrix4f = matrixStack.last().pose();
		bufferbuilder.vertex(matrix4f, x1, y2, 5).uv(minU, maxV).color(r, g, b, a).endVertex();
		bufferbuilder.vertex(matrix4f, x2, y2, 5).uv(maxU, maxV).color(r, g, b, a).endVertex();
		bufferbuilder.vertex(matrix4f, x2, y1, 5).uv(maxU, minV).color(r, g, b, a).endVertex();
		bufferbuilder.vertex(matrix4f, x1, y1, 5).uv(minU, minV).color(r, g, b, a).endVertex();
		bufferbuilder.end();
		BufferUploader.end(bufferbuilder);
	}

}
