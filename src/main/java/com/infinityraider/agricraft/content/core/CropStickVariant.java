package com.infinityraider.agricraft.content.core;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.block.BlockBase;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;

import java.util.function.Supplier;

public enum CropStickVariant {
    WOOD(Material.WOOD, () -> AgriCraft.instance.getModItemRegistry().crop_sticks_wood, () -> AgriCraft.instance.getModBlockRegistry().crop_sticks_wood),
    IRON(Material.IRON, () -> AgriCraft.instance.getModItemRegistry().crop_sticks_iron, () -> AgriCraft.instance.getModBlockRegistry().crop_sticks_iron),
    OBSIDIAN(Material.ROCK, () -> AgriCraft.instance.getModItemRegistry().crop_sticks_obsidian, () -> AgriCraft.instance.getModBlockRegistry().crop_sticks_obsidian);

    private final String id;
    private final Material material;
    private final Supplier<Item> itemSupplier;
    private final Supplier<BlockBase> blockSupplier;

    CropStickVariant(Material material, Supplier<Item> itemSupplier, Supplier<BlockBase> blockSupplier) {
        this.id = Names.Blocks.CROP_STICKS + "_" + this.name().toLowerCase();
        this.material = material;
        this.itemSupplier = itemSupplier;
        this.blockSupplier = blockSupplier;
    }

    public String getId() {
        return this.id;
    }

    public Material getMaterial() {
        return this.material;
    }

    public Item getItem() {
        return this.itemSupplier.get();
    }

    public BlockBase getBlock() {
        return this.blockSupplier.get();
    }
}
