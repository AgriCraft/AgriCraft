package com.infinityraider.agricraft.capability;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.items.IAgriJournalItem;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.capability.IInfCapabilityImplementation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Class to keep track of players reading the journal or not
 */
@OnlyIn(Dist.CLIENT)
public class CapabilityJournalReader implements IInfCapabilityImplementation<ItemStack, CapabilityJournalReader.Impl> {
    private static final CapabilityJournalReader INSTANCE = new CapabilityJournalReader();

    public static CapabilityJournalReader getInstance() {
        return INSTANCE;
    }

    public static ResourceLocation KEY = new ResourceLocation(AgriCraft.instance.getModId().toLowerCase(), Names.Items.JOURNAL);

    @CapabilityInject(Impl.class)
    public static final Capability<Impl> CAPABILITY = null;

    public boolean isReading(ItemStack journal, @Nullable PlayerEntity player) {
        return player != null && this.getReader(journal) == player;
    }

    @Nullable
    public PlayerEntity getReader(ItemStack journal) {
        return this.getCapability(journal).map(impl -> impl).flatMap(impl -> Optional.ofNullable(impl.getReader())).orElse(null);
    }

    public void setReader(ItemStack journal, @Nullable PlayerEntity player) {
        this.getCapability(journal).ifPresent(impl -> impl.setReader(player));
    }

    public void toggleReader(ItemStack journal, PlayerEntity player) {
        PlayerEntity reader = this.getReader(journal);
        if(reader == null) {
            this.setReader(journal, player);
        } else {
            if(player == reader) {
                this.setReader(journal, null);
            } else {
                this.setReader(journal, player);
            }
        }
    }

    @Override
    public Capability<Impl> getCapability() {
        return CAPABILITY;
    }

    @Override
    public boolean shouldApplyCapability(ItemStack carrier) {
        return carrier.getItem() instanceof IAgriJournalItem;
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

    @Override
    public Class<Impl> getCapabilityClass() {
        return Impl.class;
    }

    @Override
    public Serializer<Impl> getSerializer() {
        return new Serializer<Impl>() {
            @Override
            public CompoundNBT writeToNBT(Impl object) {
                return new CompoundNBT();
            }

            @Override
            public void readFromNBT(Impl object, CompoundNBT nbt) {
                //NOOP
            }
        };
    }

    @Override
    public void copyData(Impl from, Impl to) {
        from.setReader(to.getReader());
    }

    public static final class Impl {
        private PlayerEntity reader;

        private Impl() {
            this.reader = null;
        }

        @Nullable
        public PlayerEntity getReader() {
            return this.reader;
        }

        public void setReader(@Nullable PlayerEntity reader) {
            this.reader = reader;
        }

    }
}
