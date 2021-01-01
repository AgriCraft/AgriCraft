package com.infinityraider.agricraft.items;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.adapter.IAgriAdapter;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import com.infinityraider.agricraft.items.tabs.AgriTabs;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.infinitylib.item.ItemBase;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.infinityraider.infinitylib.utility.NBTHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemAgriSeed extends ItemBase implements IAgriAdapter<AgriSeed> {

    /**
     * This constructor shouldn't be called from anywhere except from the BlockModPlant public
     * constructor, if you create a new BlockModPlant, its constructor will create the seed for you
     */
    public ItemAgriSeed() {
        super("agri_seed");
        this.setCreativeTab(AgriTabs.TAB_AGRICRAFT_SEED);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
        if (tab == this.getCreativeTab() || tab == CreativeTabs.SEARCH) {
            final PlantStats baseStat = new PlantStats();
            for (IAgriPlant plant : AgriApi.getPlantRegistry().all()) {
                if (plant.getSeedItems().stream().anyMatch(s -> s.isItemEqual(this))) {
                    ItemStack stack = new ItemStack(this);
                    NBTTagCompound tag = new NBTTagCompound();
                    tag.setString(AgriNBT.SEED, plant.getId());
                    baseStat.writeToNBT(tag);
                    stack.setTagCompound(tag);
                    list.add(stack);
                }
            }
        }
    }

    @Override
    public boolean getHasSubtypes() {
        return true;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        final Optional<AgriSeed> seed = AgriApi.getSeedRegistry().valueOf(stack);
        return seed.map(s -> s.getPlant().getSeedName()).orElse("Generic Seeds");
    }

    @Override
    public List<String> getIgnoredNBT() {
        List<String> tags = super.getIgnoredNBT();
        tags.add(PlantStats.NBT_ANALYZED);
        tags.add(PlantStats.NBT_GROWTH);
        tags.add(PlantStats.NBT_GAIN);
        tags.add(PlantStats.NBT_STRENGTH);
        return tags;
    }

    @Override
    public boolean accepts(Object obj) {
        CompoundNBT tag = NBTHelper.asTag(obj);
        return tag != null && tag.hasKey(AgriNBT.SEED) && AgriApi.getStatRegistry().hasAdapter(tag);
    }

    @Override
    public Optional<AgriSeed> valueOf(Object obj) {
        CompoundNBT tag = NBTHelper.asTag(obj);
        if (tag == null) {
            return Optional.empty();
        }
        IAgriPlant plant = AgriApi.getPlantRegistry().get(tag.getString(AgriNBT.SEED)).orElse(null);
        IAgriStat stat = AgriApi.getStatRegistry().valueOf(tag).orElse(null);
        if (plant != null && stat != null) {
            return Optional.of(new AgriSeed(plant, stat));
        } else {
            return Optional.empty();
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public String getModelId(ItemStack stack) {
        Optional<AgriSeed> seed = AgriApi.getSeedRegistry().valueOf(stack);
        return seed.map(s -> s.getPlant().getId()).orElse("");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public String getBaseTexture(ItemStack stack) {
        return AgriApi.getSeedRegistry().valueOf(stack)
                .map(s -> s.getPlant().getSeedTexture().toString())
                .orElse("agricraft:items/seed_unknown");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<ResourceLocation> getAllTextures() {
        final Collection<IAgriPlant> plants = AgriApi.getPlantRegistry().all();
        final List<ResourceLocation> textures = new ArrayList<>(plants.size());
        textures.add(new ResourceLocation("agricraft:items/seed_unknown"));
        for (IAgriPlant p : AgriApi.getPlantRegistry().all()) {
            textures.add(p.getSeedTexture());
        }
        return textures;
    }
}
