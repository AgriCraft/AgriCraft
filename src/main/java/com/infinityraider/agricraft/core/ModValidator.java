/*
 */
package com.infinityraider.agricraft.core;

import com.agricraft.agricore.util.AgriValidator;
import com.infinityraider.agricraft.utility.OreDictUtil;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;

/**
 *
 *
 */
public class ModValidator implements AgriValidator {

    @Override
    public boolean isValidBlock(String block) {
        String[] parts = block.split(":");
        if (parts.length < 2) {
            return false;
        } else if (OreDictUtil.isValidOre(block)) {
            return true;
        } else {
            Block b = Block.getBlockFromName(parts[0] + ":" + parts[1]);
            //AgriCore.getLogger("agricraft").debug(b);
            return b != null;
        }
    }

    @Override
    public boolean isValidItem(String item) {
        String[] parts = item.split(":");
        if (parts.length < 2) {
            return false;
        } else if (OreDictUtil.isValidOre(item)) {
            return true;
        } else {
            Item i = Item.getByNameOrId(parts[0] + ":" + parts[1]);
            //AgriCore.getLogger("agricraft").debug(i);
            return i != null;
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
        return modid.equals("minecraft") || Loader.isModLoaded(modid);
    }

}
