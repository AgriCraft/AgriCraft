package com.InfinityRaider.AgriCraft.handler;

import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@SideOnly(Side.CLIENT)
public class MissingJsonHandler {
    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onModelBake(ModelBakeEvent event) {
        ModelLoader modelLoader = event.modelLoader;
        Set<ModelResourceLocation> missingModels = getMissingModels(modelLoader);
        purgeMissingAgriCraftModels(missingModels);
    }

    @SuppressWarnings("unchecked")
    private Set<ModelResourceLocation> getMissingModels(ModelLoader loader) {
        Set<ModelResourceLocation> set = null;
        for(Field field : loader.getClass().getDeclaredFields()) {
            if(field.getType() != Set.class) {
                continue;
            }
            ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
            Type[] types = parameterizedType.getActualTypeArguments();
            if(types.length != 1) {
                continue;
            }
            if(types[0].getTypeName().equals("net.minecraft.client.resources.model.ModelResourceLocation")) {
                field.setAccessible(true);
                try {
                    set = ( Set<ModelResourceLocation>) field.get(loader);
                } catch(Exception e) {
                    LogHelper.printStackTrace(e);
                    set = new HashSet<>();
                }
                field.setAccessible(false);
                break;
            }
        }
        return set == null ? new HashSet<>() : set;
    }

    private void purgeMissingAgriCraftModels(Set<ModelResourceLocation> set) {
        Iterator<ModelResourceLocation> it = set.iterator();
        while(it.hasNext()) {
            ModelResourceLocation model = it.next();
            if(model.getResourceDomain().equalsIgnoreCase(Reference.MOD_ID)) {
                it.remove();
            }
        }
    }
}
