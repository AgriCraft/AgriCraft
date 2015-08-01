package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.renderers.RenderBlockBase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public abstract class BlockAgriCraft extends Block {
    protected BlockAgriCraft(Material mat) {
        super(mat);
    }

    @SideOnly(Side.CLIENT)
    public abstract RenderBlockBase getRenderer();

    @Override
    public int getRenderType() {
        return AgriCraft.proxy.getRenderId(this);
    }
}
