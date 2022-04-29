
package com.infinityraider.agricraft.impl.v1;

import com.agricraft.agricore.util.AgriValidator;
import com.agricraft.agricore.util.TypeHelper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.requirement.AgriSeason;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoil;
import com.infinityraider.agricraft.impl.v1.journal.JsonMutationConditionManager;
import com.infinityraider.agricraft.impl.v1.plant.JsonPlantCallbackManager;
import com.infinityraider.infinitylib.utility.TagUtil;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.List;

public class AgriValidatorImpl implements AgriValidator {

    @Override
    public <T> boolean isValidObject(Class<T> token, String object, boolean useTag, List<String> stateDefinition) {
        if (TypeHelper.isType(ItemStack.class, token)) {
            return this.isValidItem(object, useTag);
        }
        if (TypeHelper.isType(BlockState.class, token)) {
            return TagUtil.fetchBlockStates(object, useTag, stateDefinition).size() > 0;
        }
        if (TypeHelper.isType(FluidState.class, token)) {
            return TagUtil.fetchFluidStates(object, useTag, stateDefinition).size() > 0;
        }
        return false;
    }

    @Override
    @Nonnull
    @SuppressWarnings("unchecked")
    public <T> Class<T> getTokenClass(String token) {
        if("item".equalsIgnoreCase(token)) {
            return (Class<T>) ItemStack.class;
        }
        if("block".equalsIgnoreCase(token)) {
            return (Class<T>) BlockState.class;
        }
        if("fluid".equalsIgnoreCase(token)) {
            return (Class<T>) FluidState.class;
        }
        AgriCraft.instance.getLogger().error("Invalid token class: " + token + ", encountered, check your jsons");
        return (Class<T>) Object.class;
    }

    @Override
    public boolean isValidRenderType(String renderType) {
        return AgriCraft.instance.proxy().isValidRenderType(renderType);
    }

    @Override
    public boolean isValidSeason(String season) {
        return AgriSeason.fromString(season).isPresent();
    }

    protected boolean isValidItem(String item, boolean useTag) {
        String[] parts = item.split(":");
        if (parts.length != 2) {
            return false;
        } else if (useTag && TagUtil.isValidTag(ForgeRegistries.ITEMS.tags(), item)) {
            return true;
        } else {
            try {
                return ForgeRegistries.ITEMS.containsKey(new ResourceLocation(parts[0], parts[1]));
            } catch (Exception e) {
                AgriCraft.instance.getLogger().error("Failed to parse item: " + item + ", encountered Exception");
                AgriCraft.instance.getLogger().printStackTrace(e);
                return false;
            }
        }
    }

    protected boolean isValidBlock(String block, boolean useTag, List<String> stateDefinition) {
        String[] parts = block.split(":");
        if (parts.length != 2) {
            return false;
        } else if (useTag && TagUtil.isValidTag(ForgeRegistries.BLOCKS.tags(), block)) {
            return true;
        } else {
            try {
                return ForgeRegistries.BLOCKS.containsKey(new ResourceLocation(parts[0], parts[1]));
            } catch (Exception e) {
                AgriCraft.instance.getLogger().error("Failed to parse block: " + block + ", encountered Exception");
                AgriCraft.instance.getLogger().printStackTrace(e);
                return false;
            }
        }
    }

    protected boolean isValidFluid(String fluid, boolean useTag, List<String> stateDefinition) {
        String[] parts = fluid.split(":");
        if (parts.length != 2) {
            return false;
        } else if (useTag && TagUtil.isValidTag(ForgeRegistries.FLUIDS.tags(), fluid)) {
            return true;
        } else {
            try {
                return ForgeRegistries.FLUIDS.containsKey(new ResourceLocation(parts[0], parts[1]));
            } catch (Exception e) {
                AgriCraft.instance.getLogger().error("Failed to parse fluid: " + fluid + ", encountered Exception");
                AgriCraft.instance.getLogger().printStackTrace(e);
                return false;
            }
        }
    }

    @Override
    public boolean isValidResource(String resource) {
        try {
            if(resource.contains("#")) {
                return new ModelResourceLocation(resource).toString().equalsIgnoreCase(resource);
            } else {
                return new ResourceLocation(resource).toString().equalsIgnoreCase(resource);
            }
        } catch (NullPointerException e) {
            return false;
        }
    }

    @Override
    public boolean isValidPlantCallback(JsonElement callback) {
        return JsonPlantCallbackManager.get(callback).isPresent();
    }

    @Override
    public boolean isValidMutationCondition(String id, JsonObject args) {
        return JsonMutationConditionManager.get(id).isPresent();
    }

    @Override
    public boolean isValidHumidity(String humidity) {
        return IAgriSoil.Humidity.fromString(humidity).isPresent();
    }

    @Override
    public boolean isValidAcidity(String acidity) {
        return IAgriSoil.Acidity.fromString(acidity).isPresent();
    }

    @Override
    public boolean isValidNutrients(String nutrients) {
        return IAgriSoil.Nutrients.fromString(nutrients).isPresent();
    }

    @Override
    public boolean isValidMod(String modid) {
        return modid.equals("minecraft") || ModList.get().isLoaded(modid);
    }

}
