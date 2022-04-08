package com.infinityraider.agricraft.plugins.immersiveengineering;

import blusunrize.immersiveengineering.api.crafting.ClocheRenderFunction;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.content.items.IAgriSeedItem;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.impl.v1.plant.NoPlant;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Transformation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.function.Consumer;

public class AgriRenderFunction implements ClocheRenderFunction {
    public static final AgriRenderFunction INSTANCE = new AgriRenderFunction();

    @Override
    public float getScale(ItemStack seed, float growth) {
        return 1;
    }

    @Override
    public Collection<Pair<BlockState, Transformation>> getBlocks(ItemStack stack, float growth) {
        return Collections.emptyList();
    }

    @Override
    public void injectQuads(ItemStack stack, float growth, Consumer<?> quadConsumer) {
        IAgriPlant plant = this.getPlant(stack);
        if(plant.isPlant()) {
            ImmersiveEngineeringCompatClient.injectPlantQuads(plant, this.getGrowthStage(plant, growth), quadConsumer);
        }
    }

    protected IAgriPlant getPlant(ItemStack stack) {
        if(stack.getItem() instanceof IAgriSeedItem) {
            return ((IAgriSeedItem) stack.getItem()).getPlant(stack);
        }
        return NoPlant.getInstance();
    }

    protected IAgriGrowthStage getGrowthStage(IAgriPlant plant, float growth) {
        return plant.getGrowthStages().stream()
                .filter(stage -> stage.growthPercentage() >= growth)
                .min(Comparator.comparingDouble(IAgriGrowthStage::growthPercentage))
                .orElse(plant.getFinalStage());
    }
}
