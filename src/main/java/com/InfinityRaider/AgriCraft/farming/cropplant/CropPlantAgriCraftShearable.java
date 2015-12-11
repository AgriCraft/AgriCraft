package com.InfinityRaider.AgriCraft.farming.cropplant;

import com.InfinityRaider.AgriCraft.api.v1.IAgriCraftPlant;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class CropPlantAgriCraftShearable extends CropPlantAgriCraft {
    private Item item;
    private int meta;

    public CropPlantAgriCraftShearable(IAgriCraftPlant plant, ItemStack shearingResult) {
        super(plant);
        this.item = shearingResult.getItem();
        this.meta = shearingResult.getItemDamage();
    }

    @Override
    public boolean onHarvest(World world, int x, int y, int z, EntityPlayer player) {
        if(player == null) {
            return true;
        }
        if(player.getCurrentEquippedItem() == null) {
            return true;
        }
        if(player.getCurrentEquippedItem().getItem() == null) {
            return true;
        }
        if(!(player.getCurrentEquippedItem().getItem() instanceof ItemShears)) {
            return true;
        }
        TileEntityCrop crop = (TileEntityCrop) world.getTileEntity(x, y, z);
        crop.getWorldObj().setBlockMetadataWithNotify(crop.xCoord, crop.yCoord, crop.zCoord, 2, 2);
        int amount = ((int) (Math.ceil((crop.getGain() + 0.00) / 3)))/2;
        if(amount>0) {
            ItemStack drop = new ItemStack(item, amount, meta);
            if (world.getGameRules().getGameRuleBooleanValue("doTileDrops") && !world.restoringBlockSnapshots) {
                float f = 0.7F;
                double d0 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                double d1 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                double d2 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                EntityItem entityitem = new EntityItem(world, (double) x + d0, (double) y + d1, (double) z + d2, drop);
                entityitem.delayBeforeCanPickup = 10;
                world.spawnEntityInWorld(entityitem);
            }
        }
        player.getCurrentEquippedItem().damageItem(1, player);
        return false;
    }
}
