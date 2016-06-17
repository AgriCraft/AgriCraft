package com.infinityraider.agricraft.items;

import com.infinityraider.agricraft.api.v3.items.IClipper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemClipper extends ItemBase implements IClipper {
	
    public ItemClipper() {
        super("clipper", true);
        this.setMaxStackSize(1);
    }

    @Override
    public boolean canItemEditBlocks() {return true;}

    //this is called when you right click with this item in hand
    @Override
    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        return EnumActionResult.PASS;   //return PASS or else no other use methods will be called (for instance "onBlockActivated" on the crops block)
    }

    @Override
    public void onClipperUsed(World world, BlockPos pos, IBlockState state, EntityPlayer player) {}

    @SideOnly(Side.CLIENT)
	@Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
        if (stack == null || stack.getItem() == null) {
            list.add("ERROR");
            return;
        }
        if (stack.getItem() instanceof ItemClipping) {
            stack = ItemStack.loadItemStackFromNBT(stack.getTagCompound());
        }
        if (stack == null || stack.getItem() == null) {
            list.add("ERROR");
        }
    }
}
