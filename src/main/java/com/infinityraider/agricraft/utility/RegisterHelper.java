package com.infinityraider.agricraft.utility;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;

public abstract class RegisterHelper {
    public static void registerBlock(Block block, String modId, String name) {
        RegisterHelper.registerBlock(block, modId, name, null);
    }

    public static void registerBlock(Block block, String modId, String name, Class<? extends ItemBlock> itemClass) {
        String unlocalized = modId.toLowerCase() + ':' + name;
        block.setUnlocalizedName(unlocalized);
        if (itemClass != null) {
            GameRegistry.registerBlock(block, itemClass, unlocalized);
        } else {
            GameRegistry.registerBlock(block, unlocalized);
        }
    }

    public static void registerItem(Item item, String modId, String name) {
        String unlocalized = modId.toLowerCase() + ':' + name;
        item.setUnlocalizedName(unlocalized);
        GameRegistry.registerItem(item, unlocalized);
    }

    public static void removeRecipe(ItemStack stack) {
        ArrayList recipes = (ArrayList) CraftingManager.getInstance().getRecipeList();
        ItemStack result;
        for(int i=0;i<recipes.size();i++) {
            IRecipe recipe = (IRecipe) recipes.get(i);
            result = recipe.getRecipeOutput();
            if(result!=null && stack.getItem()==result.getItem() && stack.getItemDamage()==result.getItemDamage()) {
                recipes.remove(i);
            }
        }
    }

    public static SoundEvent registerSound(String modId, String name) {
        return registerSound(new ResourceLocation(modId, name));
    }

    public static SoundEvent registerSound(ResourceLocation id) {
        int size = SoundEvent.REGISTRY.getKeys().size();
        SoundEvent sound = new SoundEvent(id);
        SoundEvent.REGISTRY.register(size, id, sound);
        return sound;
    }
}
