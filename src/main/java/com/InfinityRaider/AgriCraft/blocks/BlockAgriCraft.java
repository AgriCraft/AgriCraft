package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderBlockBase;
import com.InfinityRaider.AgriCraft.utility.RegisterHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;

public abstract class BlockAgriCraft extends Block {
    protected BlockAgriCraft(Material mat) {
        super(mat);
        RegisterHelper.registerBlock(this, getInternalName(), getItemBlockClass());
    }

    @SideOnly(Side.CLIENT)
    public abstract RenderBlockBase getRenderer();

    @Override
    public int getRenderType() {
        return AgriCraft.proxy.getRenderId(this);
    }

    protected abstract Class<? extends ItemBlock> getItemBlockClass();

    protected abstract String getInternalName();
}
