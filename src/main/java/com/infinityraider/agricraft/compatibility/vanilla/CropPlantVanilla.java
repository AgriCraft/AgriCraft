/*
 * Vanilla Crop Class.
 */
package com.infinityraider.agricraft.compatibility.vanilla;

import com.infinityraider.agricraft.api.v1.RenderMethod;
import com.infinityraider.agricraft.farming.cropplant.CropPlantGeneric;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 *
 * @author RlonRyan
 */
public class CropPlantVanilla extends CropPlantGeneric {
	
	private final String[] textures = new String[8];

	public CropPlantVanilla(ItemSeeds seeds, String textureBase) {
		super(seeds);
		for (int i = 0; i < textures.length; i++) {
            textures[i] = "minecraft:blocks/" + textureBase + "_stage_" + i;
        }
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite getPrimaryPlantTexture(int growthStage) {
		growthStage = Math.max(Math.min(growthStage, textures.length-1), 0);
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(textures[growthStage]);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite getSecondaryPlantTexture(int growthStage) {
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public RenderMethod getRenderMethod() {
		return RenderMethod.HASHTAG;
	}

	@Override
    @SideOnly(Side.CLIENT)
    public String getInformation() {
        String name = this.getSeed().getUnlocalizedName();
        int index = name.indexOf('_');
        if(index<0) {
            index = name.indexOf('.');
        }
        name = name.substring(index+1);
        return "agricraft_journal."+name;
    }
	
	@Override
    public ArrayList<ItemStack> getAllFruits() {
        ArrayList<ItemStack> list = new ArrayList<>();
        list.add(new ItemStack(this.getBlock().getItemDropped(this.getBlock().getStateFromMeta(7), null, 0)));
        return list;
    }

    @Override
    public ItemStack getRandomFruit(Random rand) {
        return new ItemStack(this.getBlock().getItemDropped(this.getBlock().getStateFromMeta(7), rand, 0));
    }

    @Override
    public ArrayList<ItemStack> getFruitsOnHarvest(int gain, Random rand) {
        int amount = (int) (Math.ceil((gain + 0.00) / 3));
        ArrayList<ItemStack> list = new ArrayList<>();
        while(amount>0) {
            list.add(getRandomFruit(rand));
            amount--;
        }
        return list;
    }
	
}
