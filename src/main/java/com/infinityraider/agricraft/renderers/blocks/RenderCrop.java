package com.infinityraider.agricraft.renderers.blocks;

import com.agricraft.agricore.util.TypeHelper;
import com.google.common.collect.Maps;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.blocks.BlockCrop;
import com.infinityraider.agricraft.reference.AgriProperties;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.tiles.TileEntityCrop;
import com.infinityraider.infinitylib.render.block.RenderBlockWithTileBase;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import java.util.List;
import java.util.Map;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderCrop extends RenderBlockWithTileBase<BlockCrop, TileEntityCrop> {

    public static final ResourceLocation TEXTURE = new ResourceLocation("agricraft:blocks/crop");

    private Map<VertexFormat, List<BakedQuad>[]> cropQuads;

    public RenderCrop(BlockCrop block) {
        super(block, new TileEntityCrop(), false, true, false);
        this.cropQuads = Maps.newIdentityHashMap();
    }

    @Override
    public List<ResourceLocation> getAllTextures() {
        return TypeHelper.asList(TEXTURE);
    }

    @Override
    public void renderWorldBlockDynamic(ITessellator tessellator, World world, BlockPos pos, double x, double y, double z,
            BlockCrop block, TileEntityCrop crop, float partialTick, int destroyStage, float alpha) {
    }

    @Override
    public void renderWorldBlockStatic(ITessellator tessellator, IBlockState state, BlockCrop block, EnumFacing side) {
        TextureAtlasSprite sprite = RenderCrop.getIcon(TEXTURE);
        this.renderBaseQuads(tessellator, side, sprite);
        if (state instanceof IExtendedBlockState) {
            IExtendedBlockState extendedState = (IExtendedBlockState) state;
            IAgriPlant plant = extendedState.getValue(AgriProperties.CROP_PLANT);
            int growthstage = extendedState.getValue(AgriProperties.GROWTH_STAGE);
            if (extendedState.getValue(AgriProperties.CROSS_CROP)) {
                tessellator.drawScaledPrism(0, 10, 2, 16, 11, 3, sprite);
                tessellator.drawScaledPrism(0, 10, 13, 16, 11, 14, sprite);
                tessellator.drawScaledPrism(2, 10, 0, 3, 11, 16, sprite);
                tessellator.drawScaledPrism(13, 10, 0, 14, 11, 16, sprite);
            }
            if (plant != null) {
                tessellator.addQuads(plant.getPlantQuads(extendedState, growthstage, side, tessellator));
            }
        }
    }

    private void renderBaseQuads(ITessellator tessellator, EnumFacing side, TextureAtlasSprite sprite) {
        int index = side == null ? EnumFacing.values().length : side.ordinal();
        boolean createQuads = false;
        if (!cropQuads.containsKey(tessellator.getVertexFormat())) {
            List<BakedQuad>[] lists = new List[EnumFacing.values().length + 1];
            cropQuads.put(tessellator.getVertexFormat(), lists);
            createQuads = true;
        } else if (cropQuads.get(tessellator.getVertexFormat())[index] == null) {
            createQuads = true;
        }
        if (createQuads) {
            tessellator.translate(0, -3 * Constants.UNIT, 0);
            tessellator.drawScaledPrism(2, 0, 2, 3, 16, 3, sprite);
            tessellator.drawScaledPrism(13, 0, 2, 14, 16, 3, sprite);
            tessellator.drawScaledPrism(13, 0, 13, 14, 16, 14, sprite);
            tessellator.drawScaledPrism(2, 0, 13, 3, 16, 14, sprite);
            tessellator.translate(0, 3 * Constants.UNIT, 0);
        } else {
            tessellator.addQuads(cropQuads.get(tessellator.getVertexFormat())[index]);
        }
    }

    @Override
    public void renderInventoryBlock(ITessellator tessellator, World world, IBlockState state, BlockCrop block, TileEntityCrop tile,
            ItemStack stack, EntityLivingBase entity, ItemCameraTransforms.TransformType type) {

    }

    @Override
    public TextureAtlasSprite getIcon() {
        return getIcon(TEXTURE);
    }

    @Override
    public boolean applyAmbientOcclusion() {
        return true;
    }

}
