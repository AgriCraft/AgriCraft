package com.InfinityRaider.AgriCraft.farming;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Random;

public class CropProduce {
    public static final int DEFAULT_WEIGHT = 100;
    private ArrayList<Product> products = new ArrayList<>();
    private int totalWeight;

    public void addProduce(ItemStack stack) {
        if(stack==null || stack.getItem()==null) {
            return;
        }
        this.addProduce(stack, DEFAULT_WEIGHT);
    }

    public void addProduce(ItemStack stack, int weight) {
        this.addProduce(stack, weight, false);
    }

    public void addProduce(ItemStack stack, int weight, int minGain) {
        this.addProduce(stack, weight, minGain, false);
    }

    public void addProduce(ItemStack stack, int weight, boolean overwrite) {
        this.addProduce(stack, weight, 1, overwrite);
    }

    public void addProduce(ItemStack stack, int weight, int minGain, boolean overwrite) {
        if(overwrite) {
            this.removeProduce(stack);
        }
        this.products.add(new Product(stack, weight, minGain));
        this.totalWeight = this.totalWeight + weight;
    }

    public void overwriteWeight(ItemStack stack, int weight) {
        this.addProduce(stack, weight, true);
    }

    public void removeProduce(ItemStack stack) {
        for(int i=0;i<products.size();i++) {
            Product product = products.get(i);
            if(product.product.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(stack, product.product)) {
                totalWeight = totalWeight - product.weight;
                products.remove(i);
            }
        }
    }

    public ArrayList<ItemStack> getProduce(int amount, Random rand) {
        ArrayList<ItemStack> results = new ArrayList<>();
        while(amount>0) {
            double stop = rand.nextDouble()*this.totalWeight;
            for (Product product:this.products) {
                stop = stop - product.weight;
                if (stop <= 0) {
                    results.add(product.product.copy());
                    break;
                }
            }
            amount--;
        }
        return results;
    }

    public ArrayList<ItemStack> getAllProducts() {
        ArrayList<ItemStack> fruits = new ArrayList<>();
        for(Product product:this.products) {
            fruits.add(product.product.copy());
        }
        return fruits;
    }

    public int getWeight(ItemStack stack) {
        int weight = 0;
        for (Product product:products) {
            if (product.product.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(stack, product.product)) {
                weight = product.weight;
                break;
            }
        }
        return weight;
    }


    // Something funny is going on here.
    private static class Product {
        protected ItemStack product;
        protected int weight;
        protected int minGain;

        public Product(ItemStack product, int weight) {
            this(product, weight, 1);
        }

        public Product(ItemStack product, int weight, int minGain) {
            this.product = product.copy();
            this.product.stackSize = 1;
            this.weight = weight;
            this.minGain = minGain;
        }
    }
}
