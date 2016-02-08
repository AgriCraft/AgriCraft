package com.InfinityRaider.AgriCraft.renderers.items;

import com.InfinityRaider.AgriCraft.items.ItemBase;
import com.InfinityRaider.AgriCraft.renderers.TessellatorV2;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class RenderItemIcon extends RenderItemBase {
	
    public RenderItemIcon(ItemBase item) {
        super(item);
    }

    private void drawItemIcon3D(TessellatorV2 tess, ItemStack item) {
		GL11.glDisable(GL11.GL_LIGHTING);
		tess.scale(0.5, 0.5, 0.5);
		tess.addTranslation(-1, -.9, 0);
		tess.startDrawingQuads();
		renderUtil.drawScaledPrism(tess, 8, 0, 0, 8, 16, 16, ((ItemBase)item.getItem()).getIcon(item));
		tess.draw();
		tess.addTranslation(1, .9, 0);
		tess.scale(2, 2, 2);
		GL11.glEnable(GL11.GL_LIGHTING);
    }

    @Override
    protected void renderItemDefault(TessellatorV2 tessellator, ItemStack item) {
        drawItemIcon3D(tessellator, item);
    }

    @Override
    protected void renderItemThirdPerson(TessellatorV2 tessellator, ItemStack item) {
        drawItemIcon3D(tessellator, item);
    }

    @Override
    protected void renderItemFirstPerson(TessellatorV2 tessellator, ItemStack item) {
        drawItemIcon3D(tessellator, item);

    }

    @Override
    protected void renderItemHead(TessellatorV2 tessellator, ItemStack item) {
        drawItemIcon3D(tessellator, item);
    }

    @Override
    protected void renderItemGui(TessellatorV2 tessellator, ItemStack item) {
        drawItemIcon3D(tessellator, item);
    }

    @Override
    protected void renderItemGround(TessellatorV2 tessellator, ItemStack item) {
        drawItemIcon3D(tessellator, item);
    }

    @Override
    protected void renderItemFixed(TessellatorV2 tessellator, ItemStack item) {
        drawItemIcon3D(tessellator, item);
    }

    @Override
    public boolean shouldRender3D(ItemStack stack) {
        return false;
    }
}
