package k4unl.minecraft.Hydraulicraft.api;

import codechicken.multipart.TMultiPart;
import net.minecraft.tileentity.TileEntity;

import java.lang.reflect.Constructor;

/**
 * 
 * @author minemaarten
 * Original idea and class from PneumaticCraft.
 * Modified by K-4U for Hydraulicraft.
 * Deprecated in favour of HCApi
 */
@Deprecated
public class HydraulicBaseClassSupplier {
	private static Constructor baseHandlerConstructor;
	private static Constructor multipartConstructor;

    public static IBaseClass getBaseClass(TileEntity target, int maxStorage){
    	IBaseClass baseClassEntity = null;
        try {
            if(baseHandlerConstructor == null) baseHandlerConstructor = Class.forName("k4unl.minecraft.Hydraulicraft.tileEntities.TileHydraulicBase").getConstructor(int.class);
            baseClassEntity = (IBaseClass)baseHandlerConstructor.newInstance(maxStorage);
            baseClassEntity.init(target);
        } catch(Exception e) {
            System.err.println("[Hydraulicraft API] An error has occured whilst trying to get a base class. Here's a stacktrace:");
            e.printStackTrace();
        }
    	
        return baseClassEntity;
    }

    public static IBaseClass getBaseClass(TMultiPart target, int maxStorage){
    	IBaseClass baseClassEntity = null;
        try {
            if(multipartConstructor == null) multipartConstructor = Class.forName("k4unl.minecraft.Hydraulicraft.tileEntities.TileHydraulicBase").getConstructor(int.class);
            baseClassEntity = (IBaseClass)multipartConstructor.newInstance(maxStorage);
            baseClassEntity.init(target);
        } catch(Exception e){
        	System.err.println("[Hydraulicraft API] An error has occured whilst trying to get a base class. Here's a stacktrace:");
            e.printStackTrace();
        }
        return baseClassEntity;
    }
    

}
