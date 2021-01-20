package com.infinityraider.agricraft.content.core;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.impl.v1.plant.NoPlant;
import com.infinityraider.agricraft.content.AgriTabs;
import com.infinityraider.agricraft.reference.AgriNBT;
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

public class ItemDynamicAgriSeed extends ItemBase {
    private static final IAgriPlant NO_PLANT = NoPlant.getInstance();

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
            AgriApi.getPlantRegistry().stream()
                    .map(IAgriPlant::getSeed)
                    .filter(seed -> seed.getItem() == this)
                    .forEach(items::add);
        }
    }

    public IAgriPlant getPlant(ItemStack stack) {
        if(!stack.hasTag()) {
            return NO_PLANT;
        }
        CompoundNBT tag = stack.getTag();
        return tag.contains(AgriNBT.PLANT) ? AgriApi.getPlantRegistry().get(tag.getString(AgriNBT.PLANT)).orElse(NO_PLANT) : NO_PLANT;
    }
    @Override
    @OnlyIn(Dist.CLIENT)
    public InfItemRenderer getItemRenderer() {
        return AgriSeedRenderer.getInstance();
    }
}
