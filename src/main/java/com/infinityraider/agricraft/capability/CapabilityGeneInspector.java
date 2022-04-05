package com.infinityraider.agricraft.capability;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.capability.CapabilityGeneInspector.Impl;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.capability.IInfSerializableCapabilityImplementation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class CapabilityGeneInspector implements IInfSerializableCapabilityImplementation<ItemStack, Impl> {
    private static final CapabilityGeneInspector INSTANCE = new CapabilityGeneInspector();

    public static CapabilityGeneInspector getInstance() {
        return INSTANCE;
    }

    public static ResourceLocation KEY = new ResourceLocation(AgriCraft.instance.getModId().toLowerCase(), Names.Objects.GENE_INSPECTOR);

    public static final Capability<Impl> CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    private CapabilityGeneInspector() {}

    public boolean canInspect(Player player) {
        return this.hasInspectionCapability(player.getItemBySlot(EquipmentSlot.HEAD));
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
        Item item = carrier.getItem();
        return item instanceof ArmorItem && ((ArmorItem) item).getEquipmentSlot(carrier) == EquipmentSlot.HEAD;
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

    public static class Impl implements Serializable<Impl> {
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
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putBoolean(AgriNBT.FLAG, this.isActive());
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag tag) {
            this.setActive(tag.contains(AgriNBT.FLAG) && tag.getBoolean(AgriNBT.FLAG));
        }

        @Override
        public void copyDataFrom(Impl from) {
            this.setActive(from.isActive());
        }
    }
}
