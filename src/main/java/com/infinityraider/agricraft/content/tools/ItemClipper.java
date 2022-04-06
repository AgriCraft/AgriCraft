package com.infinityraider.agricraft.content.tools;

import com.google.common.collect.Lists;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.event.AgriCropEvent;
import com.infinityraider.agricraft.api.v1.content.items.IAgriClipperItem;
import com.infinityraider.agricraft.content.AgriTabs;
import com.infinityraider.agricraft.reference.AgriToolTips;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.item.ItemBase;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemClipper extends ItemBase implements IAgriClipperItem {
    public ItemClipper() {
        super(Names.Items.CLIPPER, new Properties()
                .tab(AgriTabs.TAB_AGRICRAFT)
                .stacksTo(1)
        );
    }

    @Nonnull
    @Override
    public InteractionResult useOn(@Nonnull UseOnContext context) {
        Level world = context.getLevel();
        if(world.isClientSide()) {
            return InteractionResult.PASS;
        }
        BlockPos pos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();
        return AgriApi.getCrop(world, pos).map(crop -> {
            if(!crop.hasPlant() || !crop.getPlant().allowsClipping(crop.getGrowthStage(), stack, player)) {
                if(player != null) {
                    player.sendMessage(AgriToolTips.MSG_CLIPPING_IMPOSSIBLE, player.getUUID());
                }
                return InteractionResult.FAIL;
            }
            if(MinecraftForge.EVENT_BUS.post(new AgriCropEvent.Clip.Pre(crop, stack, player))) {
                return InteractionResult.FAIL;
            }
            List<ItemStack> drops = Lists.newArrayList();
            crop.getPlant().getClipProducts(drops::add, stack, crop.getGrowthStage(), crop.getStats(), world.getRandom());
            crop.setGrowthStage(crop.getPlant().getInitialGrowthStage());
            crop.getPlant().onClipped(crop, stack, player);
            AgriCropEvent.Clip.Post event = new AgriCropEvent.Clip.Post(crop, stack, drops, player);
            MinecraftForge.EVENT_BUS.post(event);
            event.getDrops().forEach(crop::dropItem);
            return InteractionResult.SUCCESS;
        }).orElse(InteractionResult.FAIL);
    }
}
