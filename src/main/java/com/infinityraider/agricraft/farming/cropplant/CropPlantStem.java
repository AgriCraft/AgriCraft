package com.infinityraider.agricraft.farming.cropplant;

import com.infinityraider.agricraft.api.v1.RenderMethod;
import net.minecraft.block.Block;
import net.minecraft.item.ItemSeeds;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CropPlantStem extends CropPlantGeneric {
    private final Block block;

    public CropPlantStem(ItemSeeds seed, Block block) {
        super(seed, "minecraft:blocks/pumpkin_stem_disconnected");
        this.block = block;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderMethod getRenderMethod() {
        return RenderMethod.STEM;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ResourceLocation getPrimaryPlantTexture(int growthStage) {
        /*
        if(growthStage>= Constants.MATURE) {
            return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("minecraft:blocks/pumpkin_stem_connected");
        } else {
            return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("minecraft:blocks/pumpkin_stem_disconnected");
        }
        */
        //TODO: get stem texture
        return null;
    }

    @Override
    public ResourceLocation getSecondaryPlantTexture(int growthStage) {
        /*
        if(block == Blocks.melon_block) {
            return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("minecraft:blocks/melon_side");

        }
        else if(block == Blocks.pumpkin) {
            return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("minecraft:blocks/pumpkin_side");

        }
        return Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
        */
        //TODO: get block texture
        return null;
    }

    @Override
    public String getInformation() {
        String name = getSeed().getUnlocalizedName();
        if(name.indexOf('_')>=0) {
            name = name.substring(name.indexOf('_')+1);
        }
        if(name.indexOf('.')>=0) {
            name = name.substring(name.indexOf('.')+1);
        }
        return "agricraft_journal."+name;
    }
}
