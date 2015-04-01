package com.InfinityRaider.AgriCraft.items;

import com.InfinityRaider.AgriCraft.blocks.BlockModPlant;
import com.InfinityRaider.AgriCraft.creativetab.AgriCraftTab;
import com.InfinityRaider.AgriCraft.farming.GrowthRequirements;
import com.InfinityRaider.AgriCraft.farming.IAgriCraftPlant;
import com.InfinityRaider.AgriCraft.farming.IAgriCraftSeed;
import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemModSeed extends ItemSeeds implements IAgriCraftSeed{
    private String displayName;
    @SideOnly(Side.CLIENT)
    private String information;

    public ItemModSeed(BlockModPlant plant, String information) {
        this(plant, net.minecraft.init.Blocks.farmland, information);
    }

    public ItemModSeed(BlockModPlant plant, String name, String information) {
        this(plant, net.minecraft.init.Blocks.farmland, information);
        this.displayName = name;
    }

    public ItemModSeed(BlockModPlant plant, Block soil, String information) {
        super(plant, soil);
        if(FMLCommonHandler.instance().getEffectiveSide()==Side.CLIENT) {
            this.information = information;
        }
        this.setCreativeTab(AgriCraftTab.agriCraftTab);
    }

    public ItemModSeed(BlockModPlant plant, Block soil, String name, String information) {
        this(plant, soil, information);
        this.displayName = name;
    }

    public IAgriCraftPlant getPlant() {
        return (BlockModPlant) this.getPlant(null, 0, 0, 0);
    }

    @Override
    public int tier() {
        return ((BlockModPlant) this.getPlant()).tier;
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

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float f1, float f2, float f3) {
        if(world.getBlock(x,y,z)==Blocks.blockCrop) {
            LogHelper.debug("Trying to plant seed "+stack.getItem().getUnlocalizedName()+" on crops");
            return true;
        }
        if(GrowthRequirements.getGrowthRequirement((ItemSeeds) stack.getItem(), stack.getItemDamage()).isValidSoil(world, x, y, z)) {
            super.onItemUse(stack,player,world,x,y,z,side,f1,f2,f3);
        }
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg) {
        LogHelper.debug("registering icon for: " + this.getUnlocalizedName());
        itemIcon = reg.registerIcon(this.getUnlocalizedName().substring(this.getUnlocalizedName().indexOf('.')+1));
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return (this.displayName==null || this.displayName.equals(""))?super.getItemStackDisplayName(stack):this.displayName;
    }
}
