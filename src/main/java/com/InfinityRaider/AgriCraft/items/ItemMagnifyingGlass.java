package com.InfinityRaider.AgriCraft.items;

import com.InfinityRaider.AgriCraft.blocks.BlockCrop;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.renderers.items.RenderItemBase;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ItemMagnifyingGlass extends ItemAgricraft {
    public ItemMagnifyingGlass() {
        super();
        this.setMaxStackSize(1);
    }

    @Override
    protected String getInternalName() {
        return Names.Objects.magnifyingGlass;
    }

    //I'm overriding this just to be sure
    @Override
    public boolean canItemEditBlocks() {return true;}

    //this is called when you right click with this item in hand
    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if(world.isRemote) {
            ArrayList<String> list = new ArrayList<String>();
            if(world.getBlock(x, y, z)!=null && world.getBlock(x, y, z) instanceof BlockCrop && world.getTileEntity(x, y, z)!=null && world.getTileEntity(x, y, z) instanceof TileEntityCrop) {
                TileEntityCrop crop = (TileEntityCrop) world.getTileEntity(x, y, z);
                if(crop.hasPlant()) {
                    int growth = crop.getGrowth();
                    int gain = crop.getGain();
                    int strength = crop.getStrength();
                    boolean analyzed = crop.isAnalyzed();
                    ItemStack seed = crop.getSeedStack();
                    String seedName = seed.getItem().getItemStackDisplayName(seed);
                    int meta = world.getBlockMetadata(x, y, z);
                    float growthPercentage = ((float) meta)/((float) 7)*100.0F;
                    list.add(StatCollector.translateToLocal("agricraft_tooltip.cropWithPlant"));
                    list.add(StatCollector.translateToLocal("agricraft_tooltip.seed") + ": " + seedName);
                    if(analyzed) {
                        list.add(" - " + StatCollector.translateToLocal("agricraft_tooltip.growth") + ": " + growth);
                        list.add(" - " + StatCollector.translateToLocal("agricraft_tooltip.gain") + ": " + gain);
                        list.add(" - " + StatCollector.translateToLocal("agricraft_tooltip.strength") + ": " + strength);
                    }
                    else {
                        list.add(StatCollector.translateToLocal("agricraft_tooltip.analyzed"));
                    }
                    list.add(StatCollector.translateToLocal(crop.isFertile()?"agricraft_tooltip.fertile":"agricraft_tooltip.notFertile"));
                    if (growthPercentage < 100.0) {
                        list.add(String.format("Growth : %.0f %%", growthPercentage));
                    } else {
                        list.add("Growth : Mature");
                    }
                }
                else if(crop.isCrossCrop()) {
                    list.add(StatCollector.translateToLocal("agricraft_tooltip.crossCrop"));
                }
                else if(crop.hasWeed()) {
                    list.add(StatCollector.translateToLocal("agricraft_tooltip.weeds"));
                }
                else {
                    list.add(StatCollector.translateToLocal("agricraft_tooltip.cropWithoutPlant"));
                }
            }
            else {
                list.add(StatCollector.translateToLocal("agricraft_tooltip.notCrop"));
            }
            for(String msg:list) {
                player.addChatComponentMessage(new ChatComponentText(msg));
            }
        }
        return true;   //return true so nothing else happens
    }

    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag) {
        list.add(StatCollector.translateToLocal("agricraft_tooltip.magnifyingGlass"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg) {
        LogHelper.debug("registering icon for: " + this.getUnlocalizedName());
        this.itemIcon = reg.registerIcon(this.getUnlocalizedName().substring(this.getUnlocalizedName().indexOf('.')+1));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderItemBase getItemRenderer() {
        return null;
    }
}