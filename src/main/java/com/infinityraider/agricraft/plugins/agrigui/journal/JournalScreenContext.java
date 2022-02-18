package com.infinityraider.agricraft.plugins.agrigui.journal;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.client.IJournalDataDrawer;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;

public class JournalScreenContext implements IJournalDataDrawer.IPageRenderContext, IRenderUtilities {

	private static final int PAGE_WIDTH = 125;
	private static final int PAGE_HEIGHT = 180;
	private static final ResourceLocation FONT = new ResourceLocation(AgriCraft.instance.getModId(), "unicode_font");

	private final int baseX;
	private final int baseY;
	private int renderX;
	private int renderY;

	public JournalScreenContext(int baseX, int baseY) {
		this.baseX = baseX;
		this.baseY = baseY;
	}
	public void setRenderXY(int x, int y) {
		this.renderX = x;
		this.renderY = y;
	}

	@Override
	public void draw(MatrixStack transforms, TextureAtlasSprite texture, float x, float y, float w, float h, float r, float g, float b, float a) {
		this.bindTextureAtlas();
		this.draw(transforms, x, y, w, h, texture.getMinU(), texture.getMinV(), texture.getMaxU(), texture.getMaxV(), r, g, b, a);
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
	public void drawFullPageTexture(MatrixStack transforms, ResourceLocation texture) {
		this.draw(transforms, texture, 8, 25, 128, 192);
	}

	@Override
	public void draw(MatrixStack transforms, ResourceLocation texture, float x, float y, float w, float h, float u1, float v1, float u2, float v2) {
		this.bindTexture(texture);
		this.draw(transforms, x, y, w, h, u1, v1, u2, v2, 1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void draw(MatrixStack transforms, float x, float y, float w, float h, float u1, float v1, float u2, float v2, float r, float g, float b, float a) {
		this.drawColored(transforms, x, x + w, y, y + h, u1, u2, v1, v2, r, g, b, a);
	}

	@Override
	public float drawText(MatrixStack matrixStack, ITextComponent text, float x, float y, float scale) {
		scale+=0.3F;
		matrixStack.push();
		FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
		matrixStack.scale(scale, scale, scale);
		float[] dy = {0.0F};
		float finalScale = scale;
		fontRenderer.trimStringToWidth(text.deepCopy().mergeStyle(Style.EMPTY.setFontId(FONT)), (int) (PAGE_WIDTH/scale)).forEach(line -> {
			fontRenderer.func_238422_b_(matrixStack, line, (baseX+renderX + x)/ finalScale, (baseY+renderY+y + dy[0]) / finalScale, 0);
			dy[0] +=6.2F;
		});
		matrixStack.pop();
		return dy[0]*scale;
	}

	@Override
	public void drawItem(MatrixStack transforms, ItemStack item, float x, float y) {
		Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(item, (int) x+baseX+renderX, (int) y+baseY+renderY);
	}

	private void drawColored(MatrixStack matrixStack, float x1, float x2, float y1, float y2, float minU, float maxU, float minV, float maxV, float r, float g, float b, float a) {
		x1+=baseX + renderX;
		x2+=baseX + renderX;
		y1+=baseY + renderY;
		y2+=baseY + renderY;
		BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		Matrix4f matrix4f = matrixStack.getLast().getMatrix();
		bufferbuilder.pos(matrix4f, x1, y2, 5).tex(minU, maxV).color(r, g, b, a).endVertex();
		bufferbuilder.pos(matrix4f, x2, y2, 5).tex(maxU, maxV).color(r, g, b, a).endVertex();
		bufferbuilder.pos(matrix4f, x2, y1, 5).tex(maxU, minV).color(r, g, b, a).endVertex();
		bufferbuilder.pos(matrix4f, x1, y1, 5).tex(minU, minV).color(r, g, b, a).endVertex();
		bufferbuilder.finishDrawing();
		RenderSystem.enableAlphaTest();
		WorldVertexBufferUploader.draw(bufferbuilder);
	}

}
