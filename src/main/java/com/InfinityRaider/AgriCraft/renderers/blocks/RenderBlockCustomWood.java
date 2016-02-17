package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.renderers.TessellatorV2;
import com.infinityraider.agricraft.tileentity.TileEntityCustomWood;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public abstract class RenderBlockCustomWood<T extends TileEntityCustomWood> extends RenderBlockBase {
    protected T teDummy;

    protected RenderBlockCustomWood(Block block, T te, boolean inventory) {
        super(block, te, inventory);
        this.teDummy = te;
    }

    @Override
    protected final void doInventoryRender(TessellatorV2 tessellator, Block block, ItemStack item, ItemCameraTransforms.TransformType transformType) {
        teDummy.setMaterial(item);
        GL11.glDisable(GL11.GL_LIGHTING);
        renderInInventory(tessellator, block, item, transformType);
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    protected abstract void renderInInventory(TessellatorV2 tessellator, Block block, ItemStack item, ItemCameraTransforms.TransformType transformType);
}
