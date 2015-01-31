package com.InfinityRaider.AgriCraft.farming;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Random;

public class CropProduce {
    private ArrayList<Product> products = new ArrayList<Product>();
    private int totalWeight;

    public void addProduce(ItemStack stack, int weight) {
        this.addProduce(stack, weight, false);
    }

    public void addProduce(ItemStack stack, int weight, boolean overwrite) {
        if(overwrite) {
            this.removeProduce(stack);
        }
        this.products.add(new Product(stack, weight));
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
        ArrayList<ItemStack> results = new ArrayList<ItemStack>();
        while(amount>0) {
            double stop = rand.nextDouble()*this.totalWeight;
            for (Product product:this.products) {
                stop = stop - product.weight;
                if (stop <= 0) {
                    results.add(product.product.copy());
                }
            }
            amount--;
        }
        return results;
    }


    private static class Product {
        protected ItemStack product;
        protected int weight;

        public Product(ItemStack product, int weight) {
            this.product = product.copy();
            this.product.stackSize = 1;
            this.weight = weight;
        }
    }
}
