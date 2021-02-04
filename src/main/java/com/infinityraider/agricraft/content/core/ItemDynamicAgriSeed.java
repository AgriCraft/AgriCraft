package com.infinityraider.agricraft.content.core;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.api.v1.items.IAgriSeedItem;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatProvider;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatsMap;
import com.infinityraider.agricraft.impl.v1.plant.NoPlant;
import com.infinityraider.agricraft.content.AgriTabs;
import com.infinityraider.agricraft.reference.AgriToolTips;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.agricraft.render.items.AgriSeedRenderer;
import com.infinityraider.infinitylib.item.ItemBase;
import com.infinityraider.infinitylib.render.item.InfItemRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class ItemDynamicAgriSeed extends ItemBase implements IAgriSeedItem {
    private static final IAgriPlant NO_PLANT = NoPlant.getInstance();

    public static ItemStack toStack(AgriSeed seed, int amount) {
        return toStack(seed.getGenome(), amount);
    }

    public static ItemStack toStack(IAgriPlant plant, int amount) {
        return toStack(AgriApi.getAgriGenomeBuilder(plant).build(), amount);
    }

    public static ItemStack toStack(IAgriGenome genome, int amount) {
        // Create the stack.
        ItemStack stack = new ItemStack(AgriCraft.instance.getModItemRegistry().seed, amount);
        // Create the tag.
        CompoundNBT tag = new CompoundNBT();
        genome.writeToNBT(tag);
        // Put the tag on stack
        stack.setTag(tag);
        // Return the stack
        return stack;
    }

    public ItemDynamicAgriSeed() {
        super(Names.Items.SEED, AgriCraft.instance.proxy().setItemRenderer(new Properties()
                .group(AgriTabs.TAB_AGRICRAFT_SEED))
        );
    }

    @Nonnull
    public ITextComponent getDisplayName(@Nonnull ItemStack stack) {
        return this.getPlant(stack).getSeedName();
    }

    @Override
    public void fillItemGroup(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
        if(this.isInGroup(group)) {
            items.clear();
            AgriApi.getPlantRegistry()
                    .stream()
                    .map(IAgriPlant::toAgriSeed)
                    .map(seed -> toStack(seed, 1))
                    .forEach(items::add);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag advanced) {
        this.getStats(stack).map(stats -> {
            stats.addTooltips(tooltip::add);
            return true;
        }).orElseGet(() -> {
            tooltip.add(AgriToolTips.UNKNOWN);
            return false;
        });
    }

    @Override
    public Optional<IAgriGenome> getGenome(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if(tag == null) {
            return Optional.empty();
        }
        IAgriGenome genome = AgriApi.getAgriGenomeBuilder(NO_PLANT).build();
        if(!genome.readFromNBT(tag)) {
            // Faulty NBT
            stack.setTag(null);
            return Optional.empty();
        }
        return Optional.of(genome);
    }

    @Override
    public Optional<AgriSeed> getSeed(ItemStack stack) {
        return this.getGenome(stack).map(AgriSeed::new);
    }

    @Override
    public IAgriPlant getPlant(ItemStack stack) {
        return this.getGenome(stack).map(IAgriGenome::getPlant).orElse(NO_PLANT);
    }

    @Override
    public Optional<IAgriStatsMap> getStats(ItemStack stack) {
        return this.getGenome(stack).map(IAgriStatProvider::getStats);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public InfItemRenderer getItemRenderer() {
        return AgriSeedRenderer.getInstance();
    }
}
