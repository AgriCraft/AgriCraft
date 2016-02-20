package com.infinityraider.agricraft.renderers.items;

import com.infinityraider.agricraft.renderers.TessellatorV2;
import net.minecraft.item.ItemStack;
import com.infinityraider.agricraft.api.v1.IAgriCraftRenderable;
import com.infinityraider.agricraft.renderers.RenderUtil;
import com.infinityraider.agricraft.renderers.renderinghacks.IItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import org.lwjgl.opengl.GL11;

public abstract class AbstractItemRenderer implements IItemRenderer {

	@SuppressWarnings("deprecation")
	@Override
	public final void renderItem(ItemStack stack, ItemCameraTransforms.TransformType transformType) {
		TessellatorV2 tessellator = TessellatorV2.getInstance(Tessellator.getInstance());
		GL11.glDisable(GL11.GL_LIGHTING);
		tessellator.startDrawingQuads();
		switch (transformType) {
			case NONE:
				renderItemDefault(tessellator, stack);
				break;
			case THIRD_PERSON:
				renderItemThirdPerson(tessellator, stack);
				break;
			case FIRST_PERSON:
				renderItemFirstPerson(tessellator, stack);
				break;
			case HEAD:
				renderItemHead(tessellator, stack);
				break;
			case GUI:
				renderItemGui(tessellator, stack);
				break;
			case GROUND:
				renderItemGround(tessellator, stack);
				break;
			case FIXED:
				renderItemFixed(tessellator, stack);
				break;
		}
		tessellator.draw();
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	protected void renderItemDefault(TessellatorV2 tessellator, ItemStack item) {
		if (item.getItem() instanceof IAgriCraftRenderable) {
			RenderUtil.drawScaledPrism(tessellator, 0, 0, 0, 0, 16, 16, ((IAgriCraftRenderable) item.getItem()).getIcon());
		}
	}

	protected void renderItemThirdPerson(TessellatorV2 tessellator, ItemStack item) {
		renderItemDefault(tessellator, item);
	}

	protected void renderItemFirstPerson(TessellatorV2 tessellator, ItemStack item) {
		renderItemDefault(tessellator, item);
	}

	protected void renderItemHead(TessellatorV2 tessellator, ItemStack item) {
		renderItemDefault(tessellator, item);
	}

	protected void renderItemGui(TessellatorV2 tessellator, ItemStack stack) {

		// This is awesome.
		tessellator.pushMatrix();

		// The translations required are quite odd.
		// These numbers are pure magic.
		tessellator.rotate(45, 0, 1, 0);
		// The items are oddly skewed...
		tessellator.scale(1, 0.925, 0.8);
		// z is left-right, y is up-down
		tessellator.translate(0, -0.49, -0.49);

		// Render the item.
		renderItemDefault(tessellator, stack);

		tessellator.popMatrix();

	}

	protected void renderItemGround(TessellatorV2 tessellator, ItemStack item) {
		renderItemDefault(tessellator, item);
	}

	protected void renderItemFixed(TessellatorV2 tessellator, ItemStack item) {
		renderItemDefault(tessellator, item);
	}

	@Override
	public boolean shouldRender3D(ItemStack stack) {
		return false;
	}

}
