package com.InfinityRaider.AgriCraft.compatibility.tconstruct;

import com.InfinityRaider.AgriCraft.blocks.BlockCrop;
import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TinkersConstructHelper extends ModHelper {
    private static ArrayList<Item> shovels;
    private static Item scythe;
    private Method damageTool;

    @Override
    protected void onPostInit() {
        shovels = new ArrayList<Item>();
        shovels.add((Item) Item.itemRegistry.getObject("TConstruct:shovel"));
        shovels.add((Item) Item.itemRegistry.getObject("TConstruct:excavator"));
        shovels.add((Item) Item.itemRegistry.getObject("TConstruct:mattock"));
        scythe = (Item) Item.itemRegistry.getObject("TConstruct:scythe");
        try {
            damageTool = Class.forName("tconstruct.library.tools.AbilityHelper").getDeclaredMethod("damageTool", ItemStack.class, int.class, EntityLivingBase.class, boolean.class);
        } catch(Exception e) {
            LogHelper.printStackTrace(e);
        }
    }

    public static boolean isShovel(ItemStack stack) {
        if(ModHelper.allowIntegration(Names.Mods.tconstruct)) {
            if(stack==null || stack.getItem()==null) {
                return false;
            }
            try {
                return shovels.contains(stack.getItem());
            } catch(Exception e) {
                return false;
            }
        }
        return false;
    }

    @Override
    protected boolean useTool(World world, int x, int y, int z, EntityPlayer player, ItemStack stack, BlockCrop block, TileEntityCrop crop) {
        if (stack.getItem() == scythe) {
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
                    else if (world.getBlock(xPos, y, zPos) instanceof BlockCrop && block.harvest(world, xPos, y, zPos, player, null)) {
                        if(damageTool != null) {
                            try {
                                damageTool.invoke(null, player.getCurrentEquippedItem(), 1, player, false);
                            } catch (Exception e) {
                                LogHelper.printStackTrace(e);
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected List<Item> getTools() {
        ArrayList<Item> list = new ArrayList<Item>();
        list.add(scythe);
        return list;
    }

    @Override
    protected String modId() {
        return Names.Mods.tconstruct;
    }
}
