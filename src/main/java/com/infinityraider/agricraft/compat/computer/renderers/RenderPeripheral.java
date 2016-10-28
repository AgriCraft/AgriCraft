package com.infinityraider.agricraft.compat.computer.renderers;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.infinityraider.agricraft.compat.computer.blocks.BlockPeripheral;
import com.infinityraider.agricraft.compat.computer.tiles.TileEntityPeripheral;
import com.infinityraider.agricraft.container.ContainerSeedAnalyzer;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.reference.Reference;
import com.infinityraider.agricraft.renderers.models.ModelPeripheralProbe;
import com.infinityraider.agricraft.utility.BaseIcons;
import com.infinityraider.infinitylib.render.block.RenderBlockWithTileBase;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;

public class RenderPeripheral extends RenderBlockWithTileBase<BlockPeripheral, TileEntityPeripheral> {
    public static final ResourceLocation TEXTURE_TOP = new ResourceLocation("agricraft:blocks/peripheralTop");
    public static final ResourceLocation TEXTURE_SIDE = new ResourceLocation("agricraft:blocks/peripheralSide");
    public static final ResourceLocation TEXTURE_BOTTOM = new ResourceLocation("agricraft:blocks/peripheralBottom");
    public static final ResourceLocation TEXTURE_INNER = new ResourceLocation("agricraft:blocks/peripheralInner");

    private static final ResourceLocation probeTexture = new ResourceLocation(Reference.MOD_ID + ":textures/blocks/peripheralProbe.png");
    private static final ModelBase probeModel = new ModelPeripheralProbe();

    public RenderPeripheral(BlockPeripheral block) {
        super(block, new TileEntityPeripheral(), true, true, true);
    }

    @Override
    public void renderInventoryBlock(ITessellator tessellator, World world, IBlockState state, BlockPeripheral block, @Nullable TileEntityPeripheral tile,
                                     ItemStack stack, EntityLivingBase entity, ItemCameraTransforms.TransformType type) {
        renderBase(tessellator);
    }

    private void drawSeed(ITessellator tessellator, TileEntityPeripheral peripheral) {
        ItemStack stack = peripheral.getStackInSlot(ContainerSeedAnalyzer.seedSlotId);
        if (stack == null || stack.getItem() == null) {
            return;
        }

        TextureAtlasSprite icon = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite(); //TODO: find seed icon

        if (icon == null) {
            return;
        }

        float dx = 4 * Constants.UNIT;
        float dy = 14 * Constants.UNIT;
        float dz = 4 * Constants.UNIT;
        float scale = 0.5F;
        float angle = 90.0F;

        GL11.glPushMatrix();
        GL11.glTranslated(dx, dy, dz);
        //resize the texture to half the size
        GL11.glScalef(scale, scale, scale);
        //rotate the renderer
        GL11.glRotatef(angle, 1.0F, 0.0F, 0.0F);

        //TODO: render the seed
        GL11.glRotatef(-angle, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(1 / scale, 1 / scale, 1 / scale);
        GL11.glTranslated(-dx, -dy, -dz);
        GL11.glPopMatrix();
    }

    private void performAnimations(ITessellator tessellator, TileEntityPeripheral peripheral, TextureAtlasSprite icon) {
        int maxDoorPos = TileEntityPeripheral.MAX / 2;
        float unit = Constants.UNIT;

        GL11.glPushMatrix();

        for (EnumFacing dir : TileEntityPeripheral.VALID_DIRECTIONS) {
            int timer = peripheral.getTimer(dir);

            //doors
            float doorPosition = (timer >= maxDoorPos ? maxDoorPos : timer) * 4.0F / maxDoorPos;
            if (doorPosition < 4) {
                Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                tessellator.drawScaledPrism(4, 2, 0, 8 - doorPosition, 14, 1, icon);
                tessellator.drawScaledPrism(8 + doorPosition, 2, 0, 12, 14, 1, icon);
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

    private void renderBase(ITessellator tessellator) {
        final TextureAtlasSprite iconTop = this.getIcon(TEXTURE_TOP);
        final TextureAtlasSprite iconSide = this.getIcon(TEXTURE_SIDE);
        final TextureAtlasSprite iconBottom = this.getIcon(TEXTURE_BOTTOM);
        final TextureAtlasSprite iconInside = this.getIcon(TEXTURE_INNER);
        float unit = Constants.UNIT;

        //top
        tessellator.drawScaledFaceDouble(0, 0, 16, 16, EnumFacing.UP, iconTop, 1);
        //bottom
        tessellator.drawScaledFaceDouble(0, 0, 16, 16, EnumFacing.DOWN, iconBottom, 0);
        //front
        tessellator.drawScaledFaceDouble(0, 0, 16, 16, EnumFacing.NORTH, iconSide, 0);
        //right
        tessellator.drawScaledFaceDouble(0, 0, 16, 16, EnumFacing.EAST, iconSide, 1);
        //left
        tessellator.drawScaledFaceDouble(0, 0, 16, 16, EnumFacing.WEST, iconSide, 0);
        //back
        tessellator.drawScaledFaceDouble(0, 0, 16, 16, EnumFacing.SOUTH, iconSide, 1);
        //inside top
        tessellator.drawScaledFace(4, 4, 12, 12, EnumFacing.UP, iconBottom, 12 * unit);
        //inside front
        tessellator.drawScaledFaceDouble(0, 0, 16, 16, EnumFacing.NORTH, iconInside, 4 * unit);
        //inside right
        tessellator.drawScaledFaceDouble(0, 0, 16, 16, EnumFacing.EAST, iconInside, 12 * unit);
        //inside left
        tessellator.drawScaledFaceDouble(0, 0, 16, 16, EnumFacing.WEST, iconInside, 4 * unit);
        //inside back
        tessellator.drawScaledFaceDouble(0, 0, 16, 16, EnumFacing.SOUTH, iconInside, 12 * unit);
    }

    @Override
    public List<ResourceLocation> getAllTextures() {
        List<ResourceLocation> list = new ArrayList<>();
        list.add(TEXTURE_TOP);
        list.add(TEXTURE_SIDE);
        list.add(TEXTURE_BOTTOM);
        list.add(TEXTURE_INNER);
        return list;
    }

    @Override
    public void renderWorldBlock(ITessellator tessellator, World world, BlockPos pos, double x, double y, double z, IBlockState state, BlockPeripheral block,
                                 @Nullable TileEntityPeripheral tile, boolean dynamicRender, float partialTick, int destroyStage) {
        if(dynamicRender) {
            drawSeed(tessellator, tile);
            performAnimations(tessellator, tile, BaseIcons.DEBUG.getIcon());
        } else {
            this.renderBase(tessellator);
        }
    }

    @Override
    public TextureAtlasSprite getIcon() {
        return null;
    }

    @Override
    public boolean applyAmbientOcclusion() {
        return false;
    }
}
