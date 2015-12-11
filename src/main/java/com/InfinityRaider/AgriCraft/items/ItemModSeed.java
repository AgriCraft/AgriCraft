package com.InfinityRaider.AgriCraft.items;

import com.InfinityRaider.AgriCraft.api.v1.IAgriCraftSeed;
import com.InfinityRaider.AgriCraft.blocks.BlockModPlant;
import com.InfinityRaider.AgriCraft.creativetab.AgriCraftTab;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.RegisterHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemModSeed extends ItemSeeds implements IAgriCraftSeed{
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
        return (BlockModPlant) this.getPlant(null, 0, 0, 0);
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

    @Override
    public IIcon getIcon(ItemStack stack) {
        return getIconFromDamage(stack.getItemDamage());
    }

    @SideOnly(Side.CLIENT)
    public void setInformation(String information) {
        this.information = information;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float f1, float f2, float f3) {
        if (world.getBlock(x, y, z) == Blocks.blockCrop) {
            LogHelper.debug("Trying to plant seed " + stack.getItem().getUnlocalizedName() + " on crops");
            return true;
        }
        if (CropPlantHandler.getGrowthRequirement(stack.getItem(), stack.getItemDamage()).isValidSoil(world, x, y, z)) {
            if (side != 1) {
                return false;
            } else if (player.canPlayerEdit(x, y, z, side, stack) && player.canPlayerEdit(x, y + 1, z, side, stack)) {
                if (world.isAirBlock(x, y + 1, z)) {
                    world.setBlock(x, y + 1, z, this.getPlant());
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

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg) {
        LogHelper.debug("registering icon for: " + this.getUnlocalizedName());
        itemIcon = reg.registerIcon(this.getUnlocalizedName().substring(this.getUnlocalizedName().indexOf('.')+1));
    }
}
