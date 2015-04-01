package com.InfinityRaider.AgriCraft.compatibility.thaumictinkerer;

import com.InfinityRaider.AgriCraft.farming.ICropOverridingSeed;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import thaumic.tinkerer.common.item.ItemInfusedSeeds;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;

public class ThaumicTinkererHelper {
    public static HashMap<TileEntityCrop, ThaumicTinkererOverride> overrides= new HashMap<TileEntityCrop, ThaumicTinkererOverride>();

    private static final ClassLoader loader = ItemInfusedSeeds.class.getClassLoader();
    private static final Class[] interfaces = {ICropOverridingSeed.class};
    private static final InvocationHandler handler = new TT_InvocationHandler();

    public static void init() {
        Object instance = Proxy.newProxyInstance(loader, interfaces, handler);
    }

    private static class TT_InvocationHandler implements InvocationHandler {
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String name = method.getName();
            LogHelper.debug("Invoking "+name);
            if(name.equals("getOverride")) {
                if(args.length==1 && args[0] instanceof TileEntityCrop) {
                    TileEntityCrop crop = (TileEntityCrop) args[0];
                    ThaumicTinkererOverride override = overrides.get(crop);
                    if(override==null) {
                        override = new ThaumicTinkererOverride(crop);
                    }
                    return override;
                }
                else {
                    LogHelper.debug("Invalid arguments for method 'getOverride()'");
                }
            }
            else if(name.equals("getGrowthRequirement")) {
                return null;
            }
            else if(name.equals("getRenderType()")) {
                return 6;
            }
            return null;
        }
    }
}
