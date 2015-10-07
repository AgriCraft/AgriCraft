package com.InfinityRaider.AgriCraft.compatibility.witchery;

import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

import java.lang.reflect.Method;

public class CropPlantMandrake extends CropPlantWitchery {
    public CropPlantMandrake() {
        super((ItemSeeds) Item.itemRegistry.getObject("witchery:seedsmandrake"));
    }

    @Override
    public boolean onHarvest(World world, int x, int y, int z, EntityPlayer player) {
        TileEntityCrop crop = (TileEntityCrop) world.getTileEntity(x, y, z);
        int amount = (int) (Math.ceil((crop.getStats().gain + 0.00) / 3));
        int strength = crop.getStats().strength;
        while(amount>0) {
            //determine if we want to spawn a mandrake depending on difficulty, time of day and strength of the crop
            boolean spawnMandrake = false;
            if(world.difficultySetting!=EnumDifficulty.PEACEFUL && world.isDaytime()) {
                spawnMandrake = world.rand.nextInt(9)+1>=strength;
            }
            //spawn a mandrake
            if(spawnMandrake) {
                try {
                    //create and spawn the mandrake
                    Entity mandrake = (Entity) Class.forName("com.emoniph.witchery.entity.EntityMandrake").getConstructor(World.class).newInstance(world);
                    //EntityMandrake mandrake = new EntityMandrake(world);
                    mandrake.setLocationAndAngles(1.5D + x, 0.05D + y, 1.5D + z, 0.0F, 0.0F);
                    world.spawnEntityInWorld(mandrake);
                    //particle effect
                    Class particleEffectClass = Class.forName("com.emoniph.witchery.util.ParticleEffect");
                    Object explodeEffect = particleEffectClass.getField("EXPLODE").get(null);
                    //sound effect
                    Class soundEffectClass = Class.forName("com.emoniph.witchery.util.SoundEffect");
                    Object noneEffect = soundEffectClass.getField("NONE").get(null);
                    //create packet
                    Object packetParticles = Class.forName("com.emoniph.witchery.network.PacketParticles").getConstructor(particleEffectClass, soundEffectClass, Entity.class, double.class, double.class).newInstance(explodeEffect, noneEffect, mandrake, 0.5D, 1.0D);
                    //target point
                    Object targetPoint = Class.forName("com.emoniph.witchery.util.TargetPointUtil").getDeclaredMethod("from", Entity.class, double.class).invoke(null, mandrake, 16.0D);
                    //send message
                    Method sendToAllAllAroundMethod = Class.forName("com.emoniph.witchery.network.PacketPipeline").getDeclaredMethod("sendToAllAround", IMessage.class, NetworkRegistry.TargetPoint.class);
                    Object packetPipeline = Class.forName("com.emoniph.witchery.Witchery").getDeclaredField("packetPipeline").get(null);
                    sendToAllAllAroundMethod.invoke(packetPipeline, packetParticles, targetPoint);
                } catch(Exception e) {
                    if(ConfigurationHandler.debug) {
                        e.printStackTrace();
                    }
                    spawnMandrake = false;
                }
            }
            //regular harvest
            if(!spawnMandrake){
                if (world.getGameRules().getGameRuleBooleanValue("doTileDrops") && !world.restoringBlockSnapshots) {
                    ItemStack drop = getRandomFruit(world.rand);
                    float f = 0.7F;
                    double d0 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
                    double d1 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
                    double d2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
                    EntityItem entityitem = new EntityItem(world, x + d0, y + d1, z + d2, drop);
                    entityitem.delayBeforeCanPickup = 10;
                    world.spawnEntityInWorld(entityitem);
                }
            }
            amount--;
        }
        world.setBlockMetadataWithNotify(x, y, z, 2, 2);
        return false;
    }


    @Override
    @SideOnly(Side.CLIENT)
    public boolean renderAsFlower() {
        return false;
    }
}
