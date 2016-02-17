package com.InfinityRaider.AgriCraft.renderers.blocks;

import com.InfinityRaider.AgriCraft.renderers.*;
import com.InfinityRaider.AgriCraft.renderers.renderinghacks.BlockRendererDispatcherWrapped;
import com.InfinityRaider.AgriCraft.renderers.renderinghacks.IItemRenderer;
import com.InfinityRaider.AgriCraft.renderers.renderinghacks.ISimpleBlockRenderingHandler;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityBase;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public abstract class RenderBlockBase extends TileEntitySpecialRenderer<TileEntityBase> implements ISimpleBlockRenderingHandler, IItemRenderer {

    private final Block block;
    private final boolean inventory;

    protected RenderBlockBase(Block block, boolean inventory) {
        this(block, null, inventory);
    }

    protected RenderBlockBase(Block block, TileEntityBase te, boolean inventory) {
        this.block = block;
        this.registerRenderer(block, te);
        this.inventory = inventory;
        if(inventory) {
            BlockRendererDispatcherWrapped.getInstance().registerItemRenderingHandler(Item.getItemFromBlock(block), this);
        }
    }

    @SuppressWarnings("unchecked")
    private void registerRenderer(Block block, TileEntityBase te) {
        if(te!=null && this.shouldBehaveAsTESR()) {
            ClientRegistry.bindTileEntitySpecialRenderer(te.getTileClass(), this);
        }
        if(this.shouldBehaveAsISBRH()) {
            BlockRendererDispatcherWrapped.getInstance().registerBlockRenderingHandler(block, this);
        }
    }

    //WORLD
    //-----
    private boolean renderBlock(IBlockAccess world, double x, double y, double z, BlockPos pos, Block block, IBlockState state, TileEntity tile, float partialTicks, int destroyStage, WorldRenderer renderer, boolean callFromTESR) {
        TessellatorV2 tessellator = TessellatorV2.getInstance(renderer);

        if (callFromTESR) {
            GL11.glPushMatrix();
            GL11.glTranslated(x, y, z);
        } else {
            tessellator.translate((float) x, (float) y, (float) z);
        }
        tessellator.setRotation(0, 0, 0, 0);
        if (tile != null && tile instanceof TileEntityBase) {
            if(callFromTESR) {
                rotateMatrix((TileEntityBase) tile, false);
            } else {
                rotateMatrix((TileEntityBase) tile, tessellator, false);
            }
        }

        tessellator.setBrightness(block.getMixedBrightnessForBlock(world, pos));
        tessellator.setColorRGBA_F(1, 1, 1, 1);

        boolean result = doWorldRender(tessellator, world, x, y, z, pos, block, state, tile, partialTicks, destroyStage, renderer, callFromTESR);

        if (tile != null && tile instanceof TileEntityBase) {
            if(callFromTESR) {
                rotateMatrix((TileEntityBase) tile, true);
            } else {
                rotateMatrix((TileEntityBase) tile, tessellator, true);
            }
        }
        tessellator.setRotation(0, 0, 0, 0);
        if (callFromTESR) {
            GL11.glTranslated(-x, -y, -z);
            GL11.glPopMatrix();
        } else {
            tessellator.translate((float) -x, (float) -y, (float) -z);
        }
        return result;
    }

    /** Call from TESR */
    @Override
    public final void renderTileEntityAt(TileEntityBase te, double x, double y, double z, float partialTicks, int destroyStage) {
        renderBlock(getWorld(), x, y, z, te.getPos(), te.getBlockType(), te.getWorld().getBlockState(te.getPos()), te, partialTicks, destroyStage, Tessellator.getInstance().getWorldRenderer(), true);
    }

    /** Call from ISBRH */
    @Override
    public final boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, BlockPos pos, Block block, IBlockState state, WorldRenderer renderer) {
        return renderBlock(world, x, y, z, pos, block, state, world.getTileEntity(pos), 0, 0, renderer, false);
    }

    protected abstract boolean doWorldRender(TessellatorV2 tessellator, IBlockAccess world, double x, double y, double z, BlockPos pos, Block block, IBlockState state, TileEntity tile, float partialTicks, int destroyStage, WorldRenderer renderer, boolean callFromTESR);

    //INVENTORY
    //---------
    @Override
    public boolean shouldRender3D(ItemStack stack) {
        return inventory;
    }

    @Override
    @SuppressWarnings("deprecation")
    public final void renderItem(ItemStack stack, ItemCameraTransforms.TransformType transformType) {
        renderInInventory(stack, transformType);
    }

	/**
	 * TODO: WARNING: HACK
	*/
    private void renderInInventory(ItemStack stack, ItemCameraTransforms.TransformType transformType) {
        TessellatorV2 tessellator = TessellatorV2.getInstance(Tessellator.getInstance());
		tessellator.pushMatrix();
		tessellator.scale(0.5, 0.5, 0.5);
		tessellator.translate(-1, -.9, 0);
        doInventoryRender(tessellator, block, stack, transformType);
		tessellator.popMatrix();
    }

    @SuppressWarnings("deprecation")
    protected abstract void doInventoryRender(TessellatorV2 tessellator, Block block, ItemStack item, ItemCameraTransforms.TransformType transformType);


    //HELPER METHODS
    //--------------
    public final Block getBlock() {
        return this.block;
    }

    public abstract boolean shouldBehaveAsTESR();

    public abstract boolean shouldBehaveAsISBRH();


    //UTILITY METHODS
    //---------------
    protected void rotateMatrix(TileEntityBase tileEntityBase, boolean inverse) {
        float angle = getRotationAngle(tileEntityBase);
        if(angle == 0) {
            return;
        }
        float dx = angle % 270 == 0 ? 0 : -1;
        float dz = angle > 90 ? -1 : 0;
        if (inverse) {
            GL11.glTranslatef(-dx, 0, -dz);
            GL11.glRotatef(-angle, 0, 1, 0);
        } else {
            GL11.glRotatef(angle, 0, 1, 0);
            GL11.glTranslatef(dx, 0, dz);
        }        
    }
    
    protected void rotateMatrix(TileEntityBase tileEntityBase, TessellatorV2 tessellator, boolean inverse) {
        float angle = getRotationAngle(tileEntityBase);
        if(angle == 0) {
            return;
        }
        float dx = angle % 270 == 0 ? 0 : -1;
        float dz = angle > 90 ? -1 : 0;
        if (inverse) {
            tessellator.translate(-dx, 0, -dz);
            tessellator.rotate(-angle, 0, 1, 0);
        } else {
            tessellator.rotate(angle, 0, 1, 0);
            tessellator.translate(dx, 0, dz);
        }
    }
    
    private float getRotationAngle(TileEntityBase tileEntityBase) {
        //+x = EAST
        //+z = SOUTH
        //-x = WEST
        //-z = NORTH
        if(!tileEntityBase.isRotatable()) {
            return 0;
        }
        float angle;
        switch(tileEntityBase.getOrientation()) {
            case SOUTH: angle = 180; break;
            case WEST: angle = 90; break;
            case NORTH: angle = 0; break;
            case EAST: angle = 270; break;
            default: return 0;
        }
        return angle;        
    }

}
