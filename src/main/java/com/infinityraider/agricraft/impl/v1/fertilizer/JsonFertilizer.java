package com.infinityraider.agricraft.impl.v1.fertilizer;

import com.agricraft.agricore.plant.fertilizer.AgriFertilizer;
import com.google.common.base.Preconditions;
import com.infinityraider.agricraft.api.v1.adapter.IAgriAdapter;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizable;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

public class JsonFertilizer implements IAgriFertilizer, IAgriAdapter {

    private final String id;
    private final ITextComponent name;
    private final Collection<Item> variants;

    private final boolean trigger_mutation;
    private final boolean trigger_weeds;
    private final int potency;

    public JsonFertilizer(@Nonnull AgriFertilizer fertilizer) {
        this.id = Preconditions.checkNotNull(fertilizer).getId();
        this.name = new TranslationTextComponent(fertilizer.getLangKey());
        this.variants = Collections.unmodifiableCollection(stacksToItems(fertilizer.getVariants(ItemStack.class)));
        this.trigger_mutation = fertilizer.canTriggerMutation();
        this.trigger_weeds = fertilizer.canTriggerWeeds();
        this.potency = fertilizer.getPotency();
    }

    private Collection<Item> stacksToItems(Collection<ItemStack> stacks) {
        return Preconditions.checkNotNull(stacks).stream().map(ItemStack::getItem).collect(Collectors.toList());
    }

    @Nonnull
    @Override
    public String getId() {
        return this.id;
    }

    @Nonnull
    @Override
    public Collection<Item> getVariants() {
        return this.variants;
    }

    @Override
    public boolean isFertilizer() {
        return true;
    }

    public ITextComponent getName() {
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
    public int getPotency() {
        return this.potency;
    }

    @Override
    public ActionResultType applyFertilizer(World world, BlockPos pos, IAgriFertilizable target, ItemStack stack, Random random, @Nullable LivingEntity entity) {
        for (int i = 0; i < this.potency; i++) {
            target.applyGrowthTick();
        }
        if((entity instanceof PlayerEntity) && !(((PlayerEntity) entity).isCreative())) {
            stack.shrink(1);
        }
        // to spawn the bone meal particles
        world.playEvent(2005, pos, 0);
        return ActionResultType.SUCCESS;
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
