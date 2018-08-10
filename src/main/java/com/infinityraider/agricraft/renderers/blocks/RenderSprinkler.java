package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.blocks.irrigation.BlockSprinkler;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.tiles.irrigation.TileEntitySprinkler;
import com.infinityraider.agricraft.utility.BaseIcons;
import com.infinityraider.agricraft.utility.CustomWoodType;
import com.infinityraider.agricraft.utility.CustomWoodTypeRegistry;
import com.infinityraider.infinitylib.render.block.RenderBlockWithTileBase;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderSprinkler extends RenderBlockWithTileBase<BlockSprinkler, TileEntitySprinkler> {

    // Dimensions
    private static final float MIN_Y = 8.0F;
    private static final float MAX_Y = 12.0F;
    private static final float MIN_C = 7.0F;
    private static final float MAX_C = 9.0F;
    private static final float BLADE_W = 1.0F;
    private static final float BLADE_L = 3.0F;

    // Calculated
    private static final float BMX_Y = MIN_Y + BLADE_W;
    private static final float BMX_A = MIN_C - BLADE_L;
    private static final float BMX_B = MAX_C + BLADE_L;

    public RenderSprinkler(BlockSprinkler block) {
        super(block, new TileEntitySprinkler(), true, true, true);
    }

    @Override
    public void renderInventoryBlock(ITessellator tess, World world, IBlockState state, BlockSprinkler block, TileEntitySprinkler tile, ItemStack stack, EntityLivingBase entity, ItemCameraTransforms.TransformType type) {
        // Save translation matrix.
        tess.pushMatrix();
        
        // Translate to connect to above channel.
        tess.translate(0, -4 * Constants.UNIT, 0);
        
        // Render parts.
        this.renderConnector(tess, tile.getChannelIcon());
        this.renderHead(tess, 0, tile.getHeadIcon());
        
        // Reset matrix.
        tess.popMatrix();
    }

    @Override
    public List<ResourceLocation> getAllTextures() {
        return Collections.emptyList();
    }

    @Override
    public void renderWorldBlockStatic(ITessellator tessellator, IBlockState state, BlockSprinkler block, EnumFacing side) {
        if (state instanceof IExtendedBlockState) {
            // Resolve the wood type.
            final CustomWoodType type = CustomWoodTypeRegistry.getFromState(state).orElse(CustomWoodTypeRegistry.DEFAULT);
            // Render the connector.
            this.renderConnector(tessellator, type.getIcon());
        }
    }

    @Override
    public void renderWorldBlockDynamic(ITessellator tess, World world, BlockPos pos, double x, double y, double z, BlockSprinkler block, TileEntitySprinkler tile, float partialTick, int destroyStage, float alpha) {
        // All we have to do here is render the sprinkler head.
        this.renderHead(tess, tile.getAngle(), BaseIcons.IRON_BLOCK.getIcon());
    }

    public void renderConnector(ITessellator tess, TextureAtlasSprite material) {
        // Save translation matrix.
        tess.pushMatrix();
        
        // Translate to connect to above channel.
        tess.translate(0, 4 * Constants.UNIT, 0);
        
        // Render the connector with the given wood type.
        tess.drawScaledPrism(4, 8, 4, 12, 16, 12, material);
        
        // Reset matrix.
        tess.popMatrix();
    }

    public void renderHead(ITessellator tess, float angle, TextureAtlasSprite material) {
        // Save translation matrix.
        tess.pushMatrix();

        // Rotate the sprinkler head to the desired angle.
        tess.translate(0.5F, 0, 0.5F);
        tess.rotate(angle, 0, 1, 0);
        tess.translate(-0.5F, 0, -0.5F);

        // Draw head core.
        tess.drawScaledPrism(MIN_C, MIN_Y, MIN_C, MAX_C, MAX_Y, MAX_C, material);

        // Draw head blades.
        tess.drawScaledPrism(BMX_A, MIN_Y, MIN_C, BMX_B, BMX_Y, MAX_C, material);
        tess.drawScaledPrism(MIN_C, MIN_Y, BMX_A, MAX_C, BMX_Y, BMX_B, material);

        // Restore translation matrix.
        tess.popMatrix();
    }

    @Override
    public TextureAtlasSprite getIcon() {
        return this.getTileEntity().getChannelIcon();
    }

    @Override
    public boolean applyAmbientOcclusion() {
        return false;
    }

}
