package com.infinityraider.agricraft.farming.cropplant;

import com.infinityraider.agricraft.api.v1.IAgriCraftPlant;
import com.infinityraider.agricraft.api.v1.IGrowthRequirement;
import com.infinityraider.agricraft.api.v1.IMutation;
import com.infinityraider.agricraft.api.v1.RenderMethod;
import com.infinityraider.agricraft.items.ItemClipping;
import com.infinityraider.agricraft.reference.Constants;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Implementation of the CropPlant class for an IAgriCraftPlant object
 */
public class CropPlantAgriCraft extends CropPlant {
	
    IAgriCraftPlant plant;
	ItemClipping clipping;

    public CropPlantAgriCraft(IAgriCraftPlant plant) {
        super();
        this.plant = plant;
        this.setTier(plant.getSeed().tier());
        this.setGrowthRequirement(plant.getGrowthRequirement());
        this.setSpreadChance(100/getTier());
		this.clipping = new ItemClipping(this, plant.getBlock().getRegistryName().replaceFirst(".*:", ""), plant.getBlock().getRegistryName().replaceFirst(":", ":blocks/") + 4);
    }

    @Override
    public int tier() {
        return 1;
    }

    @Override
    public ItemStack getSeed() {
        return plant.getSeedStack(1);
    }

	@Override
	public ItemStack getClipping() {
		return new ItemStack(this.clipping);
	}

    @Override
    public Block getBlock() {
        return plant.getBlock();
    }

    @Override
    public ArrayList<ItemStack> getAllFruits() {
        return plant.getAllFruits();
    }

    @Override
    public ItemStack getRandomFruit(Random rand) {
        return plant.getRandomFruit(rand);
    }

    @Override
    public ArrayList<ItemStack> getFruitsOnHarvest(int gain, Random rand) {
        int amount =  (int) (Math.ceil((gain + 0.00) / 3));
        return plant.getFruit(amount, rand);
    }

    @Override
    public boolean canBonemeal() {
        return getTier()<4;
    }

    @Override
    public List<IMutation> getDefaultMutations() {
        return plant.getSeed().getMutations();
    }

    @Override
    protected IGrowthRequirement initGrowthRequirement() {
        return null;
    }

    @Override
    public void onAllowedGrowthTick(World world, BlockPos pos, int oldGrowthStage) {}

    @Override
    @SideOnly(Side.CLIENT)
    public float getHeight(int meta) {
        return Constants.UNIT*13;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderMethod getRenderMethod() {
        return plant.renderAsFlower() ? RenderMethod.CROSSED : RenderMethod.HASHTAG;
    }

    @Override
    public TextureAtlasSprite getPrimaryPlantTexture(int growthStage) {
        return plant.getPlantIcon(growthStage);
    }

    @Override
    public TextureAtlasSprite getSecondaryPlantTexture(int growthStage) {
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getInformation() {
        return plant.getSeed().getInformation();
    }
}
