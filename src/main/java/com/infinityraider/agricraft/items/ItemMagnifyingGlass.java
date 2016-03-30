package com.infinityraider.agricraft.items;

import com.infinityraider.agricraft.blocks.BlockCrop;
import com.infinityraider.agricraft.tileentity.TileEntityCrop;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class ItemMagnifyingGlass extends ItemBase {
	
    public ItemMagnifyingGlass() {
        super("magnifying_glass", true);
        this.setMaxStackSize(1);
    }

    //I'm overriding this just to be sure
    @Override
    public boolean canItemEditBlocks() {return true;}

    //this is called when you right click with this item in hand
    @Override
    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        if(world.isRemote) {
            ArrayList<String> list = new ArrayList<>();
            IBlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            TileEntity te = world.getTileEntity(pos);
            if((block != null) && (block instanceof BlockCrop) && (te != null) &&(te instanceof TileEntityCrop)) {
                TileEntityCrop crop = (TileEntityCrop) te;
                if(crop.hasPlant()) {
                    int growth = crop.getGrowth();
                    int gain = crop.getGain();
                    int strength = crop.getStrength();
                    boolean analyzed = crop.isAnalyzed();
                    ItemStack seed = crop.getSeedStack();
                    String seedName = seed.getItem().getItemStackDisplayName(seed);
                    int meta = crop.getGrowthStage();
                    float growthPercentage = ((float) meta)/((float) 7)*100.0F;
                    list.add(I18n.translateToLocal("agricraft_tooltip.cropWithPlant"));
                    list.add(I18n.translateToLocal("agricraft_tooltip.seed") + ": " + seedName);
                    if(analyzed) {
                        list.add(" - " + I18n.translateToLocal("agricraft_tooltip.growth") + ": " + growth);
                        list.add(" - " + I18n.translateToLocal("agricraft_tooltip.gain") + ": " + gain);
                        list.add(" - " + I18n.translateToLocal("agricraft_tooltip.strength") + ": " + strength);
                    }
                    else {
                        list.add(I18n.translateToLocal("agricraft_tooltip.analyzed"));
                    }
                    list.add(I18n.translateToLocal(crop.isFertile()?"agricraft_tooltip.fertile":"agricraft_tooltip.notFertile"));
                    if (growthPercentage < 100.0) {
                        list.add(String.format("Growth : %.0f %%", growthPercentage));
                    } else {
                        list.add("Growth : Mature");
                    }
                }
                else if(crop.isCrossCrop()) {
                    list.add(I18n.translateToLocal("agricraft_tooltip.crossCrop"));
                }
                else if(crop.hasWeed()) {
                    list.add(I18n.translateToLocal("agricraft_tooltip.weeds"));
                }
                else {
                    list.add(I18n.translateToLocal("agricraft_tooltip.cropWithoutPlant"));
                }
            }
            else {
                list.add(I18n.translateToLocal("agricraft_tooltip.notCrop"));
            }
            for(String msg:list) {
                player.addChatComponentMessage(new TextComponentString(msg));
            }
        }
        return EnumActionResult.SUCCESS;
    }

    @SideOnly(Side.CLIENT)
	@Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
        list.add(I18n.translateToLocal("agricraft_tooltip.magnifyingGlass"));
    }

}