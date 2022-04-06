package com.infinityraider.agricraft.content.tools;

import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.enchantment.EnchantmentBase;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import javax.annotation.Nonnull;

import static net.minecraft.world.item.enchantment.Enchantment.Rarity.COMMON;

public class EnchantmentSeedBag extends EnchantmentBase {
    public EnchantmentSeedBag() {
        super(Names.Items.SEED_BAG, COMMON, EnchantmentCategory.create("SEED_BAG", item -> item instanceof ItemSeedBag),
                new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});
    }

    @Override
    protected boolean checkCompatibility(@Nonnull Enchantment other) {
        return false;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return (stack.getItem() instanceof ItemSeedBag) && !((ItemSeedBag) stack.getItem()).isActivated(stack);
    }

    @Override
    public boolean isTreasureOnly() {
        return false;
    }

    @Override
    public boolean isCurse() {
        return false;
    }

    @Override
    public boolean isTradeable() {
        return false;
    }

    @Override
    public boolean isDiscoverable() {
        return true;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return (stack.getItem() instanceof ItemSeedBag) && !((ItemSeedBag) stack.getItem()).isActivated(stack);
    }

    @Override
    public boolean isAllowedOnBooks() {
        return false;
    }
}
