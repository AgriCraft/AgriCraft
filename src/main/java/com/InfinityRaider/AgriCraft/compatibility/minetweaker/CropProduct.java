package com.InfinityRaider.AgriCraft.compatibility.minetweaker;

import com.InfinityRaider.AgriCraft.blocks.BlockModPlant;
import com.InfinityRaider.AgriCraft.farming.CropProduce;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.items.ItemModSeed;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.OreDictHelper;
import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import minetweaker.api.minecraft.MineTweakerMC;
import minetweaker.mc1710.item.MCItemStack;
import minetweaker.mc1710.oredict.MCOreDictEntry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.agricraft.CropProduct")
public class CropProduct {

    @ZenMethod
    public static void add(IItemStack seed, IItemStack fruit, int weight) {
        ItemStack seedToChange = MineTweakerMC.getItemStack(seed);
        ItemStack fruitToAdd = MineTweakerMC.getItemStack(fruit);
        String error = "Invalid Seed";
        boolean success = seedToChange!=null && seedToChange.getItem()!=null && seedToChange.getItem() instanceof ItemModSeed;
        if(success) {
            BlockModPlant crop = ((ItemModSeed) seedToChange.getItem()).getPlant();
            MineTweakerAPI.apply(new AddAction(crop, fruitToAdd, weight));
        }
        if(!success) {
            MineTweakerAPI.logError("Adding fruit: '"+fruitToAdd.getDisplayName()+"' to '"+seedToChange.getDisplayName()+"' failed: "+error);
        }
    }
    
    @ZenMethod
    public static void remove(IItemStack seed, IItemStack fruit) {
        ItemStack seedToChange = MineTweakerMC.getItemStack(seed);
        ItemStack fruitToRemove = MineTweakerMC.getItemStack(fruit);
        String error = "Invalid Seed";
        boolean success = seedToChange!=null && seedToChange.getItem()!=null && seedToChange.getItem() instanceof ItemModSeed;
        if(success) {
            BlockModPlant crop = ((ItemModSeed) seedToChange.getItem()).getPlant();
            MineTweakerAPI.apply((new RemoveAction(crop, fruitToRemove)));
        }
        if(!success) {
            MineTweakerAPI.logError("Removing fruit: '"+fruitToRemove.getDisplayName()+"' from '"+seedToChange.getDisplayName()+"' failed: "+error);
        }
    }

    private static class AddAction implements IUndoableAction {
        private BlockModPlant crop;
        private ItemStack fruit;
        private int weight;
        public AddAction(BlockModPlant crop, ItemStack fruitToAdd, int weight) {
            this.crop = crop;
            this.fruit = fruitToAdd.copy();
            this.fruit.stackSize = 1;
            this.weight=weight>0?weight: CropProduce.DEFAULT_WEIGHT;
        }

        @Override
        public void apply() {
            crop.products.addProduce(fruit, weight);
            String oreDictTag = this.fruitTag();
            LogHelper.debug("Registering "+fruit.getDisplayName()+" to the ore dictionary as "+oreDictTag);
            if(ConfigurationHandler.registerCropProductsToOreDict && !OreDictHelper.hasOreId(fruit, oreDictTag)) {
                OreDictionary.registerOre(oreDictTag, fruit);
            }
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            crop.products.removeProduce(fruit);
            if(ConfigurationHandler.registerCropProductsToOreDict) {
                MCOreDictEntry ore = new MCOreDictEntry(this.fruitTag());
                ore.remove(new MCItemStack(this.fruit));
            }
        }

        @Override
        public String describe() {
            return "Adding fruit: '"+fruit.getDisplayName()+"' to '"+crop.getUnlocalizedName()+"' with weight "+weight;
        }

        @Override
        public String describeUndo() {
            return "Removing previously added fruit'"+fruit.getDisplayName()+"' from '"+crop.getUnlocalizedName()+"'";
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }

        private String fruitTag() {
            return crop.getUnlocalizedName().substring(crop.getUnlocalizedName().indexOf(':')+1);
        }
    }

    private static class RemoveAction implements IUndoableAction {
        private BlockModPlant crop;
        private ItemStack fruit;
        private int weight;
        public RemoveAction(BlockModPlant crop, ItemStack fruit) {
            this.crop = crop;
            this.fruit = fruit.copy();
            this.fruit.stackSize = 1;
            this.weight = crop.products.getWeight(this.fruit);
        }

        @Override
        public void apply() {
            crop.products.removeProduce(fruit);
            if(ConfigurationHandler.registerCropProductsToOreDict) {
                MCOreDictEntry ore = new MCOreDictEntry(this.fruitTag());
                ore.remove(new MCItemStack(this.fruit));
            }
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            crop.products.addProduce(fruit, weight);
            String oreDictTag = this.fruitTag();
            LogHelper.debug("Registering " + fruit.getDisplayName() + " to the ore dictionary as " + oreDictTag);
            if(ConfigurationHandler.registerCropProductsToOreDict && !OreDictHelper.hasOreId(fruit, oreDictTag)) {
                OreDictionary.registerOre(oreDictTag, fruit);
            }
        }

        @Override
        public String describe() {
            return "Removing fruit '"+fruit.getDisplayName()+"' from '"+crop.getUnlocalizedName()+"'";
        }

        @Override
        public String describeUndo() {
            return "Adding previously removed fruit'"+fruit.getDisplayName()+"' from '"+crop.getUnlocalizedName()+"'";
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }

        private String fruitTag() {
            return crop.getUnlocalizedName().substring(crop.getUnlocalizedName().indexOf(':')+1);
        }
    }
}