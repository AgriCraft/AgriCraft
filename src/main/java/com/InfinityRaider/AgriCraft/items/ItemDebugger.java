package com.InfinityRaider.AgriCraft.items;

import com.InfinityRaider.AgriCraft.utility.DebugHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemDebugger extends ModItem {
    public ItemDebugger() {
        super();
        this.setCreativeTab(null);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        DebugHelper.debug(player, world, x, y ,z);
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg) {
        this.itemIcon = reg.registerIcon(this.getUnlocalizedName().substring(this.getUnlocalizedName().indexOf('.')+1));
    }
}
