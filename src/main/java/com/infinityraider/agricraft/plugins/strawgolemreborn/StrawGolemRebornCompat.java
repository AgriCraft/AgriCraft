package com.infinityraider.agricraft.plugins.strawgolemreborn;

import com.google.common.collect.Lists;
import com.infinityraider.agricraft.content.AgriTileRegistry;
import com.infinityraider.agricraft.content.core.TileEntityCrop;
import com.t2pellet.strawgolem.crop.CropRegistry;
import com.t2pellet.strawgolem.entity.EntityStrawGolem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

class StrawGolemRebornCompat {
    static void init() {
        AgriCropLogic logic = new AgriCropLogic();
        AgriTileRegistry.getInstance().crop.onRegistration(t -> CropRegistry.INSTANCE.register(t, logic, logic, logic));
    }

    private static class AgriCropLogic implements CropRegistry.IHarvestChecker<TileEntityCrop>, CropRegistry.IHarvestLogic<TileEntityCrop>, CropRegistry.IReplantLogic<TileEntityCrop> {
        @Override
        public boolean isMature(TileEntityCrop crop) {
            return crop.isMature();
        }

        @Override
        public List<ItemStack> doHarvest(ServerLevel level, EntityStrawGolem golem, BlockPos pos, TileEntityCrop crop) {
            List<ItemStack> drops = Lists.newArrayList();
            crop.harvest(drops::add, golem);
            return drops;
        }

        @Override
        public void doReplant(Level level, BlockPos pos, TileEntityCrop crop) {
            // do nothing
        }
    }
}
