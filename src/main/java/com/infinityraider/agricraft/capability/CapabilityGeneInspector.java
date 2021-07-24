package com.infinityraider.agricraft.capability;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.capability.IInfSerializableCapabilityImplementation;
import com.infinityraider.infinitylib.utility.ISerializable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class CapabilityGeneInspector implements IInfSerializableCapabilityImplementation<ItemStack, CapabilityGeneInspector.Impl> {
    private static final CapabilityGeneInspector INSTANCE = new CapabilityGeneInspector();

    public static CapabilityGeneInspector getInstance() {
        return INSTANCE;
    }

    public static ResourceLocation KEY = new ResourceLocation(AgriCraft.instance.getModId().toLowerCase(), Names.Objects.GENE_INSPECTOR);

    @CapabilityInject(CapabilityGeneInspector.Impl.class)
    public static final Capability<CapabilityGeneInspector.Impl> CAPABILITY = null;

    private CapabilityGeneInspector() {}

    public boolean canInspect(PlayerEntity player) {
        return this.hasInspectionCapability(player.getItemStackFromSlot(EquipmentSlotType.HEAD));
    }

    public boolean hasInspectionCapability(ItemStack stack) {
        return this.getCapability(stack).map(Impl::isActive).orElse(false);
    }

    public boolean applyInspectionCapability(ItemStack stack) {
        if(stack.getItem() instanceof ArmorItem) {
            return this.getCapability(stack).map(impl -> {
                if(impl.isActive()) {
                    return false;
                }
                impl.setActive(true);
                return true;
            }).orElse(false);
        }
        return false;
    }

    @Override
    public Class<Impl> getCapabilityClass() {
        return Impl.class;
    }

    @Override
    public Capability<Impl> getCapability() {
        return CAPABILITY;
    }

    @Override
    public boolean shouldApplyCapability(ItemStack carrier) {
        return carrier.getItem() instanceof ArmorItem
                && ((ArmorItem) carrier.getItem()).getEquipmentSlot() == EquipmentSlotType.HEAD;
    }

    @Override
    public Impl createNewValue(ItemStack carrier) {
        return new Impl();
    }

    @Override
    public ResourceLocation getCapabilityKey() {
        return KEY;
    }

    @Override
    public Class<ItemStack> getCarrierClass() {
        return ItemStack.class;
    }

    public static class Impl implements ISerializable {
        private boolean flag;

        private Impl() {
            this.flag = false;
        }

        public boolean isActive() {
            return this.flag;
        }

        public void setActive(boolean flag) {
            this.flag = flag;
        }

        @Override
        public void readFromNBT(CompoundNBT tag) {
            this.setActive(tag.contains(AgriNBT.FLAG) && tag.getBoolean(AgriNBT.FLAG));
        }

        @Override
        public CompoundNBT writeToNBT() {
            CompoundNBT tag = new CompoundNBT();
            tag.putBoolean(AgriNBT.FLAG, this.isActive());
            return tag;
        }
    }
}
