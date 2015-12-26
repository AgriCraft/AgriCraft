package com.InfinityRaider.AgriCraft.reference;

import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlant;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class BlockStates {
    public static final PropertyInteger GROWTHSTAGE = PropertyInteger.create("age", 0, 7);

    public static final IProperty<CropPlant> PLANT = new IProperty<CropPlant>() {
        @Override
        public String getName() {
            return "PLANT";
        }

        @Override
        public Collection<CropPlant> getAllowedValues() {
            List<CropPlant> list = CropPlantHandler.getPlantsUpToTier(5);
            list.add(CropPlantHandler.NONE);
            return list;
        }

        @Override
        public Class<CropPlant> getValueClass() {
            return CropPlant.class;
        }

        @Override
        public String getName(CropPlant value) {
            ItemStack seed = value.getSeed();
            if(seed == null) {
                return "NONE";
            }
            return Item.itemRegistry.getNameForObject(seed.getItem()).toString()+":"+seed.getItemDamage();
        }
    };

    public static final IProperty<Boolean> WEEDS = new IProperty<Boolean>() {
        @Override
        public String getName() {
            return "WEEDS";
        }

        @Override
        public Collection<Boolean> getAllowedValues() {
            return Arrays.asList(true, false);
        }

        @Override
        public Class<Boolean> getValueClass() {
            return Boolean.class;
        }

        @Override
        public String getName(Boolean value) {
            return value.toString();
        }
    };

    public static final IProperty<Boolean> CROSSCROP = new IProperty<Boolean>() {
        @Override
        public String getName() {
            return "CROSSCROP";
        }

        @Override
        public Collection<Boolean> getAllowedValues() {
            return Arrays.asList(true, false);
        }

        @Override
        public Class<Boolean> getValueClass() {
            return Boolean.class;
        }

        @Override
        public String getName(Boolean value) {
            return value.toString();
        }
    };
}
