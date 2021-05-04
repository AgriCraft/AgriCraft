package com.infinityraider.agricraft.content.tools;

import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.enchantment.EnchantmentBase;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

import static net.minecraft.enchantment.Enchantment.Rarity.COMMON;

public class EnchantmentSeedBag extends EnchantmentBase {
    public EnchantmentSeedBag() {
        super(Names.Items.SEED_BAG, COMMON, EnchantmentType.create("SEED_BAG", item -> item instanceof ItemSeedBag),
                new EquipmentSlotType[]{EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND});
    }

    @Override
    protected boolean canApplyTogether(@Nonnull Enchantment other) {
        return false;
    }

    @Override
    public boolean canApply(ItemStack stack) {
        return (stack.getItem() instanceof ItemSeedBag) && !((ItemSeedBag) stack.getItem()).isActivated(stack);
    }

    @Override
    public boolean isTreasureEnchantment() {
        return false;
    }

    @Override
    public boolean isCurse() {
        return false;
    }

    @Override
    public boolean canVillagerTrade() {
        return false;
    }

    @Override
    public boolean canGenerateInLoot() {
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
