/*
 * An intermediate for the water pad.
 */
package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.renderers.blocks.RenderBlockBase;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderWaterPad;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 *
 * @author rlonryan
 */
public abstract class AbstractBlockWaterPad extends BlockBase {

	public AbstractBlockWaterPad(Material mat, String internalName) {
		super(mat, internalName);
		this.setHardness(0.5F);
        this.setStepSound(soundTypeGravel);
	}
	
	@Override
    protected IProperty[] getPropertyArray() {
        return new IProperty[0];
    }
	
	@Override
    @SideOnly(Side.CLIENT)
    public RenderBlockBase getRenderer() {
        return new RenderWaterPad(this);
    }
	
	public static class ItemBlockWaterPad extends net.minecraft.item.ItemBlock {
        public ItemBlockWaterPad(Block block) {
            super(block);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
            list.add(StatCollector.translateToLocal("agricraft_tooltip.waterPadDry"));
        }
    }
	
}
