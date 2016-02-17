package com.infinityraider.agricraft.farming.cropplant;

import com.infinityraider.agricraft.api.v1.BlockWithMeta;
import com.infinityraider.agricraft.api.v1.IGrowthRequirement;
import com.infinityraider.agricraft.api.v1.IMutation;
import com.infinityraider.agricraft.api.v1.RenderMethod;
import com.infinityraider.agricraft.farming.growthrequirement.GrowthRequirementHandler;
import com.infinityraider.agricraft.reference.Constants;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CropPlantNetherWart extends CropPlant {
    private String[] textureNames;

    public CropPlantNetherWart() {
        super();
        this.textureNames = new String[3];
        for(int i = 0; i< textureNames.length; i++) {
            textureNames[i] = "minecraft:blocks/nether_wart_stage_"+i;
        }
    }

    @Override
    public int tier() {
        return 2;
    }

    @Override
    public ItemStack getSeed() {
        return new ItemStack(Items.nether_wart);
    }

    @Override
    public Block getBlock() {
        return Blocks.nether_wart;
    }

    @Override
    public ArrayList<ItemStack> getAllFruits() {
        ArrayList<ItemStack> list = new ArrayList<>();
        list.add(new ItemStack(Items.nether_wart));
        return list;
    }

    @Override
    public ItemStack getRandomFruit(Random rand) {
        return new ItemStack(Items.nether_wart);
    }

    @Override
    public ArrayList<ItemStack> getFruitsOnHarvest(int gain, Random rand) {
        int amount = (int) (Math.ceil((gain + 0.00) / 3));
        ArrayList<ItemStack> list = new ArrayList<>();
        while (amount > 0) {
            list.add(getRandomFruit(rand));
            amount--;
        }
        return list;
    }

    @Override
    public boolean canBonemeal() {
        return true;
    }

    @Override
    public List<IMutation> getDefaultMutations() {
        return new ArrayList<>();
    }

    @Override
    protected IGrowthRequirement initGrowthRequirement() {
        return GrowthRequirementHandler.getNewBuilder().soil(new BlockWithMeta(Blocks.soul_sand)).brightnessRange(0, 8).build();
    }

    @Override
    public void onAllowedGrowthTick(World world, BlockPos pos, int oldGrowthStage) {}

    @Override
    @SideOnly(Side.CLIENT)
    public float getHeight(int meta) {
        return Constants.UNIT * 13;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderMethod getRenderMethod() {
        return RenderMethod.HASHTAG;
    }

    @Override
    public TextureAtlasSprite getPrimaryPlantTexture(int growthStage) {
        int index = 0;
        if(growthStage >= Constants.MATURE) {
            index = 2;
        }
        else if(growthStage > 3) {
            index = 1;
        }
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(textureNames[index]);
    }

    @Override
    public TextureAtlasSprite getSecondaryPlantTexture(int growthStage) {
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getInformation() {
        return "agricraft_journal." + "nether_wart";
    }
}
