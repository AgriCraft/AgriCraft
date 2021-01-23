package com.infinityraider.agricraft.content.core;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.impl.v1.plant.NoPlant;
import com.infinityraider.agricraft.content.AgriTabs;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.agricraft.render.items.AgriSeedRenderer;
import com.infinityraider.infinitylib.item.ItemBase;
import com.infinityraider.infinitylib.render.item.InfItemRenderer;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.Optional;

public class ItemDynamicAgriSeed extends ItemBase {
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
    public String getTranslationKey(@Nonnull ItemStack stack) {
        return AgriApi.getSeedAdapterizer().valueOf(stack)
                .map(seed -> seed.getPlant().getSeedName())
                .orElse("unknown seed");
    }

    @Nonnull
    public ITextComponent getDisplayName(@Nonnull ItemStack stack) {
        return new StringTextComponent(this.getTranslationKey(stack));
    }

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

    public Optional<AgriSeed> getSeed(ItemStack stack) {
        return this.getGenome(stack).map(AgriSeed::new);
    }

    public IAgriPlant getPlant(ItemStack stack) {
        return this.getGenome(stack).map(IAgriGenome::getPlant).orElse(NO_PLANT);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public InfItemRenderer getItemRenderer() {
        return AgriSeedRenderer.getInstance();
    }
}
