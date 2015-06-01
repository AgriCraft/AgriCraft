package com.InfinityRaider.AgriCraft.compatibility.pneumaticcraft;

import com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant.CropPlant;
import com.InfinityRaider.AgriCraft.blocks.BlockCrop;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import com.InfinityRaider.AgriCraft.utility.RenderHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Random;

public class CropPlantPneumaticCraft extends CropPlant {
    private int meta;
    private Block plant;
    private static final Item seed = (Item) Item.itemRegistry.getObject("PneumaticCraft:plasticPlant");

    public CropPlantPneumaticCraft(int meta, Block plant) {
        this.meta = meta;
        this.plant = plant;
    }

    @Override
    public int tier() {
        return 3;
    }

    @Override
    public ItemStack getSeed() {
        return new ItemStack(seed, 1, meta);
    }

    @Override
    public boolean onHarvest(World world, int x, int y, int z, EntityPlayer player) {
        int gain = ((TileEntityCrop) world.getTileEntity(x, y, z)).getGain();
        try {
            Class clazz = Class.forName("pneumaticCraft.common.block.pneumaticPlants.BlockPneumaticPlantBase");
            Method method = clazz.getMethod("executeFullGrownEffect", World.class, int.class, int.class, int.class, Random.class);
            world.setBlockMetadataWithNotify(x, y, z, 14, 4);
            method.invoke(plant, world, x, y, z, world.rand);
            if(gain==10) {
                method.invoke(plant, world, x, y, z, world.rand);
            }
            world.setBlockMetadataWithNotify(x, y, z, 2, 2);
            return false;
        } catch(Exception e) {
            if(ConfigurationHandler.debug) {
                e.printStackTrace();
            }
            return true;
        }
    }

    @Override
    public ArrayList<ItemStack> getAllFruits() {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        list.add(new ItemStack(seed, 1, meta));
        return list;
    }

    @Override
    public ItemStack getRandomFruit(Random rand) {
        return new ItemStack(seed, 1, meta);
    }

    @Override
    public ArrayList<ItemStack> getFruitsOnHarvest(int gain, Random rand) {
        int amount = (int) (Math.ceil((gain + 0.00) / 3));
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        while(amount>0) {
            list.add(new ItemStack(seed, 1, meta));
            amount--;
        }
        return list;
    }

    @Override
    public boolean canBonemeal() {
        return true;
    }

    @Override
    public boolean onAllowedGrowthTick(World world, int x, int y, int z, int oldGrowthStage) {
        if(oldGrowthStage==7) {
            TileEntityCrop crop = (TileEntityCrop) world.getTileEntity(x, y, z);
            int strength = crop.getStrength();
            if(world.rand.nextInt(10)>strength) {
                ((BlockCrop) world.getBlock(x, y, z)).harvest(world, x, y, z, null);
            }
        }
        return true;
    }

    @Override
    public boolean isFertile(World world, int x, int y, int z) {
        return true;
    }

    @Override
    public float getHeight(int meta) {
        return Constants.unit*13;
    }

    @Override
    public IIcon getPlantIcon(int growthStage) {
        return plant.getIcon(0, Math.max(0, growthStage - 1));
    }

    @Override
    public boolean renderAsFlower() {
        return true;
    }

    @Override
    public String getInformation() {
        return "agricraft.journal_PC.plant"+meta;
    }

    /** This is called when the plant is rendered */
    @SideOnly(Side.CLIENT)
    public void renderPlantInCrop(IBlockAccess world, int x, int y, int z, RenderBlocks renderer) {
        if(meta==11) {
            IIcon icon = getPlantIcon(world.getBlockMetadata(x, y, z));
            Tessellator tessellator = Tessellator.instance;
            tessellator.addTranslation(x, y, z);
            tessellator.setBrightness(Blocks.wheat.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z));
            tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
            int minY = 0;
            int maxY = 12;
            //plane 1 front right
            RenderHelper.addScaledVertexWithUV(tessellator, 6, minY, 4.001F, 0, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 6, maxY, 4.001F, 0, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 18, maxY, 4.001F, 16, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 18, minY, 4.001F, 16, 0, icon);
            //plane 1 front left
            RenderHelper.addScaledVertexWithUV(tessellator, -2, minY, 3.999F, 0, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, -2, maxY, 3.999F, 0, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 10, maxY, 3.999F, 16, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 10, minY, 3.999F, 16, 0, icon);
            //plane 1 back right
            RenderHelper.addScaledVertexWithUV(tessellator, 6, minY, 4.001F, 0, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 18, minY, 4.001F, 16, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 18, maxY, 4.001F, 16, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 6, maxY, 4.001F, 0, 16, icon);
            //plane 1 back left
            RenderHelper.addScaledVertexWithUV(tessellator, -2, minY, 3.999F, 0, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 10, minY, 3.999F, 16, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 10, maxY, 3.999F, 16, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, -2, maxY, 3.999F, 0, 16, icon);
            //plane 2 front right
            RenderHelper.addScaledVertexWithUV(tessellator, 3.999F, minY, 6, 16, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 3.999F, minY, 18, 0, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 3.999F, maxY, 18, 0, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 3.999F, maxY, 6, 16, 16, icon);
            //plane 2 front left
            RenderHelper.addScaledVertexWithUV(tessellator, 4.001F, minY, -2, 16, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 4.001F, minY, 10, 0, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 4.001F, maxY, 10, 0, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 4.001F, maxY, -2, 16, 16, icon);
            //plane 2 back right
            RenderHelper.addScaledVertexWithUV(tessellator, 3.999F, minY, 6, 16, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 3.999F, maxY, 6, 16, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 3.999F, maxY, 18, 0, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 3.999F, minY, 18, 0, 0, icon);
            //plane 2 back right
            RenderHelper.addScaledVertexWithUV(tessellator, 4.001F, minY, -2, 16, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 4.001F, maxY, -2, 16, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 4.001F, maxY, 10, 0, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 4.001F, minY, 10, 0, 0, icon);
            //plane 3 front right
            RenderHelper.addScaledVertexWithUV(tessellator, 6, minY, 11.999F, 16, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 18, minY, 11.999F, 0, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 18, maxY, 11.999F, 0, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 6, maxY, 11.999F, 16, 16, icon);
            //plane 3 front left
            RenderHelper.addScaledVertexWithUV(tessellator, -2, minY, 12.001F, 16, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 10, minY, 12.001F, 0, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 10, maxY, 12.001F, 0, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, -2, maxY, 12.001F, 16, 16, icon);
            //plane 3 back right
            RenderHelper.addScaledVertexWithUV(tessellator, 6, minY, 11.999F, 16, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 6, maxY, 11.999F, 16, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 18, maxY, 11.999F, 0, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 18, minY, 11.999F, 0, 0, icon);
            //plane 3 back left
            RenderHelper.addScaledVertexWithUV(tessellator, -2, minY, 12.001F, 16, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, -2, maxY, 12.001F, 16, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 10, maxY, 12.001F, 0, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 10, minY, 12.001F, 0, 0, icon);
            //plane 4 front right
            RenderHelper.addScaledVertexWithUV(tessellator, 11.999F, minY, 18, 16, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 11.999F, minY, 6, 0, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 11.999F, maxY, 6, 0, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 11.999F, maxY, 18, 16, 16, icon);
            //plane 4 front left
            RenderHelper.addScaledVertexWithUV(tessellator, 12.001F, minY, 10, 16, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 12.001F, minY, -2, 0, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 12.001F, maxY, -2, 0, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 12.001F, maxY, 10, 16, 16, icon);
            //plane 4 back right
            RenderHelper.addScaledVertexWithUV(tessellator, 11.999F, minY, 18, 16, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 11.999F, maxY, 18, 16, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 11.999F, maxY, 6, 0, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 11.999F, minY, 6, 0, 0, icon);
            //plane 4 back left
            RenderHelper.addScaledVertexWithUV(tessellator, 12.001F, minY, 10, 16, 0, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 12.001F, maxY, 10, 16, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 12.001F, maxY, -2, 0, 16, icon);
            RenderHelper.addScaledVertexWithUV(tessellator, 12.001F, minY,-2, 0, 0, icon);

            tessellator.addTranslation(-x, -y, -z);

        } else {
            super.renderPlantInCrop(world, x, y, z, renderer);
        }
    }
}
