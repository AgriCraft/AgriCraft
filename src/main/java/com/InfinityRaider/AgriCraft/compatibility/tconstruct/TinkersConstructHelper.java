package com.InfinityRaider.AgriCraft.compatibility.tconstruct;

import com.InfinityRaider.AgriCraft.blocks.BlockCrop;
import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import tconstruct.library.tools.AbilityHelper;
import tconstruct.tools.TinkerTools;

import java.util.ArrayList;
import java.util.List;

public class TinkersConstructHelper extends ModHelper {
    public static boolean isShovel(ItemStack stack) {
        if(ModHelper.allowIntegration(Names.Mods.tconstruct)) {
            if(stack==null || stack.getItem()==null) {
                return false;
            }
            try {
                return stack.getItem() == TinkerTools.shovel || stack.getItem() == TinkerTools.mattock || stack.getItem() == TinkerTools.excavator;
            } catch(Exception e) {
                return false;
            }
        }
        return false;
    }

    @Override
    protected boolean useTool(World world, int x, int y, int z, EntityPlayer player, ItemStack stack, BlockCrop block, TileEntityCrop crop) {
        if (stack.getItem() == TinkerTools.scythe) {
            NBTTagCompound tag = player.getCurrentEquippedItem().stackTagCompound;
            if(tag==null || !tag.hasKey("InfiTool")) {
                //invalid tool
                return true;
            }
            NBTTagCompound toolTag = tag.getCompoundTag("InfiTool");
            for (int xPos = x - 1; xPos <= x + 1; xPos++) {
                for (int zPos = z - 1; zPos <= z + 1; zPos++) {
                    if(toolTag.getBoolean("Broken")) {
                        break;
                    }
                    else if (world.getBlock(xPos, y, zPos) instanceof BlockCrop && block.harvest(world, xPos, y, zPos, player)) {
                        AbilityHelper.damageTool(player.getCurrentEquippedItem(), 1, player, false);
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected List<Item> getTools() {
        ArrayList<Item> list = new ArrayList<Item>();
        list.add(TinkerTools.scythe);
        return list;
    }

    @Override
    protected void init() {

    }

    @Override
    protected void initPlants() {

    }

    @Override
    protected String modId() {
        return Names.Mods.tconstruct;
    }
}
