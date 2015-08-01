package com.InfinityRaider.AgriCraft.renderers;

import com.InfinityRaider.AgriCraft.tileentity.TileEntityCustomWood;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

public abstract class RenderBlockCustomWood extends RenderBlockBase {
    protected TileEntityCustomWood teDummy;

    protected RenderBlockCustomWood(Block block, boolean inventory) {
        super(block, inventory);
        try {
            this.teDummy = getTileEntityClass().getConstructor().newInstance();
        } catch (Exception e) {
            LogHelper.printStackTrace(e);
        }
    }

    protected RenderBlockCustomWood(Block block, TileEntity te, boolean inventory) {
        super(block, te, inventory);
    }

    @Override
    protected final void doInventoryRender(ItemRenderType type, ItemStack item, Object... data) {
        teDummy.setMaterial(item.getTagCompound());
        GL11.glDisable(GL11.GL_LIGHTING);
        renderInInventory(type, item, data);
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    protected abstract void renderInInventory(ItemRenderType type, ItemStack item, Object... data);

    protected abstract Class<? extends TileEntityCustomWood> getTileEntityClass();
}
