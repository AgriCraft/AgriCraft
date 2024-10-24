package com.agricraft.agricraft.datagen;

import com.agricraft.agricraft.common.registry.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.common.lib.BotaniaTags;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // Cross-Compatibility
        //     Plants (blocks)
        this.tag(TagKey.create(Registries.BLOCK, new ResourceLocation("agricraft:ores/aluminium")))
                .addOptionalTag(new ResourceLocation("c:aluminum_ores"))
                .addOptionalTag(new ResourceLocation("c:aluminium_ores"))
                .addOptionalTag(new ResourceLocation("forge:ores/aluminum"))
                .addOptionalTag(new ResourceLocation("forge:ores/aluminium"));
        this.tag(TagKey.create(Registries.BLOCK, new ResourceLocation("agricraft:ores/coal")))
                .add(Blocks.COAL_ORE)
                .add(Blocks.DEEPSLATE_COAL_ORE)
                .addOptionalTag(new ResourceLocation("minecraft:coal_ores"))
                .addOptionalTag(new ResourceLocation("c:coal_ores"))
                .addOptionalTag(new ResourceLocation("forge:ores/coal"));
        this.tag(TagKey.create(Registries.BLOCK, new ResourceLocation("agricraft:ores/copper")))
                .add(Blocks.COPPER_ORE)
                .add(Blocks.DEEPSLATE_COPPER_ORE)
                .addOptionalTag(new ResourceLocation("minecraft:copper_ores"))
                .addOptionalTag(new ResourceLocation("c:copper_ores"))
                .addOptionalTag(new ResourceLocation("forge:ores/copper"));
        this.tag(TagKey.create(Registries.BLOCK, new ResourceLocation("agricraft:ores/diamond")))
                .add(Blocks.DIAMOND_ORE)
                .add(Blocks.DEEPSLATE_DIAMOND_ORE)
                .addOptionalTag(new ResourceLocation("minecraft:diamond_ores"))
                .addOptionalTag(new ResourceLocation("c:diamond_ores"))
                .addOptionalTag(new ResourceLocation("forge:ores/diamond"));
        this.tag(TagKey.create(Registries.BLOCK, new ResourceLocation("agricraft:ores/emerald")))
                .add(Blocks.EMERALD_ORE)
                .add(Blocks.DEEPSLATE_EMERALD_ORE)
                .addOptionalTag(new ResourceLocation("minecraft:emerald_ores"))
                .addOptionalTag(new ResourceLocation("c:emerald_ores"))
                .addOptionalTag(new ResourceLocation("forge:ores/emerald"));
        this.tag(TagKey.create(Registries.BLOCK, new ResourceLocation("agricraft:ores/gold")))
                .add(Blocks.GOLD_ORE)
                .add(Blocks.DEEPSLATE_GOLD_ORE)
                .addOptionalTag(new ResourceLocation("minecraft:gold_ores"))
                .addOptionalTag(new ResourceLocation("c:gold_ores"))
                .addOptionalTag(new ResourceLocation("forge:ores/gold"));
        this.tag(TagKey.create(Registries.BLOCK, new ResourceLocation("agricraft:ores/iron")))
                .add(Blocks.IRON_ORE)
                .add(Blocks.DEEPSLATE_IRON_ORE)
                .addOptionalTag(new ResourceLocation("minecraft:iron_ores"))
                .addOptionalTag(new ResourceLocation("c:iron_ores"))
                .addOptionalTag(new ResourceLocation("forge:ores/iron"));
        this.tag(TagKey.create(Registries.BLOCK, new ResourceLocation("agricraft:ores/lapis")))
                .add(Blocks.LAPIS_ORE)
                .add(Blocks.DEEPSLATE_LAPIS_ORE)
                .addOptionalTag(new ResourceLocation("minecraft:lapis_ores"))
                .addOptionalTag(new ResourceLocation("c:lapis_ores"))
                .addOptionalTag(new ResourceLocation("forge:ores/lapis"));
        this.tag(TagKey.create(Registries.BLOCK, new ResourceLocation("agricraft:ores/lead")))
                .addOptionalTag(new ResourceLocation("c:lead_ores"))
                .addOptionalTag(new ResourceLocation("forge:ores/lead"));
        this.tag(TagKey.create(Registries.BLOCK, new ResourceLocation("agricraft:ores/netherite_scrap")))
                .add(Blocks.ANCIENT_DEBRIS)
                .addOptionalTag(new ResourceLocation("c:netherite_scrap_ores"))
                .addOptionalTag(new ResourceLocation("forge:ores/netherite_scrap"));
        this.tag(TagKey.create(Registries.BLOCK, new ResourceLocation("agricraft:ores/nickel")))
                .addOptionalTag(new ResourceLocation("c:nickel_ores"))
                .addOptionalTag(new ResourceLocation("forge:ores/nickel"));
        this.tag(TagKey.create(Registries.BLOCK, new ResourceLocation("agricraft:ores/osmium")))
                .addOptionalTag(new ResourceLocation("c:osmium_ores"))
                .addOptionalTag(new ResourceLocation("forge:ores/osmium"));
        this.tag(TagKey.create(Registries.BLOCK, new ResourceLocation("agricraft:ores/platinum")))
                .addOptionalTag(new ResourceLocation("c:platinum_ores"))
                .addOptionalTag(new ResourceLocation("forge:ores/platinum"));
        this.tag(TagKey.create(Registries.BLOCK, new ResourceLocation("agricraft:ores/quartz")))
                .add(Blocks.NETHER_QUARTZ_ORE)
                .addOptionalTag(new ResourceLocation("c:quartz_ores"))
                .addOptionalTag(new ResourceLocation("forge:ores/quartz"));
        this.tag(TagKey.create(Registries.BLOCK, new ResourceLocation("agricraft:ores/redstone")))
                .add(Blocks.REDSTONE_ORE)
                .add(Blocks.DEEPSLATE_REDSTONE_ORE)
                .addOptionalTag(new ResourceLocation("c:redstone_ores"))
                .addOptionalTag(new ResourceLocation("forge:ores/redstone"));
        this.tag(TagKey.create(Registries.BLOCK, new ResourceLocation("agricraft:ores/tin")))
                .addOptionalTag(new ResourceLocation("c:tin_ores"))
                .addOptionalTag(new ResourceLocation("forge:ores/tin"));

        this.tag(BotaniaTags.Blocks.AGRICARNATION_GROWTH_CANDIDATE)
                .add(ModBlocks.CROP.get());
    }
}
