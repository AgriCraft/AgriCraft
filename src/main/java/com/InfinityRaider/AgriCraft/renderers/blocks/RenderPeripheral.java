package com.InfinityRaider.AgriCraft.renderers.blocks;

import com.InfinityRaider.AgriCraft.blocks.BlockBase;
import com.InfinityRaider.AgriCraft.blocks.BlockPeripheral;
import com.InfinityRaider.AgriCraft.container.ContainerSeedAnalyzer;
import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.renderers.TessellatorV2;
import com.InfinityRaider.AgriCraft.renderers.models.ModelPeripheralProbe;
import com.InfinityRaider.AgriCraft.tileentity.peripheral.TileEntityPeripheral;
import com.InfinityRaider.AgriCraft.utility.ForgeDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderPeripheral extends RenderBlockBase {
    private static final ResourceLocation probeTexture = new ResourceLocation(Reference.MOD_ID.toLowerCase()+":textures/blocks/peripheralProbe.png");
    private static final ModelBase probeModel = new ModelPeripheralProbe();

    public RenderPeripheral() {
        super(Blocks.blockPeripheral, new TileEntityPeripheral(), true);
    }

    @Override
    protected boolean doWorldRender(TessellatorV2 tessellator, IBlockAccess world, double x, double y, double z, BlockPos pos, IBlockState state, BlockBase block, TileEntity tile, int modelId, float f) {
        tessellator.startDrawingQuads();
        if (tile instanceof TileEntityPeripheral) {
            TileEntityPeripheral peripheral = (TileEntityPeripheral) tile;
            drawSeed(tessellator,  peripheral);
            performAnimations(tessellator, peripheral.getTexture(state, null), peripheral, block.colorMultiplier(world, pos));
            renderBase(tessellator, world, pos, (BlockPeripheral) block, state, peripheral, block.colorMultiplier(world, pos));
            tessellator.draw();
        }
        return true;
    }

    private void drawSeed(TessellatorV2 tessellator, TileEntityPeripheral peripheral) {
        ItemStack stack = peripheral.getStackInSlot(ContainerSeedAnalyzer.seedSlotId);
        if(stack == null || stack.getItem() == null) {
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
        
        //TODO: render item
        
        GL11.glRotatef(-angle, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(1 / scale, 1 / scale, 1 / scale);
        GL11.glTranslated(-dx, -dy, -dz);
        GL11.glPopMatrix();
    }

    private void performAnimations(TessellatorV2 tessellator, TextureAtlasSprite texture, TileEntityPeripheral peripheral, int cm) {
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
                drawScaledPrism(tessellator, 4, 2, 0, 8 - doorPosition, 14, 1, cm, texture);
                drawScaledPrism(tessellator, 8 + doorPosition, 2, 0, 12, 14, 1, cm, texture);
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

    private void renderBase(TessellatorV2 tessellator2, IBlockAccess world, BlockPos pos, BlockPeripheral blockPeripheral, IBlockState state, TileEntityPeripheral peripheral, int colorMultiplier) {
        TextureAtlasSprite iconTop = blockPeripheral.getIcon(world, pos, state, EnumFacing.UP, peripheral);
        TextureAtlasSprite iconSide = blockPeripheral.getIcon(world, pos, state, EnumFacing.NORTH, peripheral);
        TextureAtlasSprite iconBottom = blockPeripheral.getIcon(world, pos, state, EnumFacing.DOWN, peripheral);
        TextureAtlasSprite iconInside = blockPeripheral.getIcon(world, pos, state, null, peripheral);
        float unit = Constants.UNIT;
        //top
        drawScaledFaceFrontXZ(tessellator2, 0, 0, 16, 16, 1, colorMultiplier, iconTop);
        drawScaledFaceBackXZ(tessellator2, 0, 0, 16, 16, 1, colorMultiplier, iconTop);
        //bottom
        drawScaledFaceFrontXZ(tessellator2, 0, 0, 16, 16, 0, colorMultiplier, iconBottom);
        drawScaledFaceBackXZ(tessellator2, 0, 0, 16, 16, 0, colorMultiplier, iconBottom);
        //front
        drawScaledFaceFrontXY(tessellator2, 0, 0, 16, 16, 0, colorMultiplier, iconSide);
        drawScaledFaceBackXY(tessellator2, 0, 0, 16, 16, 0, colorMultiplier, iconSide);
        //right
        drawScaledFaceFrontYZ(tessellator2, 0, 0, 16, 16, 1, colorMultiplier, iconSide);
        drawScaledFaceBackYZ(tessellator2, 0, 0, 16, 16, 1, colorMultiplier, iconSide);
        //left
        drawScaledFaceFrontYZ(tessellator2, 0, 0, 16, 16, 0, colorMultiplier, iconSide);
        drawScaledFaceBackYZ(tessellator2, 0, 0, 16, 16, 0, colorMultiplier, iconSide);
        //back
        drawScaledFaceFrontXY(tessellator2, 0, 0, 16, 16, 1, colorMultiplier, iconSide);
        drawScaledFaceBackXY(tessellator2, 0, 0, 16, 16, 1, colorMultiplier, iconSide);
        //inside top
        drawScaledFaceFrontXZ(tessellator2, 4, 4, 12, 12, 12 * unit, colorMultiplier, iconBottom);
        //inside front
        drawScaledFaceFrontXY(tessellator2, 0, 0, 16, 16, 4 * unit, colorMultiplier, iconInside);
        drawScaledFaceBackXY(tessellator2, 0, 0, 16, 16, 4 * unit, colorMultiplier, iconInside);
        //inside right
        drawScaledFaceFrontYZ(tessellator2, 0, 0, 16, 16, 12 * unit, colorMultiplier, iconInside);
        drawScaledFaceBackYZ(tessellator2, 0, 0, 16, 16, 12 * unit, colorMultiplier, iconInside);
        //inside left
        drawScaledFaceFrontYZ(tessellator2, 0, 0, 16, 16, 4 * unit, colorMultiplier, iconInside);
        drawScaledFaceBackYZ(tessellator2, 0, 0, 16, 16, 4 * unit, colorMultiplier, iconInside);
        //inside back
        drawScaledFaceFrontXY(tessellator2, 0, 0, 16, 16, 12 * unit, colorMultiplier, iconInside);
        drawScaledFaceBackXY(tessellator2, 0, 0, 16, 16, 12 * unit, colorMultiplier, iconInside);
    }
}
