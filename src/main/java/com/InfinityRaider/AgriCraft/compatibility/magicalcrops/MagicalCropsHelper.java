package com.InfinityRaider.AgriCraft.compatibility.magicalcrops;

import com.InfinityRaider.AgriCraft.api.v1.IFertiliser;
import com.InfinityRaider.AgriCraft.blocks.BlockCrop;
import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import cpw.mods.fml.common.Mod;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public final class MagicalCropsHelper extends ModHelper {
    private boolean newVersion;
    private IFertiliser fertiliser;

    @Override
    protected void init() {
        Class magicalCropsClass=null;
        try {
           magicalCropsClass = Class.forName("com.mark719.magicalcrops.MagicalCrops");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        assert magicalCropsClass != null;
        Annotation[] annotations = magicalCropsClass.getAnnotations();
        for(Annotation annotation:annotations) {
            if(annotation instanceof Mod) {
                String version = ((Mod) annotation).version();
                newVersion = !version.contains("ALPHA");
            }
        }
    }

    @Override
    protected void initPlants() {
        if(newVersion) {
            initV4Plants();
        } else {
            initBetaPlants();
        }
    }

    private void initBetaPlants() {
        Class mc_ItemRegistry = null;
        Class blockMagicalCrops = null;
        try {
            mc_ItemRegistry = Class.forName("com.mark719.magicalcrops.MagicalCrops");
            blockMagicalCrops = Class.forName("com.mark719.magicalcrops.crops.BlockMagicalCrops");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        assert mc_ItemRegistry != null;
        Field[] fields = mc_ItemRegistry.getDeclaredFields();
        for(Field field : fields) {
            if(Modifier.isStatic(field.getModifiers())) {
                try {
                    Object obj = field.get(null);
                    if(obj instanceof ItemSeeds) {
                        ItemSeeds seed = (ItemSeeds) obj;
                        Block plant = seed.getPlant(null, 0, 0, 0);
                        assert blockMagicalCrops != null;
                        boolean highTier = blockMagicalCrops.isInstance(plant);
                        CropPlantHandler.registerPlant(new CropPlantMagicalCropsBeta(seed, highTier));
                    }
                } catch (Exception e) {
                    if (ConfigurationHandler.debug) {
                        e.printStackTrace();
                    }
                }            }
        }
    }

    private void initV4Plants() {
        ArrayList<Class> classes = new ArrayList<Class>();
        Method getDropMethod = null;
        try {
            classes.add(Class.forName("com.mark719.magicalcrops.handlers.MSeeds"));
            classes.add(Class.forName("com.mark719.magicalcrops.handlers.ModCompat"));
            Method[] methods = Class.forName("com.mark719.magicalcrops.blocks.BlockMagicalCrops").getDeclaredMethods();
            for(Method method:methods) {
                if(method.getName().equalsIgnoreCase("func_149865_P")) {
                    getDropMethod = method;
                    getDropMethod.setAccessible(true);
                    break;
                }
            }
        } catch (ClassNotFoundException e) {
            if (ConfigurationHandler.debug) {
                e.printStackTrace();
            }
        }
        for(Class clazz:classes) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers())) {
                    try {
                        Object obj = field.get(null);
                        if (obj instanceof ItemSeeds) {
                            ItemSeeds seed = (ItemSeeds) obj;
                            Block plant = seed.getPlant(null, 0, 0, 0);
                            assert getDropMethod != null;
                            CropPlantHandler.registerPlant(new CropPlantMagicalCropsV4(seed, (Item) getDropMethod.invoke(plant)));
                        }
                    } catch (Exception e) {
                        if (ConfigurationHandler.debug) {
                            if (ConfigurationHandler.debug) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected boolean useTool(World world, int x, int y, int z, EntityPlayer player, ItemStack stack, BlockCrop block, TileEntityCrop crop) {
        if(fertiliser!=null) {
            crop.applyFertiliser(fertiliser, world.rand);
            if(!player.capabilities.isCreativeMode) {
                player.getCurrentEquippedItem().stackSize = player.getCurrentEquippedItem().stackSize-1;
            }
            return true;
        }
        return false;
    }

    @Override
    protected List<Item> getTools() {
        if(newVersion) {
            return null;
        }
        fertiliser = new MagicalCropsFertiliser();
        ArrayList<Item> list = new ArrayList<Item>();
        list.add((Item) Item.itemRegistry.getObject("magicalcrops:magicalcrops_MagicalCropFertilizer"));
        return list;
    }

    @Override
    protected String modId() {
        return Names.Mods.magicalCrops;
    }
}
