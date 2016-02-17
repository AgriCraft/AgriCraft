package com.infinityraider.agricraft.renderers.items;

import com.infinityraider.agricraft.renderers.TessellatorV2;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import com.infinityraider.agricraft.api.v1.IAgriCraftRenderable;
import com.infinityraider.agricraft.renderers.RenderUtil;
import com.infinityraider.agricraft.renderers.renderinghacks.IItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;

public abstract class AbstractItemRenderer implements IItemRenderer {

	@SuppressWarnings("deprecation")
	public final void renderItem(ItemStack stack, ItemCameraTransforms.TransformType transformType) {
		TessellatorV2 tessellator = TessellatorV2.getInstance(Tessellator.getInstance());
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
	}

	private void drawItemIcon3D(TessellatorV2 tess, ItemStack item) {
		if (item == null || !(item.getItem() instanceof IAgriCraftRenderable)) {
			return;
		}
		tess.startDrawingQuads();
		RenderUtil.drawScaledPrism(tess, 0, 0, 0, 0, 16, 16, ((IAgriCraftRenderable) item.getItem()).getIcon());
		tess.draw();
	}

	protected void renderItemDefault(TessellatorV2 tessellator, ItemStack item) {
		drawItemIcon3D(tessellator, item);
	}

	protected void renderItemThirdPerson(TessellatorV2 tessellator, ItemStack item) {
		drawItemIcon3D(tessellator, item);
	}

	protected void renderItemFirstPerson(TessellatorV2 tessellator, ItemStack item) {
		drawItemIcon3D(tessellator, item);

	}

	protected void renderItemHead(TessellatorV2 tessellator, ItemStack item) {
		drawItemIcon3D(tessellator, item);
	}

	protected void renderItemGui(TessellatorV2 tessellator, ItemStack stack) {
		
		// This is awesome.
		tessellator.pushMatrix();
		GL11.glDisable(GL11.GL_LIGHTING);
		
		// The translations required are quite odd.
		// These numbers are pure magic.
		tessellator.rotate(45, 0, 1, 0);
		// The items are oddly skewed...
		tessellator.scale(1, 0.925, 0.8);
		// z is left-right, y is up-down
		tessellator.translate(0, -0.49, -0.49);
		
		// Render the item.
		renderItemDefault(tessellator, stack);
		
		GL11.glEnable(GL11.GL_LIGHTING);
		tessellator.popMatrix();

	}

	protected void renderItemGround(TessellatorV2 tessellator, ItemStack item) {
		drawItemIcon3D(tessellator, item);
	}

	protected void renderItemFixed(TessellatorV2 tessellator, ItemStack item) {
		drawItemIcon3D(tessellator, item);
	}

	@Override
	public boolean shouldRender3D(ItemStack stack) {
		return true;
	}

}
