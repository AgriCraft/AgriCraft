package com.InfinityRaider.AgriCraft.items;

import com.InfinityRaider.AgriCraft.api.v1.IAgriCraftSeed;
import com.InfinityRaider.AgriCraft.blocks.BlockModPlant;
import com.InfinityRaider.AgriCraft.creativetab.AgriCraftTab;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.RegisterHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemModSeed extends ItemSeeds implements IAgriCraftSeed {
    @SideOnly(Side.CLIENT)
    private String information;

    /** This constructor shouldn't be called from anywhere except from the BlockModPlant public constructor, if you create a new BlockModPlant, its contructor will create the seed for you */
    public ItemModSeed(BlockModPlant plant, String information) {
        super(plant, plant.getGrowthRequirement().getSoil()==null?net.minecraft.init.Blocks.farmland:plant.getGrowthRequirement().getSoil().getBlock());
        if(FMLCommonHandler.instance().getEffectiveSide()==Side.CLIENT) {
            this.information = information;
        }
        this.setCreativeTab(AgriCraftTab.agriCraftTab);
        //register seed
        RegisterHelper.registerSeed(this, plant);
    }

    public BlockModPlant getPlant() {
        return (BlockModPlant) (this.getPlant(null, null).getBlock());
    }

    @Override
    public int tier() {
        return (this.getPlant()).tier;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getInformation() {
        return this.information;
    }

    @SideOnly(Side.CLIENT)
    public void setInformation(String information) {
        this.information = information;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.getBlockState(pos).getBlock() == Blocks.blockCrop) {
            LogHelper.debug("Trying to plant seed " + stack.getItem().getUnlocalizedName() + " on crops");
            return true;
        }
        if (CropPlantHandler.getGrowthRequirement(stack.getItem(), stack.getItemDamage()).isValidSoil(world, pos)) {
            BlockPos blockPosUp = pos.add(0, 1, 0);
            if (side != EnumFacing.UP) {
                return false;
            } else if (player.canPlayerEdit(pos, side, stack) && player.canPlayerEdit(blockPosUp, side, stack)) {
                if (world.isAirBlock(blockPosUp)) {
                    world.setBlockState(blockPosUp, this.getPlant().getStateFromMeta(0), 3);
                    --stack.stackSize;
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        return false;
    }
}
