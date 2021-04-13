package com.infinityraider.agricraft.plugins.jei;

import com.google.common.collect.Lists;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoil;
import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@OnlyIn(Dist.CLIENT)
public class AgriIngredientSoil {
    public static final IIngredientType<Block> TYPE = () -> Block.class;

    private static final IIngredientHelper<Block> HELPER = new IIngredientHelper<Block>() {
        @Override
        public Block getMatch(Iterable<Block> iterable, @Nonnull Block soil) {
            return StreamSupport.stream(iterable.spliterator(), false)
                    .filter(aSoil -> aSoil == soil)
                    .findFirst().orElse(soil);
        }

        @Override
        public String getDisplayName(Block soil) {
            return this.getResourceId(soil);
        }

        @Override
        public String getUniqueId(Block soil) {
            return this.getResourceId(soil);
        }

        @Override
        public String getModId(Block soil) {
            return soil.getRegistryName().getNamespace();
        }

        @Override
        public String getResourceId(Block soil) {
            return AgriApi.getSoilRegistry().valueOf(soil.getDefaultState())
                    .map(IAgriSoil::getId)
                    .orElse("Unknown Soil");
        }

        @Nonnull
        @Override
        public Block copyIngredient(@Nonnull Block soil) {
            return soil;
        }

        @Override
        public String getErrorInfo(@Nullable Block soil) {
            return soil == null || soil.getRegistryName() == null ? "UNKNOWN SOIL" : soil.getRegistryName().toString();
        }
    };

    private static final IAgriIngredientRenderer<Block> RENDERER = new IAgriIngredientRenderer<Block>() {
        @Override
        public void render(@Nonnull MatrixStack transform, int x, int y, @Nullable Block soil) {
            if(soil == null) {
                return;
            }
            IJeiRuntime jei =  JeiPlugin.getJei();
            if(jei != null) {
                jei.getIngredientManager().getIngredientRenderer(VanillaTypes.ITEM).render(transform, x, y, new ItemStack(soil));
            }
        }

        @Override
        public List<ITextComponent> getTooltip(Block block, @Nonnull ITooltipFlag iTooltipFlag) {
            List<ITextComponent> tooltip = Lists.newArrayList();
            AgriApi.getSoilRegistry().valueOf(block.getDefaultState()).ifPresent(soil -> soil.addDisplayInfo(tooltip::add));
            return tooltip;
        }
    };

    public static void register(IModIngredientRegistration registration) {
        registration.register(
                TYPE,
                AgriApi.getSoilRegistry().all().stream()
                        .flatMap(soil -> soil.getVariants().stream())
                        .map(BlockState::getBlock)
                        .collect(Collectors.toSet()),
                HELPER,
                RENDERER);
    }
}
