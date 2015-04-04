package com.InfinityRaider.AgriCraft.compatibility.witchery;

import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.entity.EntityMandrake;
import com.emoniph.witchery.network.PacketParticles;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import com.emoniph.witchery.util.TargetPointUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemSeeds;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class CropPlantMandrake extends CropPlantWitchery {
    public CropPlantMandrake() {
        super((ItemSeeds) Witchery.Items.SEEDS_MANDRAKE);
    }

    @Override
    public boolean onHarvest(World world, int x, int y, int z) {
        TileEntityCrop crop = (TileEntityCrop) world.getTileEntity(x, y, z);
        int amount = (int) (Math.ceil((crop.getGain() + 0.00) / 3));
        int strength = crop.getStrength();
        while(amount>0) {
            //determine if we want to spawn a mandrake depending on difficulty, time of day and strength of the crop
            boolean spawnMandrake = false;
            if(world.difficultySetting!=EnumDifficulty.PEACEFUL && world.isDaytime()) {
                spawnMandrake = world.rand.nextInt(9)+1>=strength;
            }
            //spawn a mandrake
            if(spawnMandrake) {
                EntityMandrake mandrake = new EntityMandrake(world);
                mandrake.setLocationAndAngles(0.5D + (double) x, 0.05D + (double) y, 0.5D + (double) z, 0.0F, 0.0F);
                world.spawnEntityInWorld(mandrake);
                Witchery.packetPipeline.sendToAllAround(new PacketParticles(ParticleEffect.EXPLODE, SoundEffect.NONE, mandrake, 0.5D, 1.0D), TargetPointUtil.from(mandrake, 16.0D));

            }
            //regular harvest
            else {
                float f = 0.7F;
                double d0 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                double d1 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                double d2 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                EntityItem entityitem = new EntityItem(world, (double)x + d0, (double)y + d1, (double)z + d2, getRandomFruit(world.rand));
                entityitem.delayBeforeCanPickup = 10;
                world.spawnEntityInWorld(entityitem);
            }
            amount--;
        }
        return false;
    }


    @Override
    @SideOnly(Side.CLIENT)
    public boolean renderAsFlower() {
        return false;
    }
}
