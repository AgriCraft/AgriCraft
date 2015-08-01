package com.InfinityRaider.AgriCraft.renderers.blocks;

import com.InfinityRaider.AgriCraft.tileentity.TileEntityCustomWood;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public abstract class RenderBlockCustomWood extends RenderBlockBase {
    protected TileEntityCustomWood teDummy;

    protected RenderBlockCustomWood(Block block, TileEntityCustomWood te, boolean inventory) {
        super(block, te, inventory);
        this.teDummy = te;
    }

    @Override
    protected final void doInventoryRender(ItemRenderType type, ItemStack item, Object... data) {
        teDummy.setMaterial(item.getTagCompound());
        GL11.glDisable(GL11.GL_LIGHTING);
        renderInInventory(type, item, data);
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    protected abstract void renderInInventory(ItemRenderType type, ItemStack item, Object... data);
}
