package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.blocks.irrigation.BlockWaterChannelFull;
import com.infinityraider.agricraft.blocks.tiles.irrigation.TileEntityChannelFull;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderChannelFull extends RenderChannel<BlockWaterChannelFull, TileEntityChannelFull> {

	public RenderChannelFull(BlockWaterChannelFull block) {
		super(block, new TileEntityChannelFull());
	}

	@Override
	protected void renderBottom(ITessellator tessellator, TextureAtlasSprite matIcon) {
		//draw bottom
		tessellator.drawScaledPrism(0, 0, 0, 16, 5, 16, matIcon);
		//draw top
		tessellator.drawScaledPrism(0, 12, 0, 16, 16, 16, matIcon);
		//draw four corners
		tessellator.drawScaledPrism(0, 5, 0, 5, 12, 5, matIcon);
		tessellator.drawScaledPrism(11, 5, 0, 16, 12, 5, matIcon);
		tessellator.drawScaledPrism(11, 5, 11, 16, 12, 16, matIcon);
		tessellator.drawScaledPrism(0, 5, 11, 5, 12, 16, matIcon);

	}

	@Override
    protected void renderSide(ITessellator tessellator, TileEntityChannelFull channel, EnumFacing dir, boolean connect, TextureAtlasSprite matIcon) {
        if(connect) {
            return;
        }
        switch(dir) {
            case EAST:
                //positive x
                tessellator.drawScaledFace(5, 5, 11, 12, dir, matIcon, 16);
                break;
            case WEST:
                //negative x
                tessellator.drawScaledFace(5, 5, 11, 12, dir, matIcon, 0);
                break;
            case NORTH:
                //negative z
                tessellator.drawScaledFace(5, 5, 11, 12, dir, matIcon, 0);
                break;
            case SOUTH:
                //positive z
                tessellator.drawScaledFace(5, 5, 11, 12, dir, matIcon, 16);
                break;
        }
    }

	@Override
	public void renderInventoryBlockWood(ITessellator tessellator, World world, TileEntityChannelFull channel, ItemStack stack, EntityLivingBase entity, TextureAtlasSprite icon) {
		this.renderBottom(tessellator, icon);
		this.renderSide(tessellator, channel, EnumFacing.NORTH, true, icon);
		this.renderSide(tessellator, channel, EnumFacing.EAST, true, icon);
		this.renderSide(tessellator, channel, EnumFacing.SOUTH, true, icon);
		this.renderSide(tessellator, channel, EnumFacing.WEST, true, icon);

	}

    @Override
    public boolean applyAmbientOcclusion() {
        return true;
    }
}
