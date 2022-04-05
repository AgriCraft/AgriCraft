package com.infinityraider.agricraft.capability;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.content.items.IAgriJournalItem;
import com.infinityraider.agricraft.content.core.ItemJournal;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.capability.IInfSerializableCapabilityImplementation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class CapabilityJournalData implements IInfSerializableCapabilityImplementation<ItemStack, ItemJournal.JournalData> {
    private static final CapabilityJournalData INSTANCE = new CapabilityJournalData();

    public static CapabilityJournalData getInstance() {
        return INSTANCE;
    }

    public static ResourceLocation KEY = new ResourceLocation(AgriCraft.instance.getModId().toLowerCase(), Names.Items.JOURNAL);

    public static final Capability<ItemJournal.JournalData> CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

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
        return new ItemJournal.JournalData(stack);
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

