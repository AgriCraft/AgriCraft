package com.infinityraider.agricraft.content.world;

import com.google.common.collect.ImmutableSet;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.content.AgriBlockRegistry;
import com.infinityraider.agricraft.content.AgriTabs;
import com.infinityraider.agricraft.reference.AgriToolTips;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.item.BlockItemBase;
import com.infinityraider.infinitylib.item.InfinityItemProperty;
import com.infinityraider.infinitylib.modules.keyboard.ModuleKeyboard;
import com.infinityraider.infinitylib.render.item.IClientItemProperties;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class ItemGreenHouseMonitor extends BlockItemBase {
    public ItemGreenHouseMonitor() {
        super(AgriBlockRegistry.getInstance().greenhouse_monitor.get(), new Properties()
                .tab(AgriTabs.TAB_AGRICRAFT)
                .stacksTo(1));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level world, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag advanced) {
        tooltip.add(AgriToolTips.GREENHOUSE_MONITOR_L1);
        if(ModuleKeyboard.getInstance().isKeyPressed(Minecraft.getInstance().options.keyShift)) {
            tooltip.add(AgriToolTips.GREENHOUSE_MONITOR_L2);
            tooltip.add(AgriToolTips.GREENHOUSE_MONITOR_L3);
            tooltip.add(AgriToolTips.GREENHOUSE_MONITOR_L4);
            tooltip.add(AgriToolTips.GREENHOUSE_MONITOR_L5);
            tooltip.add(AgriToolTips.GREENHOUSE_MONITOR_L6);
        } else {
            tooltip.add(new TextComponent("")
                    .withStyle(ChatFormatting.DARK_GRAY).append(AgriToolTips.SNEAK_INFO));
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Supplier<IClientItemProperties> getClientItemProperties() {
        return () -> new IClientItemProperties(){
            @Nonnull
            @Override
            public Set<InfinityItemProperty> getModelProperties() {
                return ImmutableSet.of(new InfinityItemProperty(new ResourceLocation(AgriCraft.instance.getModId(), Names.Objects.GREENHOUSE)) {
                    @Override
                    public float getValue(ItemStack stack, @Nullable ClientLevel clientWorld, @Nullable LivingEntity entity, int seed) {
                        // 0 : complete greenhouse
                        // 1 : greenhouse with insufficient glass
                        // 2 : greenhouse with gaps
                        // 3 : no greenhouse (removed)
                        entity = entity == null ? AgriCraft.instance.proxy().getClientPlayer() : entity;
                        Level world = clientWorld == null ? entity.getLevel() : clientWorld;
                        BlockPos pos = entity.blockPosition();
                        BlockState state = world.getBlockState(pos);
                        if(state.hasProperty(BlockGreenHouseAir.STATE.getProperty())) {
                            return BlockGreenHouseAir.STATE.fetch(state).ordinal();
                        }
                        state = world.getBlockState(pos.above());
                        if(state.hasProperty(BlockGreenHouseAir.STATE.getProperty())) {
                            return BlockGreenHouseAir.STATE.fetch(state).ordinal();
                        }
                        state = world.getBlockState(pos.below());
                        if(state.hasProperty(BlockGreenHouseAir.STATE.getProperty())) {
                            return BlockGreenHouseAir.STATE.fetch(state).ordinal();
                        }
                        return 3;
                    }
                });
            }
        };
    }
}
