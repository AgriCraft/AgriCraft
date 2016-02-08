package com.InfinityRaider.AgriCraft.renderers.items;

import com.InfinityRaider.AgriCraft.renderers.TessellatorV2;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import com.InfinityRaider.AgriCraft.api.v1.IAgriCraftRenderable;
import com.InfinityRaider.AgriCraft.renderers.RenderUtil;
import com.InfinityRaider.AgriCraft.renderers.renderinghacks.IItemRenderer;
import java.util.HashMap;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;

public class AgriCraftRenderableItemRenderer implements IItemRenderer {
	
	public static final AgriCraftRenderableItemRenderer DEFAULT_RENDERER = new AgriCraftRenderableItemRenderer();
	
	protected static final HashMap<IAgriCraftRenderable, AgriCraftRenderableItemRenderer> RENDERERS = new HashMap<>();

    private AgriCraftRenderableItemRenderer() {};
	
    @SuppressWarnings("deprecation")
    public final void renderItem(ItemStack stack, ItemCameraTransforms.TransformType transformType) {
        TessellatorV2 tessellator = TessellatorV2.getInstance(Tessellator.getInstance());
        switch(transformType) {
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
		GL11.glDisable(GL11.GL_LIGHTING);
		tess.scale(0.5, 0.5, 0.5);
		tess.addTranslation(-1, -.9, 0);
		tess.startDrawingQuads();
		RenderUtil.getInstance().drawScaledPrism(tess, 8, 0, 0, 8, 16, 16, ((IAgriCraftRenderable)item.getItem()).getIcon());
		tess.draw();
		tess.addTranslation(1, .9, 0);
		tess.scale(2, 2, 2);
		GL11.glEnable(GL11.GL_LIGHTING);
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

    protected void renderItemGui(TessellatorV2 tessellator, ItemStack item) {
        drawItemIcon3D(tessellator, item);
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
