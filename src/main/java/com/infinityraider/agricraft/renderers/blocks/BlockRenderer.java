package com.infinityraider.agricraft.renderers.blocks;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.blocks.blockstate.IBlockStateSpecial;
import com.infinityraider.agricraft.renderers.tessellation.ITessellator;
import com.infinityraider.agricraft.renderers.tessellation.TessellatorBakedQuad;
import com.infinityraider.agricraft.renderers.tessellation.TessellatorVertexBuffer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@SideOnly(Side.CLIENT)
public class BlockRenderer<T extends TileEntity> extends TileEntitySpecialRenderer<T> implements IModel {
    private final IBlockRenderingHandler<T> renderer;

    public BlockRenderer(IBlockRenderingHandler<T> renderer) {
        this.renderer = renderer;
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return Collections.emptyList();
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        return renderer.getAllTextures();
    }

    @Override
    public BakedModel<T> bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        return new BakedModel<>(format, renderer, bakedTextureGetter);
    }

    @Override
    public IModelState getDefaultState() {
        return TRSRTransformation.identity();
    }

    @Override
    public void renderTileEntityAt(T te, double x, double y, double z, float partialTicks, int destroyStage) {
        ITessellator tessellator = TessellatorVertexBuffer.getInstance(Tessellator.getInstance());
        World world = te.getWorld();
        BlockPos pos = te.getPos();
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        IBlockState extendedState = block.getExtendedState(state, world, pos);

        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);

        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);

        tessellator.startDrawingQuads(DefaultVertexFormats.BLOCK);
        tessellator.setColorRGBA(255, 255, 255, 255);

        this.renderer.renderWorldBlock(tessellator, world, pos, x, y, z, extendedState, block, te, true, partialTicks, destroyStage);

        tessellator.draw();

        GL11.glTranslated(-x, -y, -z);
        GL11.glPopMatrix();
    }

    public static class BakedModel<T extends TileEntity> implements IBakedModel {
        private final VertexFormat format;
        private final IBlockRenderingHandler<T> renderer;
        private final Function<ResourceLocation, TextureAtlasSprite> textures;

        private BakedModel(VertexFormat format, IBlockRenderingHandler<T> renderer, Function<ResourceLocation, TextureAtlasSprite> textures) {
            this.format = format;
            this.renderer = renderer;
            this.textures = textures;
        }

        @Override
        @SuppressWarnings("unchecked")
        public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
            List<BakedQuad> list;
            if(side == null && (state instanceof IBlockStateSpecial)) {
                World world = Minecraft.getMinecraft().theWorld;
                T tile = ((IBlockStateSpecial<T, ? extends IBlockState>) state).getTileEntity(world);
                BlockPos pos = ((IBlockStateSpecial<T, ? extends IBlockState>) state).getPos();
                Block block = state.getBlock();
                IBlockState extendedState = ((IBlockStateSpecial<T, ? extends IBlockState>) state).getWrappedState();
                ITessellator tessellator = TessellatorBakedQuad.getInstance().setTextureFunction(this.textures);

                tessellator.startDrawingQuads(this.format);

                this.renderer.renderWorldBlock(tessellator, world, pos, pos.getX(), pos.getY(), pos.getZ(), extendedState, block, tile, false, 1, 0);

                list = tessellator.getQuads();
                tessellator.draw();
            } else {
                list = ImmutableList.of();
            }
            return list;
        }

        @Override
        public boolean isAmbientOcclusion() {
            return false;
        }

        @Override
        public boolean isGui3d() {
            return renderer.doInventoryRendering();
        }

        @Override
        public boolean isBuiltInRenderer() {
            return false;
        }

        @Override
        public TextureAtlasSprite getParticleTexture() {
            return renderer.getIcon();
        }

        @Override
        public ItemCameraTransforms getItemCameraTransforms() {
            return ItemCameraTransforms.DEFAULT;
        }

        @Override
        public ItemOverrideList getOverrides() {
            return null;
        }
    }
}
