
package com.infinityraider.agricraft.impl.v1;

import com.agricraft.agricore.util.AgriValidator;
import com.agricraft.agricore.util.TypeHelper;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.impl.v1.plant.JsonPlantCallback;
import com.infinityraider.agricraft.util.TagUtil;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;

public class ModValidator implements AgriValidator {

    @Override
    public <T> boolean isValidObject(Class<T> token, String object) {
        if (TypeHelper.isType(ItemStack.class, token)) {
            return this.isValidItem(object);
        }
        if (TypeHelper.isType(BlockState.class, token)) {
            return this.isValidBlock(object);
        }
        if (TypeHelper.isType(FluidState.class, token)) {
            return this.isValidFluid(object);
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

    protected boolean isValidItem(String item) {
        String[] parts = item.split(":");
        if (parts.length != 2) {
            return false;
        } else if (TagUtil.isValidTag(ItemTags.getCollection(), item)) {
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

    protected boolean isValidBlock(String block) {
        String[] parts = block.split(":");
        if (parts.length != 2) {
            return false;
        } else if (TagUtil.isValidTag(BlockTags.getCollection(), block)) {
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

    protected boolean isValidFluid(String fluid) {
        String[] parts = fluid.split(":");
        if (parts.length != 2) {
            return false;
        } else if (TagUtil.isValidTag(FluidTags.getCollection(), fluid)) {
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
    public boolean isValidCallback(String callback) {
        return JsonPlantCallback.get(callback).isPresent();
    }

    @Override
    public boolean isValidMod(String modid) {
        return modid.equals("minecraft") || ModList.get().isLoaded(modid);
    }

}
