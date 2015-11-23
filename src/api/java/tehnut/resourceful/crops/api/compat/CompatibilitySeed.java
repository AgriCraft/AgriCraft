package tehnut.resourceful.crops.api.compat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import tehnut.resourceful.crops.api.base.Seed;
import tehnut.resourceful.crops.api.registry.SeedRegistry;

public enum CompatibilitySeed {

    ;

    private String modid;
    private boolean config;

    CompatibilitySeed(String modid, boolean config) {
        this.modid = modid;
        this.config = config;
    }

    public ItemStack onRightClick(ItemStack stack, World world, EntityPlayer player) {
        return stack;
    }

    public String getModid() {
        return modid;
    }

    public boolean getConfig() {
        return config;
    }

    public Seed getCompatSeed() {
        return null;
    }

    String getCompatSuffix() {
        return "-Compat";
    }

    Seed getSeed(ItemStack stack) {
        return SeedRegistry.getSeed(stack.getItemDamage());
    }
}
