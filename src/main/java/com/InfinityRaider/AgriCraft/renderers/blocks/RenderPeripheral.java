package com.InfinityRaider.AgriCraft.renderers.blocks;

import com.InfinityRaider.AgriCraft.blocks.BlockPeripheral;
import com.InfinityRaider.AgriCraft.container.ContainerSeedAnalyzer;
import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.renderers.models.ModelPeripheralProbe;
import com.InfinityRaider.AgriCraft.tileentity.peripheral.TileEntityPeripheral;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

public class RenderPeripheral extends RenderBlockBase {
    private static final ResourceLocation probeTexture = new ResourceLocation(Reference.MOD_ID.toLowerCase()+":textures/blocks/peripheralProbe.png");
    private static final ModelBase probeModel = new ModelPeripheralProbe();

    public RenderPeripheral() {
        super(Blocks.blockPeripheral, new TileEntityPeripheral(), true);
    }

    @Override
    protected boolean doWorldRender(Tessellator tessellator, IBlockAccess world, double x, double y, double z, TileEntity tile, Block block, float f, int modelId, RenderBlocks renderer, boolean callFromTESR) {
        if(callFromTESR) {
            if(tile instanceof TileEntityPeripheral) {
                TileEntityPeripheral peripheral = (TileEntityPeripheral) tile;
                drawSeed(tessellator, peripheral);
                performAnimations(tessellator, peripheral, block.getIcon(3, 0), block.colorMultiplier(world, (int) x, (int) y, (int) z));
            }
        } else {
            renderBase(tessellator, (BlockPeripheral) block, block.colorMultiplier(world, (int) x, (int) y, (int) z));
        }
        return true;
    }

    private void drawSeed(Tessellator tessellator, TileEntityPeripheral peripheral) {
        ItemStack stack = peripheral.getStackInSlot(ContainerSeedAnalyzer.seedSlotId);
        if(stack == null || stack.getItem() == null) {
            return;
        }
        IIcon icon = stack.getItem().getIconFromDamage(stack.getItemDamage());
        if(icon == null) {
            return;
        }

        float dx = 4*Constants.UNIT;
        float dy = 14*Constants.UNIT;
        float dz = 4*Constants.UNIT;
        float scale = 0.5F;
        float angle = 90.0F;

        GL11.glPushMatrix();
        GL11.glTranslated(dx, dy, dz);
        //resize the texture to half the size
        GL11.glScalef(scale, scale, scale);
        //rotate the renderer
        GL11.glRotatef(angle, 1.0F, 0.0F, 0.0F);

        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationItemsTexture);
        ItemRenderer.renderItemIn2D(tessellator, icon.getMinU(), icon.getMinV(), icon.getMaxU(), icon.getMaxV(), icon.getIconWidth(), icon.getIconHeight(), Constants.UNIT);

        GL11.glRotatef(-angle, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(1 / scale, 1 / scale, 1 / scale);
        GL11.glTranslated(-dx, -dy, -dz);
        GL11.glPopMatrix();
    }

    private void performAnimations(Tessellator tessellator, TileEntityPeripheral peripheral, IIcon icon, int cm) {
        int maxDoorPos = TileEntityPeripheral.MAX/2;
        float unit = Constants.UNIT;

        GL11.glPushMatrix();

        for (ForgeDirection dir : TileEntityPeripheral.VALID_DIRECTIONS) {
            int timer = peripheral.getTimer(dir);

            //doors
            float doorPosition = (timer >= maxDoorPos ? maxDoorPos : timer) * 4.0F / maxDoorPos;
            if (doorPosition < 4) {
                Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
                tessellator.startDrawingQuads();
                drawScaledPrism(tessellator, 4, 2, 0, 8 - doorPosition, 14, 1, icon, cm);
                drawScaledPrism(tessellator, 8 + doorPosition, 2, 0, 12, 14, 1, icon, cm);
                tessellator.draw();
            }

            //probe
            float probePosition = (timer < maxDoorPos ? 0 : timer - maxDoorPos) * 90 / maxDoorPos;
            GL11.glRotatef(180, 0, 0, 1);
            float dx = -0.5F;
            float dy = -1.5F;
            float dz = 9 * unit;
            GL11.glTranslatef(dx, dy, dz);

            float dX = 0.0F;
            float dY = 21.5F * unit;
            float dZ = -5.5F * unit;

            GL11.glTranslatef(dX, dY, dZ);
            GL11.glRotatef(probePosition, 1, 0, 0);
            GL11.glTranslatef(-dX, -dY, -dZ);

            Minecraft.getMinecraft().renderEngine.bindTexture(probeTexture);
            probeModel.render(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

            GL11.glTranslatef(dX, dY, dZ);
            GL11.glRotatef(-probePosition, 1, 0, 0);
            GL11.glTranslatef(-dX, -dY, -dZ);


            GL11.glTranslatef(-dx, -dy, -dz);
            GL11.glRotatef(-180, 0, 0, 1);

            //rotate 90° for the next render
            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            GL11.glRotatef(-90, 0, 1, 0);
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        }

        GL11.glPopMatrix();
    }

    private void renderBase(Tessellator tessellator2, BlockPeripheral blockPeripheral, int colorMultiplier) {
        IIcon iconTop = blockPeripheral.getIcon(0, 0);
        IIcon iconSide = blockPeripheral.getIcon(1, 0);
        IIcon iconBottom = blockPeripheral.getIcon(2, 0);
        IIcon iconInside = blockPeripheral.getIcon(3, 0);
        float unit = Constants.UNIT;
        //top
        drawScaledFaceFrontXZ(tessellator2, 0, 0, 16, 16, iconTop, 1, colorMultiplier);
        drawScaledFaceBackXZ(tessellator2, 0, 0, 16, 16, iconTop, 1, colorMultiplier);
        //bottom
        drawScaledFaceFrontXZ(tessellator2, 0, 0, 16, 16, iconBottom, 0, colorMultiplier);
        drawScaledFaceBackXZ(tessellator2, 0, 0, 16, 16, iconBottom, 0, colorMultiplier);
        //front
        drawScaledFaceFrontXY(tessellator2, 0, 0, 16, 16, iconSide, 0, colorMultiplier);
        drawScaledFaceBackXY(tessellator2, 0, 0, 16, 16, iconSide, 0, colorMultiplier);
        //right
        drawScaledFaceFrontYZ(tessellator2, 0, 0, 16, 16, iconSide, 1, colorMultiplier);
        drawScaledFaceBackYZ(tessellator2, 0, 0, 16, 16, iconSide, 1, colorMultiplier);
        //left
        drawScaledFaceFrontYZ(tessellator2, 0, 0, 16, 16, iconSide, 0, colorMultiplier);
        drawScaledFaceBackYZ(tessellator2, 0, 0, 16, 16, iconSide, 0, colorMultiplier);
        //back
        drawScaledFaceFrontXY(tessellator2, 0, 0, 16, 16, iconSide, 1, colorMultiplier);
        drawScaledFaceBackXY(tessellator2, 0, 0, 16, 16, iconSide, 1, colorMultiplier);
        //inside top
        drawScaledFaceFrontXZ(tessellator2, 4, 4, 12, 12, iconBottom, 12 * unit, colorMultiplier);
        //inside front
        drawScaledFaceFrontXY(tessellator2, 0, 0, 16, 16, iconInside, 4 * unit, colorMultiplier);
        drawScaledFaceBackXY(tessellator2, 0, 0, 16, 16, iconInside, 4 * unit, colorMultiplier);
        //inside right
        drawScaledFaceFrontYZ(tessellator2, 0, 0, 16, 16, iconInside, 12 * unit, colorMultiplier);
        drawScaledFaceBackYZ(tessellator2, 0, 0, 16, 16, iconInside, 12 * unit, colorMultiplier);
        //inside left
        drawScaledFaceFrontYZ(tessellator2, 0, 0, 16, 16, iconInside, 4 * unit, colorMultiplier);
        drawScaledFaceBackYZ(tessellator2, 0, 0, 16, 16, iconInside, 4 * unit, colorMultiplier);
        //inside back
        drawScaledFaceFrontXY(tessellator2, 0, 0, 16, 16, iconInside, 12 * unit, colorMultiplier);
        drawScaledFaceBackXY(tessellator2, 0, 0, 16, 16, iconInside, 12 * unit, colorMultiplier);
    }

    @Override
    protected void doInventoryRender(ItemRenderType type, ItemStack item, Object... data) {
        Tessellator tessellator = Tessellator.instance;

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_LIGHTING);
        tessellator.startDrawingQuads();

        renderBase(tessellator, (BlockPeripheral) Blocks.blockPeripheral, COLOR_MULTIPLIER_STANDARD);

        tessellator.draw();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }

    @Override
    public boolean shouldBehaveAsTESR() {
        return true;
    }

    @Override
    public boolean shouldBehaveAsISBRH() {
        return true;
    }
}
