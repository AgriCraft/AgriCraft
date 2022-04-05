package com.infinityraider.agricraft.capability;

import com.google.common.collect.Sets;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutation;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.capability.CapabilityResearchedPlants.Impl;
import com.infinityraider.agricraft.network.MessageSyncResearchCapability;
import com.infinityraider.agricraft.plugins.jei.JeiBridge;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.capability.IInfSerializableCapabilityImplementation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

import javax.annotation.Nullable;
import java.util.Set;

public class CapabilityResearchedPlants implements IInfSerializableCapabilityImplementation<Player, Impl> {
    private static final CapabilityResearchedPlants INSTANCE = new CapabilityResearchedPlants();

    public static CapabilityResearchedPlants getInstance() {
        return INSTANCE;
    }

    public static ResourceLocation KEY = new ResourceLocation(AgriCraft.instance.getModId().toLowerCase(), Names.Objects.RESEARCH);

    public static final Capability<Impl> CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    private CapabilityResearchedPlants() {}

    public void researchPlant(@Nullable Player player, IAgriPlant plant) {
        if(player != null) {
            this.getCapability(player).ifPresent(impl -> impl.researchPlant(plant));
        }
    }

    public boolean isPlantResearched(@Nullable Player player, IAgriPlant plant) {
        return player != null && this.getCapability(player).map(impl -> impl.isPlantResearched(plant)).orElse(false);
    }

    public boolean isMutationResearched(@Nullable Player player, IAgriMutation mutation) {
        return player != null && this.getCapability(player).map(impl -> impl.isMutationResearched(mutation)).orElse(false);
    }

    public void configureJei(Player player) {
        this.getCapability(player).ifPresent(Impl::configureJei);
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
    public boolean shouldApplyCapability(Player carrier) {
        return true;
    }

    @Override
    public Impl createNewValue(Player carrier) {
        return new Impl(carrier);
    }

    @Override
    public ResourceLocation getCapabilityKey() {
        return KEY;
    }

    @Override
    public Class<Player> getCarrierClass() {
        return Player.class;
    }

    @Override
    public void copyData(Impl from, Impl to) {
        to.copyFrom(from);
    }

    public static class Impl implements Serializable<Impl> {
        private final Player player;

        private final Set<String> unlockedPlants;

        protected Impl(Player player) {
            this.player = player;
            this.unlockedPlants = Sets.newHashSet();
        }

        public Player getPlayer() {
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
            return this.isPlantResearched(mutation.getChild()) || mutation.getParents().stream().anyMatch(this::isPlantResearched);
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
        public void copyDataFrom(Impl from) {
            this.unlockedPlants.clear();
            this.unlockedPlants.addAll(from.unlockedPlants);
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            ListTag list = new ListTag();
            this.unlockedPlants.stream().map(StringTag::valueOf).forEach(list::add);
            tag.put(AgriNBT.ENTRIES, list);
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag tag) {
            this.unlockedPlants.clear();
            if(tag.contains(AgriNBT.ENTRIES)) {
                ListTag list = tag.getList(AgriNBT.ENTRIES, 8);
                list.forEach(entry -> {
                    if(entry instanceof StringTag) {
                        this.unlockedPlants.add(entry.getAsString());
                    }
                });
            }
        }
    }
}
