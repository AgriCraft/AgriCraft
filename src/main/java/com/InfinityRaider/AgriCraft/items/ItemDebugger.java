package com.InfinityRaider.AgriCraft.items;

import com.InfinityRaider.AgriCraft.blocks.BlockCrop;
import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityTank;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.SeedHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemDebugger extends ModItem {
    public ItemDebugger() {
        super();
        this.setCreativeTab(null);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if(!world.isRemote) {
            LogHelper.debug("Clicked block at: (" + x + "," + y + "," + z + "):");
            //print data for crop
            if (world.getBlock(x, y, z) == Blocks.blockCrop) {
                TileEntityCrop cropTE = (TileEntityCrop) world.getTileEntity(x, y, z);
                BlockCrop blockCropBlock = (BlockCrop) world.getBlock(x, y, z);
                if (cropTE.crossCrop) {
                    LogHelper.debug(" - This is a crosscrop");
                }
                if (cropTE.hasPlant()) {
                    LogHelper.debug(" - This crop has a plant");
                    LogHelper.debug(" - Seed: " + ((ItemSeeds) cropTE.seed).getUnlocalizedName());
                    LogHelper.debug(" - RegisterName: " + Item.itemRegistry.getNameForObject(cropTE.seed) + ':' + cropTE.seedMeta);
                    LogHelper.debug(" - Plant: " + SeedHelper.getPlant((ItemSeeds) cropTE.seed).getUnlocalizedName());
                    LogHelper.debug(" - Meta: " + blockCropBlock.getPlantMetadata(world, x, y, z));
                    LogHelper.debug(" - Growth: " + cropTE.growth);
                    LogHelper.debug(" - Gain: " + cropTE.gain);
                    LogHelper.debug(" - Strength: " + cropTE.strength);
                    LogHelper.debug(" - Fertile: " + cropTE.isFertile());
                }
            }

            //print data for tank
            else if (world.getBlock(x, y, z) == Blocks.blockWaterTank) {
                TileEntityTank tank = (TileEntityTank) world.getTileEntity(x, y, z);
                LogHelper.debug("Tank: " + (tank.isWood() ? "wood" : "iron") + " (single capacity: " + tank.getSingleCapacity() + ")");
                LogHelper.debug("  - MultiBlock: " + tank.isMultiBlock());
                LogHelper.debug("  - Connected tanks: " + tank.getConnectedTanks());
                LogHelper.debug("  - MultiBlock ID: " + tank.getMultiBlockId());
                int[] size = tank.getDimensions();
                LogHelper.debug("  - MultiBlock Size: " + size[0] + "x" + size[1] + "x" + size[2]);
                LogHelper.debug("  - FluidLevel: " + tank.getFluidLevel() + "/" + tank.getTotalCapacity());
                boolean left = tank.isMultiBlockPartner(world.getTileEntity(x - 1, y, z));
                boolean right = tank.isMultiBlockPartner(world.getTileEntity(x + 1, y, z));
                boolean back = tank.isMultiBlockPartner(world.getTileEntity(x, y, z - 1));
                boolean front = tank.isMultiBlockPartner(world.getTileEntity(x, y, z + 1));
                boolean top = tank.isMultiBlockPartner(world.getTileEntity(x, y + 1, z));
                boolean below = tank.isMultiBlockPartner(world.getTileEntity(x, y - 1, z));
                LogHelper.debug("  - Found multiblock partners on: " + (left ? "left, " : "") + (right ? "right, " : "") + (back ? "back, " : "") + (front ? "front, " : "") + (top ? "top, " : "") + (below ? "below" : ""));
            }
        }

        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg) {
    }
}
