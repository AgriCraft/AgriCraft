package com.InfinityRaider.AgriCraft.api.example;

import cpw.mods.fml.common.Loader;
import net.minecraft.world.World;

/**
 * This is an example implementation of how to use the AgriCraft API, you don't have to do it this way,
 * but I highly recommend it doing this way if you don't know how to use or reference the API without shipping it with your mod.
 * If done correctly this will never crash if AgriCraft is not loaded
 */
public class ExampleAgriCraftAPIwrapper {
    private static ExampleAgriCraftAPIwrapper instance;

    /**
     * This method returns the wrapper instance, and initializes it if hasn't been initialized yet
     * If AgriCraft is not present, it sets it to a placeholder (which is this class) which returns default values and never references the AgriCraft API.
     * If AgriCraft is loaded, it sets it to an actual class forwarding the calls to the AgriCraft API (see the  {@link ExampleAgriCraftAPIimplementation} class)
     *
     * @return the AgriCraftAPI wrapper
     */
    public static ExampleAgriCraftAPIwrapper getInstance() {
        if(instance == null) {
            if(Loader.isModLoaded("AgriCraft")) {
                instance = new ExampleAgriCraftAPIimplementation();
            } else {
                instance = new ExampleAgriCraftAPIwrapper();
            }
        }
        return instance;
    }

    protected ExampleAgriCraftAPIwrapper() {}


    /**
     * Define methods here which return values that you need, override them in the class which does reference AgriCraft API methods.
     * Here they should just return a default value whichever fits your application.
     *
     * If you need to work with implementations of interfaces, you can use the ever so handy {@Link Optional.Interface} annotation
     */

    /**
     * Method to check if the API is properly wrapped
     */
    public boolean isOk() {
        return false;
    }

    /**
     * Example method, this one gets the stats of a crop
     */
    public SeedStatsExample exampleMethodGetSeedStats(World world, int x, int y, int z) {
        return new SeedStatsExample((short) -1, (short) -1, (short) -1, false);
    }

    /**
     * Example method, this one gets the stat cap imposed to agricraft
     */
    public short exampleMethodGetSeedStatsCap() {
        return -1;
    }
}
