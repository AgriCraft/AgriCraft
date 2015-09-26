package com.InfinityRaider.AgriCraft.compatibility.forgemultipart;

import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.RenderHelper;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.lang.reflect.Method;
import java.util.List;

public class ForgeMultiPartHelper extends ModHelper {
    private static Class multiBlockClass;
    private static Method getTileMethod;

    private static Class tileMultiPartClass;
    private static Method getMultiPartsMethod;

    private static Class leverPartClass;
    private static Method getMetaDataMethod;

    @Override
    protected String modId() {
        return Names.Mods.mcMultipart;
    }

    @Override
    protected void postTasks() {
        try {
            multiBlockClass = Class.forName("codechicken.multipart.BlockMultipart");
            getTileMethod = multiBlockClass.getMethod("getTile", IBlockAccess.class, int.class, int.class, int.class);
        } catch (Exception e) {
            LogHelper.printStackTrace(e);
        }
        try {
            tileMultiPartClass = Class.forName("codechicken.multipart.TileMultipart");
            getMultiPartsMethod = tileMultiPartClass.getMethod("jPartList");
        } catch (Exception e) {
            LogHelper.printStackTrace(e);
        }
        try {
            leverPartClass = Class.forName("codechicken.multipart.minecraft.LeverPart");
            getMetaDataMethod = leverPartClass.getMethod("getMetadata");
        }catch (Exception e) {
            LogHelper.printStackTrace(e);
        }
    }

    public static boolean isMultiPart(Block block) {
        return multiBlockClass!=null && multiBlockClass.isInstance(block);
    }

    public static boolean isLeverFacingThis(World world, int x, int y, int z, ForgeDirection dir) {
        try {
            Object tileMultiPart = getTileMethod.invoke(null, world, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
            List multiPartList = (List) getMultiPartsMethod.invoke(tileMultiPart);
            for(Object obj:multiPartList) {
                if(!isLeverPart(obj)) {
                    continue;
                }
                try {
                    int metadata = (Integer) getMetaDataMethod.invoke(obj);
                    if (RenderHelper.isLeverFacingBlock(metadata, dir)) {
                        return true;
                    }
                } catch(Exception e) {
                    continue;
                }
            }
            return false;
        } catch(Exception e) {
            return false;
        }
    }

    public static boolean isLeverPart(Object obj) {
        return leverPartClass!=null && leverPartClass.isInstance(obj);
    }
}
