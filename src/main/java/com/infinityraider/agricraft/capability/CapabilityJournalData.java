package com.infinityraider.agricraft.capability;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.content.items.IAgriJournalItem;
import com.infinityraider.agricraft.content.core.ItemJournal;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.capability.IInfSerializableCapabilityImplementation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class CapabilityJournalData implements IInfSerializableCapabilityImplementation<ItemStack, ItemJournal.JournalData> {
    private static final CapabilityJournalData INSTANCE = new CapabilityJournalData();

    public static CapabilityJournalData getInstance() {
        return INSTANCE;
    }

    public static ResourceLocation KEY = new ResourceLocation(AgriCraft.instance.getModId().toLowerCase(), Names.Items.JOURNAL);

    @CapabilityInject(ItemJournal.JournalData.class)
    public static final Capability<ItemJournal.JournalData> CAPABILITY = null;

    private CapabilityJournalData() {}

    @Override
    public Class<ItemJournal.JournalData> getCapabilityClass() {
        return ItemJournal.JournalData.class;
    }

    @Override
    public Capability<ItemJournal.JournalData> getCapability() {
        return CAPABILITY;
    }

    @Override
    public boolean shouldApplyCapability(ItemStack stack) {
        return stack.getItem() instanceof IAgriJournalItem;
    }

    @Override
    public ItemJournal.JournalData createNewValue(ItemStack stack) {
        return ItemJournal.JournalData.createFromLegacyTag(stack);
    }

    @Override
    public ResourceLocation getCapabilityKey() {
        return KEY;
    }

    @Override
    public Class<ItemStack> getCarrierClass() {
        return ItemStack.class;
    }
}

