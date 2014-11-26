package com.InfinityRaider.AgriCraft.utility;

import com.InfinityRaider.AgriCraft.compatibility.LoadedMods;
import com.InfinityRaider.AgriCraft.compatibility.natura.NaturaHelper;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
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
            return ((ItemBlock) item).field_150939_a.getIcon(1, meta);
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
        String domain = path.substring(0,path.indexOf(":")+1);
        String file = path.substring(path.indexOf(':')+1);
        return new ResourceLocation(domain+"textures/items/"+file+".png");
    }

    //gets the render type for a plant
    //1: diagonals of the block
    //6: four lines parallel to the block edges
    public static int getRenderType(ItemSeeds seed, int meta) {
        BlockBush plant = SeedHelper.getPlant(seed);
        int renderType = plant.getRenderType();
        String name = Item.itemRegistry.getNameForObject(seed);
        if(LoadedMods.natura && name.indexOf(':')>=0 && name.substring(0,name.indexOf(':')).equalsIgnoreCase("Natura")) {
            renderType = meta==0?6:1;
        }
        return renderType;
    }

    //this method is used to convert the vanilla 0-7 meta growth stages to natura growth stages or nether wart growth stages
    public static int plantIconIndex(ItemSeeds seed, int seedMeta, int growthMeta) {
        if(SeedHelper.getPlantDomain(seed).equalsIgnoreCase("natura")) {
            return NaturaHelper.getTextureIndex(growthMeta, seedMeta);
        }
        else if(seed== Items.nether_wart) {
            return (int) Math.ceil(( (float) growthMeta-2)/2);
        }
        else {
            return growthMeta;
        }
    }

    //adds a vertex to the tessellator scaled with 1/46th of a block
    public static void addScaledVertexWithUV(Tessellator tessellator, float x, float y, float z, float u, float v) {
        float unit = Constants.unit;
        tessellator.addVertexWithUV(x*unit, y*unit, z*unit, u*unit, v*unit);
    }
}
