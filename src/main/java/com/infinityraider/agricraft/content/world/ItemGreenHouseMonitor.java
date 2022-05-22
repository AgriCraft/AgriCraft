package com.infinityraider.agricraft.content.world;

import com.google.common.collect.ImmutableSet;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.content.world.IAgriGreenHouse;
import com.infinityraider.agricraft.content.AgriBlockRegistry;
import com.infinityraider.agricraft.content.AgriTabs;
import com.infinityraider.agricraft.handler.GreenHouseHandler;
import com.infinityraider.agricraft.reference.AgriToolTips;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.item.BlockItemBase;
import com.infinityraider.infinitylib.item.InfinityItemProperty;
import com.infinityraider.infinitylib.modules.keyboard.ModuleKeyboard;
import com.infinityraider.infinitylib.render.item.IClientItemProperties;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ItemGreenHouseMonitor extends BlockItemBase {
    private static final MutableComponent[] MESSAGES = {
            AgriToolTips.MSG_GREENHOUSE_COMPLETE,
            AgriToolTips.MSG_GREENHOUSE_INSUFFICIENT_GLASS,
            AgriToolTips.MSG_GREENHOUSE_GAPS,
            AgriToolTips.MSG_GREENHOUSE_REMOVED
    };

    private static final MutableComponent[] TOOLTIPS = {
            new TextComponent("").append(AgriToolTips.MSG_GREENHOUSE_COMPLETE).withStyle(ChatFormatting.DARK_GRAY),
            new TextComponent("").append(AgriToolTips.MSG_GREENHOUSE_INSUFFICIENT_GLASS).withStyle(ChatFormatting.DARK_GRAY),
            new TextComponent("").append(AgriToolTips.MSG_GREENHOUSE_GAPS).withStyle(ChatFormatting.DARK_GRAY),
            new TextComponent("").append(AgriToolTips.MSG_GREENHOUSE_REMOVED).withStyle(ChatFormatting.DARK_GRAY)
    };

    public ItemGreenHouseMonitor() {
        super(AgriBlockRegistry.getInstance().greenhouse_monitor.get(), new Properties()
                .tab(AgriTabs.TAB_AGRICRAFT)
                .stacksTo(1));
    }

    public MutableComponent getFeedbackMessage(IAgriGreenHouse.State state) {
        return MESSAGES[state.ordinal()];
    }

    protected MutableComponent getFeedbackTooltip(IAgriGreenHouse.State state) {
        return TOOLTIPS[state.ordinal()];
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        this.useLogic(world, player);
        if(world.isClientSide()) {
            return InteractionResultHolder.success(player.getItemInHand(hand));
        } else {
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if(context.getPlayer() == null || context.getPlayer().isDiscrete()) {
            return super.useOn(context);
        } else {
            if(context.getPlayer() != null) {
                this.useLogic(context.getLevel(), context.getPlayer());
            }
        }
        return context.getLevel().isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
    }

    public void useLogic(Level world, Player player) {
        if(world.isClientSide()) {
            return;
        }
        IAgriGreenHouse.State state;
        if(player.isDiscrete()) {
            state = GreenHouseHandler.getInstance().createGreenHouse(world, player.blockPosition().above())
                    .map(IAgriGreenHouse::getState)
                    .orElse(IAgriGreenHouse.State.REMOVED);
        } else {
            state = GreenHouseHandler.getInstance().getGreenHouse(world, player.blockPosition().above())
                    .map(IAgriGreenHouse::getState)
                    .orElse(IAgriGreenHouse.State.REMOVED);
        }
        player.sendMessage(this.getFeedbackMessage(state), player.getUUID());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level world, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag advanced) {
        tooltip.add(AgriToolTips.GREENHOUSE_MONITOR_L1);
        world = world == null ? AgriCraft.instance.getClientWorld() : world;
        LivingEntity player = AgriCraft.instance.getClientPlayer();
        if(world != null && player != null) {
            tooltip.add(this.getFeedbackTooltip(this.getGreenHouseState(world, player)));
        }
        if(ModuleKeyboard.getInstance().isKeyPressed(Minecraft.getInstance().options.keyShift)) {
            tooltip.add(AgriToolTips.EMPTY_LINE);
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

    public IAgriGreenHouse.State getGreenHouseState(Level world, LivingEntity entity) {
        BlockPos pos = entity.blockPosition();
        BlockState state = world.getBlockState(pos);
        if(state.hasProperty(BlockGreenHouseAir.STATE.getProperty())) {
            return BlockGreenHouseAir.STATE.fetch(state);
        }
        state = world.getBlockState(pos.above());
        if(state.hasProperty(BlockGreenHouseAir.STATE.getProperty())) {
            return BlockGreenHouseAir.STATE.fetch(state);
        }
        state = world.getBlockState(pos.below());
        if(state.hasProperty(BlockGreenHouseAir.STATE.getProperty())) {
            return BlockGreenHouseAir.STATE.fetch(state);
        }
        return IAgriGreenHouse.State.REMOVED;
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
                        entity = entity == null ? AgriCraft.instance.getClientPlayer() : entity;
                        Level world = clientWorld == null ? entity.getLevel() : clientWorld;
                        return getGreenHouseState(world, entity).ordinal();
                    }
                });
            }
        };
    }
}
