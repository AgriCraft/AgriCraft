package com.infinityraider.agricraft.capability;

import com.google.common.collect.Sets;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutation;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.network.MessageSyncResearchCapability;
import com.infinityraider.agricraft.plugins.jei.JeiBridge;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.capability.IInfSerializableCapabilityImplementation;
import com.infinityraider.infinitylib.utility.ISerializable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import javax.annotation.Nullable;
import java.util.Set;

public class CapabilityResearchedPlants implements IInfSerializableCapabilityImplementation<PlayerEntity, CapabilityResearchedPlants.Impl> {
    private static final CapabilityResearchedPlants INSTANCE = new CapabilityResearchedPlants();

    public static CapabilityResearchedPlants getInstance() {
        return INSTANCE;
    }

    public static ResourceLocation KEY = new ResourceLocation(AgriCraft.instance.getModId().toLowerCase(), Names.Objects.RESEARCH);

    @CapabilityInject(CapabilityResearchedPlants.Impl.class)
    public static final Capability<Impl> CAPABILITY = null;

    private CapabilityResearchedPlants() {}

    public void researchPlant(@Nullable PlayerEntity player, IAgriPlant plant) {
        if(player != null) {
            this.getCapability(player).ifPresent(impl -> impl.researchPlant(plant));
        }
    }

    public boolean isPlantResearched(@Nullable PlayerEntity player, IAgriPlant plant) {
        return player != null && this.getCapability(player).map(impl -> impl.isPlantResearched(plant)).orElse(false);
    }

    public boolean isMutationResearched(@Nullable PlayerEntity player, IAgriMutation mutation) {
        return player != null && this.getCapability(player).map(impl -> impl.isMutationResearched(mutation)).orElse(false);
    }

    public void configureJei() {
        this.getCapability(AgriCraft.instance.getClientPlayer()).ifPresent(Impl::configureJei);
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
    public boolean shouldApplyCapability(PlayerEntity carrier) {
        return true;
    }

    @Override
    public Impl createNewValue(PlayerEntity carrier) {
        return new Impl(carrier);
    }

    @Override
    public ResourceLocation getCapabilityKey() {
        return KEY;
    }

    @Override
    public Class<PlayerEntity> getCarrierClass() {
        return PlayerEntity.class;
    }

    @Override
    public void copyData(Impl from, Impl to) {
        to.copyFrom(from);
    }

    public static class Impl implements ISerializable {
        private final PlayerEntity player;

        private final Set<String> unlockedPlants;

        protected Impl(PlayerEntity player) {
            this.player = player;
            this.unlockedPlants = Sets.newHashSet();
        }

        public PlayerEntity getPlayer() {
            return this.player;
        }

        public void researchPlant(IAgriPlant plant) {
            this.unlockedPlants.add(plant.getId());
            if(AgriCraft.instance.getEffectiveSide().isServer()) {
                new MessageSyncResearchCapability(this).sendTo(this.player);
            }
        }

        public boolean isPlantResearched(IAgriPlant plant) {
            return this.unlockedPlants.contains(plant.getId());
        }

        public boolean isMutationResearched(IAgriMutation mutation) {
            return this.isPlantResearched(mutation.getChild());
        }

        public void configureJei() {
            if(AgriCraft.instance.getEffectiveSide().isClient()) {
                if (AgriCraft.instance.getConfig().progressiveJEI()) {
                    AgriApi.getMutationRegistry().stream().forEach(mutation -> {
                        if (this.isMutationResearched(mutation)) {
                            JeiBridge.unHideMutation(mutation);
                        } else {
                            JeiBridge.hideMutation(mutation);
                        }
                    });
                }
            } else {
                new MessageSyncResearchCapability(this).sendTo(this.player);
            }
        }

        protected void copyFrom(Impl other) {
            if(this == other) {
                return;
            }
            this.unlockedPlants.clear();
            this.unlockedPlants.addAll(other.unlockedPlants);
        }

        @Override
        public void readFromNBT(CompoundNBT tag) {
            this.unlockedPlants.clear();
            if(tag.contains(AgriNBT.ENTRIES)) {
                ListNBT list = tag.getList(AgriNBT.ENTRIES, 8);
                list.forEach(entry -> {
                    if(entry instanceof StringNBT) {
                        this.unlockedPlants.add(entry.getString());
                    }
                });
            }
        }

        @Override
        public CompoundNBT writeToNBT() {
            CompoundNBT tag = new CompoundNBT();
            ListNBT list = new ListNBT();
            this.unlockedPlants.stream().map(StringNBT::valueOf).forEach(list::add);
            tag.put(AgriNBT.ENTRIES, list);
            return tag;
        }
    }
}
