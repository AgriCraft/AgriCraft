package com.infinityraider.agricraft.content.core;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.block.BlockBase;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;

import java.util.function.Supplier;

public enum CropStickVariant {
    WOOD(
            Material.PLANTS,
            3,
            SoundType.WOOD,
            () -> () -> AgriCraft.instance.getModItemRegistry().crop_sticks_wood,
            () -> () -> AgriCraft.instance.getModBlockRegistry().crop_sticks_wood
    ),

    IRON(
            Material.PLANTS,
            7,
            SoundType.ANVIL,
            () -> () -> AgriCraft.instance.getModItemRegistry().crop_sticks_iron,
            () -> () -> AgriCraft.instance.getModBlockRegistry().crop_sticks_iron
    ),

    OBSIDIAN(Material.PLANTS,
            7,
            SoundType.BASALT,
            () -> () -> AgriCraft.instance.getModItemRegistry().crop_sticks_obsidian,
            () -> () -> AgriCraft.instance.getModBlockRegistry().crop_sticks_obsidian
    );

    private final String id;
    private final int strength;
    private final SoundType sound;
    private final Material material;
    private final Supplier<Supplier<Item>> itemSupplier;
    private final Supplier<Supplier<BlockBase>> blockSupplier;

    CropStickVariant(Material material, int strength, SoundType sound, Supplier<Supplier<Item>> itemSupplier, Supplier<Supplier<BlockBase>> blockSupplier) {
        this.id = Names.Blocks.CROP_STICKS + "_" + this.name().toLowerCase();
        this.strength = strength;
        this.sound = sound;
        this.material = material;
        this.itemSupplier = itemSupplier;
        this.blockSupplier = blockSupplier;
    }

    public final String getId() {
        return this.id;
    }

    public final int getStrength() {
        return this.strength;
    }

    public final SoundType getSound() {
        return this.sound;
    }

    public final Material getMaterial() {
        return this.material;
    }

    public final Item getItem() {
        return this.itemSupplier.get().get();
    }

    public final BlockBase getBlock() {
        return this.blockSupplier.get().get();
    }
}
