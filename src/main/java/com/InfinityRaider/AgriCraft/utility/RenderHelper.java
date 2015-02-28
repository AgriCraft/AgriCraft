package com.InfinityRaider.AgriCraft.utility;

import chococraft.common.items.seeds.ItemGysahlSeeds;
import com.InfinityRaider.AgriCraft.compatibility.ModIntegration;
import com.InfinityRaider.AgriCraft.compatibility.chococraft.ChococraftHelper;
import com.InfinityRaider.AgriCraft.compatibility.natura.NaturaHelper;
import com.InfinityRaider.AgriCraft.compatibility.plantmegapack.PlantMegaPackHelper;
import com.InfinityRaider.AgriCraft.reference.Constants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSeeds;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

public abstract class RenderHelper {
    public static ResourceLocation getResource(Block block, int meta) {
        return getBlockResource(getIcon(block, meta));
    }

    public static ResourceLocation getResource(Item item, int meta) {
        return getItemResource(getIcon(item, meta));
    }

    public static IIcon getIcon(Item item, int meta) {
        if(item instanceof ItemBlock) {
            return ((ItemBlock) item).field_150939_a.getIcon(3, meta);
        }
        return item.getIconFromDamage(meta);
    }

    public static IIcon getIcon(Block block, int meta) {
        return  block.getIcon(0, meta);
    }

    public static ResourceLocation getBlockResource(IIcon icon) {
        String path = icon.getIconName();
        String domain = path.substring(0, path.indexOf(":") + 1);
        String file = path.substring(path.indexOf(':') + 1);
        return new ResourceLocation(domain + "textures/blocks/" + file + ".png");
    }

    public static ResourceLocation getItemResource(IIcon icon) {
        String path = icon.getIconName();
        String domain = path.substring(0, path.indexOf(":") + 1);
        String file = path.substring(path.indexOf(':')+1);
        return new ResourceLocation(domain+"textures/items/"+file+".png");
    }

    //gets the render type for a plant
    //1: diagonals of the block
    //6: four lines parallel to the block edges
    public static int getRenderType(ItemSeeds seed, int meta) {
        Block plant = SeedHelper.getPlant(seed);
        int renderType = plant.getRenderType();
        if(ModIntegration.LoadedMods.natura) {
            String name = Item.itemRegistry.getNameForObject(seed);
            if (name.indexOf(':') >= 0 && name.substring(0, name.indexOf(':')).equalsIgnoreCase("Natura")) {
                renderType = meta == 0 ? 6 : 1;
            }
        }
        return renderType;
    }

    //this method is used to convert the vanilla 0-7 meta growth stages to natura growth stages or nether wart growth stages
    public static int plantIconIndex(ItemSeeds seed, int seedMeta, int growthMeta) {
        if(ModIntegration.LoadedMods.natura && SeedHelper.getPlantDomain(seed).equalsIgnoreCase("natura")) {
            return NaturaHelper.getTextureIndex(growthMeta, seedMeta);
        }
        else if(ModIntegration.LoadedMods.plantMegaPack && SeedHelper.getPlantDomain(seed).equalsIgnoreCase("plantmegapack")) {
            return PlantMegaPackHelper.getTextureIndex(seed, growthMeta);
        }
        else if(ModIntegration.LoadedMods.chococraft && seed instanceof ItemGysahlSeeds) {
            return ChococraftHelper.transformMeta(growthMeta);
        }
        else if(seed== Items.nether_wart) {
            return (int) Math.ceil(( (float) growthMeta-2)/2);
        }
        else {
            return growthMeta;
        }
    }

    //checks if a lever is facing a block
    public static boolean isLeverFacingBlock(int leverMeta, char axis, int direction) {
        if(axis=='x') {
            if(direction>0) {
                return leverMeta % 8 == 1;
            }
            else {
                return leverMeta %8 == 2;
            }
        }
        else if(axis=='z') {
            if(direction>0) {
                return leverMeta %8 == 3;
            }
            else {
                return leverMeta %8 == 4;
            }
        }
        return false;
    }

    //adds a vertex to the tessellator scaled with 1/16th of a block
    public static void addScaledVertexWithUV(Tessellator tessellator, float x, float y, float z, float u, float v) {
        float unit = Constants.unit;
        tessellator.addVertexWithUV(x*unit, y*unit, z*unit, u*unit, v*unit);
    }

    //same as above method, but does not require the correct texture to be bound
    public static void addScaledVertexWithUV(Tessellator tessellator, float x, float y, float z, float u, float v, IIcon icon) {
        float unit = Constants.unit;
        tessellator.addVertexWithUV(x*unit, y*unit, z*unit, icon.getInterpolatedU(u), icon.getInterpolatedV(v));
    }

    //draws a rectangular prism
    public static void drawScaledPrism(Tessellator tessellator, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, IIcon icon) {
        //front plane
        addScaledVertexWithUV(tessellator, maxX, maxY, minZ, maxX, 16 - maxY, icon);
        addScaledVertexWithUV(tessellator, maxX, minY, minZ, maxX, 16-minY, icon);
        addScaledVertexWithUV(tessellator, minX, minY, minZ, minX, 16-minY, icon);
        addScaledVertexWithUV(tessellator, minX, maxY, minZ, minX, 16-maxY, icon);
        //back plane
        addScaledVertexWithUV(tessellator, maxX, maxY, maxZ, maxX, 16-maxY, icon);
        addScaledVertexWithUV(tessellator, minX, maxY, maxZ, minX, 16-maxY, icon);
        addScaledVertexWithUV(tessellator, minX, minY, maxZ, minX, 16-minY, icon);
        addScaledVertexWithUV(tessellator, maxX, minY, maxZ, maxX, 16-minY, icon);
        //right plane
        addScaledVertexWithUV(tessellator, maxX, maxY, maxZ, maxZ, 16-maxY, icon);
        addScaledVertexWithUV(tessellator, maxX, minY, maxZ, maxZ, 16-minY, icon);
        addScaledVertexWithUV(tessellator, maxX, minY, minZ, minZ, 16-minY, icon);
        addScaledVertexWithUV(tessellator, maxX, maxY, minZ, minZ, 16-maxY, icon);
        //left plane
        addScaledVertexWithUV(tessellator, minX, maxY, maxZ, maxZ, 16-maxY, icon);
        addScaledVertexWithUV(tessellator, minX, maxY, minZ, minZ, 16-maxY, icon);
        addScaledVertexWithUV(tessellator, minX, minY, minZ, minZ, 16-minY, icon);
        addScaledVertexWithUV(tessellator, minX, minY, maxZ, maxZ, 16-minY, icon);
        //top plane
        addScaledVertexWithUV(tessellator, maxX, maxY, maxZ, maxX, maxZ, icon);
        addScaledVertexWithUV(tessellator, maxX, maxY, minZ, maxX, minZ, icon);
        addScaledVertexWithUV(tessellator, minX, maxY, minZ, minX, minZ, icon);
        addScaledVertexWithUV(tessellator, minX, maxY, maxZ, minX, maxZ, icon);
        //bottom plane
        addScaledVertexWithUV(tessellator, maxX, minY, maxZ, maxX, maxZ, icon);
        addScaledVertexWithUV(tessellator, minX, minY, maxZ, minX, maxZ, icon);
        addScaledVertexWithUV(tessellator, minX, minY, minZ, minX, minZ, icon);
        addScaledVertexWithUV(tessellator, maxX, minY, minZ, maxX, minZ, icon);
    }

    //draws a rectangular prism
    public static void drawPrism(Tessellator tessellator, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, IIcon icon) {
        drawScaledPrism(tessellator, minX*16, minY*16, minZ*16, maxX*16, maxY*16, maxZ*16, icon);
    }
}
