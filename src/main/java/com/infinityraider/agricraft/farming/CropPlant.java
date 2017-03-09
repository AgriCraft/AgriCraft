package com.infinityraider.agricraft.farming;

import com.infinityraider.agricraft.api.plant.IAgriPlant;
import com.infinityraider.agricraft.api.requirement.IGrowthRequirement;
import com.infinityraider.agricraft.api.util.FuzzyStack;
import com.infinityraider.agricraft.init.AgriItems;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.renderers.PlantRenderer;
import com.infinityraider.infinitylib.render.tessellation.ITessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;
import java.util.function.Function;

/**
 * The main class used by TileEntityCrop. Only make one object of this per seed
 * object, and register using the plant registry. ICropPlant is implemented to
 * be able to read data from this class from the API
 */
public abstract class CropPlant implements IAgriPlant {

    private final IGrowthRequirement growthRequirement;

    private boolean blackListed;
    private boolean ignoreVanillaPlantingRule;

    public CropPlant(IGrowthRequirement requirement) {
        this.growthRequirement = Objects.requireNonNull(requirement, "A CropPlant's growth requirement may not be null!");
        this.blackListed = false;
        this.ignoreVanillaPlantingRule = false;
    }

    // =========================================================================
    // Misc. Methods
    // <editor-fold>
    // =========================================================================
    /**
     * Gets the growth rate for this CropPlant, used in calculations on growth
     * tick
     *
     * @return the growth rate
     */
    public final int getGrowthRate() {
        int tier = getTier();

        if (tier > 0 && tier <= Constants.GROWTH_TIER.length) {
            return Constants.GROWTH_TIER[tier];
        } else {
            return Constants.GROWTH_TIER[0];
        }
    }

    /**
     * @return if the plant is blacklisted
     */
    public final boolean isBlackListed() {
        return blackListed;
    }

    /**
     * Sets the blacklist status for this plant
     *
     * @param status true if it should be blacklisted, false if not
     */
    public final void setBlackListStatus(boolean status) {
        this.blackListed = status;
    }

    /**
     * Checks if this plant ignores the rule to disabled vanilla planting true
     * means that the seed for this plant can still be planted even though
     * vanilla planting is disabled
     *
     * @return if this ignores the vanilla planting rule or not
     */
    public final boolean ingoresVanillaPlantingRule() {
        return ignoreVanillaPlantingRule;
    }

    /**
     * Sets if this plant should ignore the rule to disabled vanilla planting
     * true means that the seed for this plant can still be planted even though
     * vanilla planting is disabled
     *
     * @param value if this ignores the vanilla planting rule or not
     */
    public final void setIgnoreVanillaPlantingRule(boolean value) {
        this.ignoreVanillaPlantingRule = value;
    }
    // =========================================================================
    // </editor-fold>
    // =========================================================================

    // =========================================================================
    // IAgriPlant Method Implementations
    // <editor-fold>
    // =========================================================================
    @Override
    public Collection<FuzzyStack> getSeedItems() {
        return Arrays.asList(new FuzzyStack(new ItemStack(AgriItems.getInstance().AGRI_SEED)));
    }

    @Override
    public final ItemStack getSeed() {
        ItemStack stack = this.getSeedItems().stream()
                .map(s -> s.toStack())
                .findFirst()
                .orElse(new ItemStack(AgriItems.getInstance().AGRI_SEED));
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString(AgriNBT.SEED, this.getId());
        new PlantStats().writeToNBT(tag);
        stack.setTagCompound(tag);
        return stack;
    }

    @Override
    public final IGrowthRequirement getGrowthRequirement() {
        return this.growthRequirement;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public List<BakedQuad> getPlantQuads(IExtendedBlockState state, int growthStage, EnumFacing direction, Function<ResourceLocation, TextureAtlasSprite> textureToIcon) {
        //The quads returned from this method are added to the tessellator,
        // however the plant renderer directly adds them to the tessellator, so an empty list is returned
        if (textureToIcon instanceof ITessellator) {
            PlantRenderer.renderPlant((ITessellator) textureToIcon, this, growthStage);
        }
        return Collections.emptyList();
    }

    // =========================================================================
    // </editor-fold>
    // =========================================================================
}
