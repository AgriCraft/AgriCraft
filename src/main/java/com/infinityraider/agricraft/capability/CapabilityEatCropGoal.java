package com.infinityraider.agricraft.capability;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.plugins.minecraft.GeneAnimalAttractant;
import com.infinityraider.infinitylib.capability.IInfCapabilityImplementation;
import net.minecraft.entity.MobEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import javax.annotation.Nullable;
import java.util.Optional;

public class CapabilityEatCropGoal implements IInfCapabilityImplementation<MobEntity, CapabilityEatCropGoal.Impl> {
    private static final CapabilityEatCropGoal INSTANCE = new CapabilityEatCropGoal();

    public static CapabilityEatCropGoal getInstance() {
        return INSTANCE;
    }

    public static ResourceLocation KEY = new ResourceLocation(AgriCraft.instance.getModId().toLowerCase(), "eat_crop_goal");

    @CapabilityInject(Impl.class)
    public static final Capability<Impl> CAPABILITY = null;

    public GeneAnimalAttractant.EatCropGoal getCropEatGoal(MobEntity entity) {
        return this.getCapability(entity).map(impl -> impl)
                .flatMap(impl -> Optional.ofNullable(impl.getGoal())).orElse(null);
    }

    public void setCropEatGoal(MobEntity entity, GeneAnimalAttractant.EatCropGoal goal) {
        this.getCapability(entity).ifPresent(impl -> impl.setGoal(goal));
    }

    @Override
    public Capability<Impl> getCapability() {
        return CAPABILITY;
    }

    @Override
    public boolean shouldApplyCapability(MobEntity carrier) {
        return true;
    }

    @Override
    public Impl createNewValue(MobEntity carrier) {
        return new Impl();
    }

    @Override
    public ResourceLocation getCapabilityKey() {
        return KEY;
    }

    @Override
    public Class<MobEntity> getCarrierClass() {
        return MobEntity.class;
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
        from.setGoal(to.getGoal());
    }

    public static final class Impl {
        private GeneAnimalAttractant.EatCropGoal goal;

        private Impl() {
            this.goal = null;
        }

        @Nullable
        public GeneAnimalAttractant.EatCropGoal getGoal() {
            return this.goal;
        }

        public void setGoal(GeneAnimalAttractant.EatCropGoal goal) {
            this.goal = goal;
        }

    }
}
