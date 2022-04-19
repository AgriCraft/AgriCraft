package com.infinityraider.agricraft.content.core;

import com.infinityraider.agricraft.api.v1.content.items.IAgriCropStickItem;
import com.infinityraider.agricraft.content.AgriItemRegistry;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.agricraft.util.FluidPredicates;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.IExtensibleEnum;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.function.Supplier;

public enum CropStickVariant implements IAgriCropStickItem.Variant, IExtensibleEnum {
    WOOD(
            Material.PLANT,
            SoundType.WOOD,
            () -> () -> AgriItemRegistry.getInstance().crop_sticks_wood.get(),
            FluidPredicates.NOT_LAVA
    ),

    IRON(
            Material.PLANT,
            SoundType.ANVIL,
            () -> () -> AgriItemRegistry.getInstance().crop_sticks_iron.get(),
            FluidPredicates.ANY_FLUID
    ),

    OBSIDIAN(Material.PLANT,
            SoundType.BASALT,
            () -> () -> AgriItemRegistry.getInstance().crop_sticks_obsidian.get(),
            FluidPredicates.ANY_FLUID
    );

    private final String id;
    private final SoundType sound;
    private final Material material;
    private final Supplier<Supplier<IAgriCropStickItem>> itemSupplier;
    private final Predicate<Fluid> fluidPredicate;

    CropStickVariant(Material material, SoundType sound,
                     Supplier<Supplier<IAgriCropStickItem>> itemSupplier,
                     Predicate<Fluid> fluidPredicate) {
        this.id = Names.Blocks.CROP_STICKS + "_" + this.name().toLowerCase();
        this.sound = sound;
        this.material = material;
        this.itemSupplier = itemSupplier;
        this.fluidPredicate = fluidPredicate;
    }

    @Override
    public final String getId() {
        return this.id;
    }

    @Override
    public final SoundType getSound() {
        return this.sound;
    }

    @Override
    public final Material getMaterial() {
        return this.material;
    }

    @Override
    public final IAgriCropStickItem getItem() {
        return this.itemSupplier.get().get();
    }

    @Override
    public final boolean canExistInFluid(Fluid fluid) {
        return this.fluidPredicate.test(fluid);
    }

    @Override
    public final void playCropStickSound(Level world, BlockPos pos) {
        world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5F, pos.getZ() + 0.5F, this.getSound().getPlaceSound(),
                SoundSource.BLOCKS, (this.getSound().getVolume() + 1.0F) / 4.0F, this.getSound().getPitch() * 0.8F);
    }

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase();
    }

    @Nullable
    public static CropStickVariant fromItem(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof IAgriCropStickItem)) {
            return null;
        }
        IAgriCropStickItem sticks = (IAgriCropStickItem) stack.getItem();
        return Arrays.stream(values())
                .filter(variant -> variant == sticks.getVariant())
                .findAny()
                .orElse(null);
    }

    @SuppressWarnings("unused")
    public static CropStickVariant create(String name, Material material, SoundType sound,
                                          Supplier<Supplier<IAgriCropStickItem>> itemSupplier,
                                          Predicate<Fluid> fluidPredicate) {
        throw new IllegalStateException("Enum not extended");
    }
}
