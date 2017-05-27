package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.blocks.BlockWaterPad;
import com.infinityraider.agricraft.reference.AgriProperties;
import com.infinityraider.agricraft.utility.BaseIcons;
import com.infinityraider.infinitylib.block.blockstate.SidedConnection;
import com.infinityraider.infinitylib.render.block.RenderBlockBase;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import java.util.Collections;
import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderWaterPad extends RenderBlockBase<BlockWaterPad> {

    private static final SidedConnection DEFAULT = new SidedConnection();

    public RenderWaterPad(BlockWaterPad block) {
        super(block, true);
    }

    @Override
    public List<ResourceLocation> getAllTextures() {
        return Collections.emptyList();
    }

    @Override
    public void renderWorldBlockStatic(ITessellator tessellator, IBlockState state, BlockWaterPad block, EnumFacing side) {
        // Icon
        final TextureAtlasSprite matIcon = BaseIcons.DIRT.getIcon();
        final TextureAtlasSprite waterIcon = BaseIcons.WATER_STILL.getIcon();

        // Check Full
        SidedConnection connection = state instanceof IExtendedBlockState ? ((IExtendedBlockState) state).getValue(AgriProperties.CONNECTIONS) : DEFAULT;

        // Draw Base
        renderBase(tessellator, matIcon);

        // Render Sides
        for (EnumFacing dir : EnumFacing.HORIZONTALS) {
            if (!connection.isConnected(dir)) {
                renderSide(tessellator, dir, matIcon);
            }
        }

        // Render Water
        if (AgriProperties.POWERED.getValue(state)) {
            renderWater(tessellator, waterIcon);
        }
    }

    @Override
    public void renderInventoryBlock(ITessellator tess, World world, IBlockState state, BlockWaterPad block,
            ItemStack stack, EntityLivingBase entity, ItemCameraTransforms.TransformType type) {

        // Icons
        final TextureAtlasSprite matIcon = BaseIcons.DIRT.getIcon();
        final TextureAtlasSprite waterIcon = BaseIcons.WATER_STILL.getIcon();

        // Draw Base
        renderBase(tess, matIcon);

        // Draw Sides
        for (EnumFacing dir : EnumFacing.HORIZONTALS) {
            renderSide(tess, dir, matIcon);
        }

        // Full
        if (AgriProperties.POWERED.getValue(state)) {
            renderWater(tess, waterIcon);
        }
    }

    private static void renderWater(ITessellator tess, TextureAtlasSprite waterIcon) {
        //draw central water levels
        tess.drawScaledFaceDouble(0, 0, 16, 16, EnumFacing.UP, waterIcon, 15);
    }

    private static void renderBase(ITessellator tess, TextureAtlasSprite matIcon) {
        tess.drawScaledPrism(0, 0, 0, 16, 8, 16, matIcon);
    }

    private static void renderSide(ITessellator tess, EnumFacing side, TextureAtlasSprite matIcon) {
        //data about side to render
        boolean xAxis = side.getAxis() == EnumFacing.Axis.X;
        int index = xAxis ? side.getFrontOffsetX() : side.getFrontOffsetZ();
        int min = index < 0 ? 0 : 15;
        int max = index < 0 ? 1 : 16;

        //render upper face
        tess.drawScaledFace(xAxis ? min : 0, xAxis ? 0 : min, xAxis ? max : 16, xAxis ? 16 : max, EnumFacing.UP, matIcon, 16);

        //render side
        tess.drawScaledFace(0, 8, 16, 16, side, matIcon, index > 0 ? 16 : 0);
        tess.drawScaledFace(0, 8, 16, 16, side.getOpposite(), matIcon, index > 0 ? 15 : 1);
    }

    @Override
    public TextureAtlasSprite getIcon() {
        return BaseIcons.DIRT.getIcon();
    }

    @Override
    public boolean applyAmbientOcclusion() {
        return true;
    }
}
