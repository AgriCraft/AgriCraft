package com.InfinityRaider.AgriCraft.renderers.blocks;

import com.InfinityRaider.AgriCraft.tileentity.TileEntityCustomWood;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public abstract class RenderBlockCustomWood<T extends TileEntityCustomWood> extends RenderBlockBase {
    protected T teDummy;

    protected RenderBlockCustomWood(Block block, T te, boolean inventory) {
        super(block, te, inventory);
        this.teDummy = te;
    }

    @Override
    protected final void doInventoryRender(ItemRenderType type, ItemStack item, Object... data) {
        teDummy.setMaterial(item);
        GL11.glDisable(GL11.GL_LIGHTING);
        renderInInventory(type, item, data);
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    protected abstract void renderInInventory(ItemRenderType type, ItemStack item, Object... data);
}
