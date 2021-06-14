package com.infinityraider.agricraft.util;

import com.google.common.base.Preconditions;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import com.infinityraider.agricraft.impl.v1.requirement.RequirementCache;
import com.infinityraider.agricraft.impl.v1.stats.AgriStatRegistry;
import com.infinityraider.agricraft.reference.AgriToolTips;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class CropHelper {
    public static boolean rollForWeedAction(IAgriCrop crop) {
        if(AgriCraft.instance.getConfig().disableWeeds()) {
            return false;
        }
        World world = crop.world();
        if(world == null) {
            return false;
        }
        if(crop.hasPlant()) {
            int resist = crop.getStats().getResistance();
            int max = AgriStatRegistry.getInstance().resistanceStat().getMax();
            // At 1 resist, 50/50 chance for weed growth tick
            // At 10 resist, 0% chance
            return  world.getRandom().nextInt(max) >= (max + resist)/2;
        }
        return world.getRandom().nextBoolean();
    }

    public static boolean checkGrowthSpace(IAgriCrop crop) {
        World world = crop.world();
        if(world == null) {
            return false;
        }
        IAgriPlant plant = crop.getPlant();
        IAgriGrowthStage stage = crop.getGrowthStage();
        double height = plant.getPlantHeight(stage);
        while(height > 16) {
            int offset = ((int) height) / 16;
            BlockPos pos = crop.getPosition().up(offset);
            BlockState state = world.getBlockState(pos);
            if(!state.getBlock().isAir(state, world, pos)) {
                return false;
            }
            height -= 16;
        }
        return true;
    }

    public static void spawnWeeds(IAgriCrop crop) {
        World world = crop.world();
        if(world == null) {
            return;
        }
        AgriApi.getWeedRegistry().stream()
                .filter(IAgriWeed::isWeed)
                .filter(weed -> world.getRandom().nextDouble() < weed.spawnChance(crop))
                .findAny()
                .ifPresent(weed -> crop.setWeed(weed, weed.getInitialGrowthStage()));
    }

    public static void spreadWeeds(IAgriCrop crop) {
        if(AgriCraft.instance.getConfig().allowAggressiveWeeds() && crop.getWeeds().isAggressive()) {
            crop.streamNeighbours()
                    .filter(IAgriCrop::isValid)
                    .filter(nb -> !nb.hasWeeds())
                    .filter(CropHelper::rollForWeedAction)
                    .forEach(nb -> nb.setWeed(crop.getWeeds(), crop.getWeeds().getInitialGrowthStage()));
        }
    }

    public static void executePlantHarvestRolls(IAgriCrop crop, @Nonnull Consumer<ItemStack> consumer) {
        World world = crop.world();
        if(world == null) {
            return;
        }
        for (int trials = (crop.getStats().getGain() + 3) / 3; trials > 0; trials--) {
            crop.getPlant().getHarvestProducts(consumer, crop.getGrowthStage(), crop.getStats(), world.getRandom());
        }
    }

    public static void addDisplayInfo(IAgriCrop crop, RequirementCache requirement, @Nonnull Consumer<ITextComponent> consumer) {
        // Validate
        Preconditions.checkNotNull(consumer);

        // Add plant information
        if (crop.hasPlant()) {
            //Add the plant data.
            consumer.accept(AgriToolTips.getPlantTooltip(crop.getPlant()));
            consumer.accept(AgriToolTips.getGrowthTooltip(crop.getGrowthStage(), crop.getGrowthPercentage()));
            //Add the stats
            crop.getStats().addTooltips(consumer);
            //Add the fertility information.
            requirement.addTooltip(consumer);
        } else {
            consumer.accept(AgriToolTips.NO_PLANT);
        }

        // Add weed information
        if(crop.hasWeeds()) {
            consumer.accept(AgriToolTips.getWeedTooltip(crop.getWeeds()));
            consumer.accept(AgriToolTips.getWeedGrowthTooltip(crop.getWeedGrowthPercentage()));
        } else {
            consumer.accept(AgriToolTips.NO_WEED);
        }

        // Add Soil Information
        crop.getSoil().map(soil -> {
            consumer.accept(AgriToolTips.getSoilTooltip(soil));
            return true;
        }).orElseGet(() -> {
            consumer.accept(AgriToolTips.getUnknownTooltip(AgriToolTips.SOIL));
            return false;
        });
    }
}
