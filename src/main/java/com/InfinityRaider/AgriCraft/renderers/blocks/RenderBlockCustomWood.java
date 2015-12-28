package com.InfinityRaider.AgriCraft.renderers.blocks;

import com.InfinityRaider.AgriCraft.tileentity.TileEntityCustomWood;
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
    protected final void doInventoryRender(Block block, ItemStack item, ItemCameraTransforms.TransformType transformType) {
        teDummy.setMaterial(item);
        GL11.glDisable(GL11.GL_LIGHTING);
        renderInInventory(block, item);
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    protected abstract void renderInInventory(Block block, ItemStack item);
}
