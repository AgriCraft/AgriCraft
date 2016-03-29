/*
 * An intermediate for the water pad.
 */
package com.infinityraider.agricraft.blocks;

import com.infinityraider.agricraft.renderers.blocks.RenderWaterPad;
import com.infinityraider.agricraft.utility.RegisterHelper;
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
		super(mat, "water_pad_" + internalName);
		this.setHardness(0.5F);
		RegisterHelper.hideModel(this, this.internalName);
	}
	
	@Override
    protected IProperty[] getPropertyArray() {
        return new IProperty[0];
    }
	
	@Override
    @SideOnly(Side.CLIENT)
    public RenderWaterPad getRenderer() {
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
