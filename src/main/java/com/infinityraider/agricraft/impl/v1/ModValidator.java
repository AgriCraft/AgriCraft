
package com.infinityraider.agricraft.impl.v1;

import com.agricraft.agricore.util.AgriValidator;
import com.infinityraider.agricraft.util.TagUtil;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

public class ModValidator implements AgriValidator {

    @Override
    public boolean isValidBlock(String block) {
        String[] parts = block.split(":");
        if (parts.length != 2) {
            return false;
        } else if (TagUtil.isValidTag(BlockTags.getCollection(), block)) {
            return true;
        } else {
            return ForgeRegistries.BLOCKS.containsKey(new ResourceLocation(parts[0],parts[1]));
        }
    }

    @Override
    public boolean isValidItem(String item) {
        String[] parts = item.split(":");
        if (parts.length != 2) {
            return false;
        } else if (TagUtil.isValidTag(ItemTags.getCollection(), item)) {
            return true;
        } else {
            return ForgeRegistries.ITEMS.containsKey(new ResourceLocation(parts[0],parts[1]));
        }
    }

    @Override
    public boolean isValidTexture(String texture) {
        try {
            return new ResourceLocation(texture).toString().equalsIgnoreCase(texture);
        } catch (NullPointerException e) {
            return false;
        }
    }

    @Override
    public boolean isValidMod(String modid) {
        return modid.equals("minecraft") || ModList.get().isLoaded(modid);
    }

}
