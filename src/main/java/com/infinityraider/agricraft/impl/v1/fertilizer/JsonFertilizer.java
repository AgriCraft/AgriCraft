package com.infinityraider.agricraft.impl.v1.fertilizer;

import com.agricraft.agricore.templates.AgriFertilizer;
import com.agricraft.agricore.templates.AgriFertilizerEffect;
import com.google.common.base.Preconditions;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizable;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlantProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

public class JsonFertilizer implements IAgriFertilizer {

    private final String id;
    private final Component name;
    private final Collection<Item> variants;

    private final boolean trigger_mutation;
    private final boolean trigger_weeds;
    private final int potency;
    private final AgriFertilizerEffect fertilizerEffect;

    public JsonFertilizer(@Nonnull AgriFertilizer fertilizer) {
        this.id = Preconditions.checkNotNull(fertilizer).getId();
        this.name = new TranslatableComponent(fertilizer.getLangKey());
        this.variants = Collections.unmodifiableCollection(stacksToItems(fertilizer.getVariants(ItemStack.class)));
        this.trigger_mutation = fertilizer.canTriggerMutation();
        this.trigger_weeds = fertilizer.canTriggerWeeds();
        this.potency = fertilizer.getPotency();
        this.fertilizerEffect = fertilizer.getEffect();
    }

    private Collection<Item> stacksToItems(Collection<ItemStack> stacks) {
        return Preconditions.checkNotNull(stacks).stream().map(ItemStack::getItem).collect(Collectors.toList());
    }

    @Nonnull
    @Override
    public String getId() {
        return this.id;
    }

    public Component getName() {
        return this.name;
    }

    @Override
    public boolean canTriggerMutation() {
        return this.trigger_mutation;
    }

    @Override
    public boolean canTriggerWeeds() {
        return this.trigger_weeds;
    }

    @Override
    public InteractionResult applyFertilizer(Level world, BlockPos pos, IAgriFertilizable target, ItemStack stack, Random random, @Nullable LivingEntity entity) {
        if (target instanceof IAgriPlantProvider) {
            IAgriCrop crop = ((IAgriCrop) target);
            String type = "neutral";
            for (int i = 0; i < this.potency; i++) {
                if (fertilizerEffect.isNegativelyAffected(crop.getPlant().getId())) {
                    if (fertilizerEffect.canReduceGrowth() && random.nextBoolean()) {
                        type = "negative";
                        IAgriGrowthStage current = crop.getGrowthStage();
                        IAgriGrowthStage previous = current.getPreviousStage(crop, random);
                        if (current.equals(previous)) {
                            if (fertilizerEffect.canKillPlant()) {
                                crop.removeGenome();
                            }
                        } else {
                            crop.setGrowthStage(previous);
                        }
                    }
                } else {
                    if(crop.hasPlant() && this.fertilizerEffect.canFertilize(crop.getPlant().getId())) {
                        target.applyGrowthTick();
                        type = "positive";
                    } else if(crop.isCrossCrop() && this.canTriggerMutation()) {
                        target.applyGrowthTick();
                        type = "positive";
                    } else if(this.canTriggerWeeds()) {
                        target.applyGrowthTick();
                        type = "positive";
                    }
                }
            }
            this.spawnParticles(world, pos, type, random);
            if ((entity instanceof Player) && !(((Player) entity).isCreative())) {
                stack.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public boolean canFertilize(IAgriFertilizable target) {
        if (!(target instanceof IAgriCrop)) {
            return false;
        }
        return ((IAgriCrop) target).hasPlant() && this.fertilizerEffect.canFertilize(((IAgriCrop) target).getPlant().getId());
    }

    protected void spawnParticles(Level world, BlockPos pos, String type, Random rand) {
        this.fertilizerEffect.getParticles(type)
                .forEach(effect -> {
                    ParticleType<?> particle = ForgeRegistries.PARTICLE_TYPES.getValue(new ResourceLocation(effect.getParticle()));
                    if (!(particle instanceof ParticleOptions)) {
                        return;
                    }
                    for (int amount = 0; amount < effect.getAmount(); ++amount) {
                        double x = pos.getX() + 0.5D + (rand.nextBoolean() ? 1 : -1) * effect.getDeltaX() * rand.nextDouble();
                        double y = pos.getY() + 0.5D + effect.getDeltaY() * rand.nextDouble();
                        double z = pos.getZ() + 0.5D + (rand.nextBoolean() ? 1 : -1) * effect.getDeltaZ() * rand.nextDouble();
                        world.addParticle((ParticleOptions) particle, x, y, z, 0.0D, 0.0D, 0.0D);
                    }
                });
    }


    @Override
    public boolean accepts(@Nullable Object obj) {
        if (obj instanceof Item) {
            return variants.contains(obj);
        }
        if (obj instanceof ItemStack) {
            return accepts(((ItemStack) obj).getItem());
        }
        return false;
    }

    @Nonnull
    @Override
    public Optional<IAgriFertilizer> valueOf(@Nullable Object obj) {
        if (obj instanceof Item && variants.contains(obj)) {
            return Optional.of(this);
        }
        if (obj instanceof ItemStack) {
            return valueOf(((ItemStack) obj).getItem());
        }
        return Optional.empty();
    }
}
