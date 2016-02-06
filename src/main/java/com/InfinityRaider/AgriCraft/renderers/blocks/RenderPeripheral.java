package com.InfinityRaider.AgriCraft.renderers.blocks;

import com.InfinityRaider.AgriCraft.blocks.BlockPeripheral;
import com.InfinityRaider.AgriCraft.container.ContainerSeedAnalyzer;
import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.renderers.RenderUtil;
import com.InfinityRaider.AgriCraft.renderers.TessellatorV2;
import com.InfinityRaider.AgriCraft.renderers.models.ModelPeripheralProbe;
import com.InfinityRaider.AgriCraft.tileentity.peripheral.TileEntityPeripheral;
import com.InfinityRaider.AgriCraft.utility.AgriForgeDirection;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

public class RenderPeripheral extends RenderBlockBase {
    private static final ResourceLocation probeTexture = new ResourceLocation(Reference.MOD_ID.toLowerCase()+":textures/blocks/peripheralProbe.png");
    private static final ModelBase probeModel = new ModelPeripheralProbe();

    public RenderPeripheral() {
        super(Blocks.blockPeripheral, new TileEntityPeripheral(), true);
    }

    @Override
    protected boolean doWorldRender(TessellatorV2 tessellator, IBlockAccess world, double x, double y, double z, BlockPos pos, Block block, IBlockState state, TileEntity tile, float partialTicks, int destroyStage, WorldRenderer renderer, boolean callFromTESR) {
        if (tile instanceof TileEntityPeripheral) {
            TileEntityPeripheral peripheral = (TileEntityPeripheral) tile;
            if (callFromTESR) {
                drawSeed(tessellator, peripheral);
                performAnimations(tessellator, peripheral, ((BlockPeripheral) block).getIcon(world, pos, state, null, peripheral), block.colorMultiplier(world, pos));
            } else {
                renderBase(tessellator, world, pos, (BlockPeripheral) block, state, peripheral, block.colorMultiplier(world, pos));
            }
        }
        return true;
    }

    private void drawSeed(TessellatorV2 tessellator, TileEntityPeripheral peripheral) {
        ItemStack stack = peripheral.getStackInSlot(ContainerSeedAnalyzer.seedSlotId);
        if(stack == null || stack.getItem() == null) {
            return;
        }

        TextureAtlasSprite icon = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite(); //TODO: find seed icon

        if(icon == null) {
            return;
        }

        float dx = 4* Constants.UNIT;
        float dy = 14* Constants.UNIT;
        float dz = 4* Constants.UNIT;
        float scale = 0.5F;
        float angle = 90.0F;

        GL11.glPushMatrix();
        GL11.glTranslated(dx, dy, dz);
        //resize the texture to half the size
        GL11.glScalef(scale, scale, scale);
        //rotate the renderer
        GL11.glRotatef(angle, 1.0F, 0.0F, 0.0F);

        //TODO: render the seed

        /*
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationItemsTexture);
        ItemRenderer.renderItemIn2D(tessellator, icon.getMinU(), icon.getMinV(), icon.getMaxU(), icon.getMaxV(), icon.getIconWidth(), icon.getIconHeight(), Constants.UNIT);
        */

        GL11.glRotatef(-angle, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(1 / scale, 1 / scale, 1 / scale);
        GL11.glTranslated(-dx, -dy, -dz);
        GL11.glPopMatrix();
    }

    private void performAnimations(TessellatorV2 tessellator, TileEntityPeripheral peripheral, TextureAtlasSprite icon, int cm) {
        int maxDoorPos = TileEntityPeripheral.MAX/2;
        float unit = Constants.UNIT;

        GL11.glPushMatrix();

        for (AgriForgeDirection dir : TileEntityPeripheral.VALID_DIRECTIONS) {
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

            //rotate 90� for the next render
            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            GL11.glRotatef(-90, 0, 1, 0);
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        }

        GL11.glPopMatrix();
    }

    private void renderBase(TessellatorV2 tessellator2, IBlockAccess world, BlockPos pos, BlockPeripheral blockPeripheral, IBlockState state, TileEntityPeripheral peripheral, int colorMultiplier) {
        TextureAtlasSprite iconTop = blockPeripheral.getIcon(world, pos, state, EnumFacing.UP, peripheral);
        TextureAtlasSprite iconSide = blockPeripheral.getIcon(world, pos, state, EnumFacing.NORTH, peripheral);
        TextureAtlasSprite iconBottom = blockPeripheral.getIcon(world, pos, state, EnumFacing.DOWN, peripheral);
        TextureAtlasSprite iconInside = blockPeripheral.getIcon(world, pos, state, null, peripheral);
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
    protected void doInventoryRender(TessellatorV2 tessellator, Block block, ItemStack item, ItemCameraTransforms.TransformType transformType) {
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_LIGHTING);
        tessellator.startDrawingQuads();

        renderBase(tessellator, Minecraft.getMinecraft().theWorld, null, (BlockPeripheral) Blocks.blockPeripheral, null, null, RenderUtil.COLOR_MULTIPLIER_STANDARD);

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
