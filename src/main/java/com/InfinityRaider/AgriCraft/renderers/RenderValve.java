package com.InfinityRaider.AgriCraft.renderers;

import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityValve;
import com.InfinityRaider.AgriCraft.utility.RenderHelper;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;


public class RenderValve extends TileEntitySpecialRenderer {

    private ModelBase model = new ModelBase() {
    };

    private ModelRenderer main;
    private ModelRenderer topValve;
    private ModelRenderer botValve;
    private ModelRenderer ducts;

    public RenderValve() {
        main = new ModelRenderer(model, 0, 0);
        main.addBox(-5f, -4f, -5f, 10, 8, 10);
        main.rotationPointX = 8;
        main.rotationPointY = 8;
        main.rotationPointZ = 8;

        topValve = new ModelRenderer(model, 0, 0);
        topValve.addBox(-3f, 3.8f, -3f, 6, 4, 6);
        topValve.rotationPointX = 8;
        topValve.rotationPointY = 8;
        topValve.rotationPointZ = 8;

        botValve = new ModelRenderer(model, 0, 0);
        botValve.addBox(-3f, -7.8f, -3f, 6, 4, 6);
        botValve.rotationPointX = 8;
        botValve.rotationPointY = 8;
        botValve.rotationPointZ = 8;

        ducts = new ModelRenderer(model, 0, 0);
        ducts.addBox(-4f, -8f, -4f, 2, 16, 2);
        ducts.addBox(-4f, -8f, 2f, 2, 16, 2);
        ducts.addBox(2f, -8f, 2f, 2, 16, 2);
        ducts.addBox(2f, -8f, -4f, 2, 16, 2);
        ducts.rotationPointX = 8;
        ducts.rotationPointY = 8;
        ducts.rotationPointZ = 8;

        field_147501_a = TileEntityRendererDispatcher.instance;
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f) {
        TileEntityValve valve = (TileEntityValve) tileEntity;
        if (valve != null) {
            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ENABLE_BIT);

            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glColor3f(1, 1, 1);
            GL11.glTranslatef((float) x, (float) y, (float) z);

            bindTexture(RenderHelper.getBlockResource(valve.getIcon()));
            main.render(Constants.unit);

            bindTexture(RenderHelper.getBlockResource(Blocks.stained_hardened_clay.getIcon(0, 7)));

            if (valve.isPowered()) {
                GL11.glTranslatef(0, Constants.unit * -3, 0);
                topValve.render(Constants.unit);
                GL11.glTranslatef(0, Constants.unit * 3, 0);
            } else {
                topValve.render(Constants.unit);
            }

            if (valve.isPowered()) {
                GL11.glTranslatef(0, Constants.unit * 3, 0);
                botValve.render(Constants.unit);
                GL11.glTranslatef(0, Constants.unit * -3, 0);
            } else {
                botValve.render(Constants.unit);
            }

            bindTexture(RenderHelper.getBlockResource(Blocks.stained_hardened_clay.getIcon(0, 12)));
            ducts.render(Constants.unit);

            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }
    }
}
