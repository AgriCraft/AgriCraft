package com.infinityraider.agricraft.util.debug;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationNetwork;
import com.infinityraider.agricraft.capability.CapabilityIrrigationComponent;
import com.infinityraider.agricraft.impl.v1.irrigation.IrrigationNetwork;
import com.infinityraider.infinitylib.utility.MessageUtil;
import com.infinityraider.infinitylib.utility.debug.DebugMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.stream.Stream;

public class DebugModeIrrigationNetwork extends DebugMode {
    @Override
    public String debugName() {
        return "irrigation network data";
    }

    @Override
    public void debugActionBlockClicked(ItemStack stack, ItemUseContext context) {
        CapabilityIrrigationComponent.getInstance().getIrrigationComponent(context.getWorld().getTileEntity(context.getPos())).ifPresent(component -> {
            // Log side:
            MessageUtil.messagePlayer(context.getPlayer(), "{0} Irrigation Network Data", AgriCraft.instance.proxy().getLogicalSide());
            // Log networks
            Stream.concat(Arrays.stream(Direction.values()), Stream.of((Direction) null))
                    .filter(dir -> component.getNode(dir).isPresent())
                    .map(component::getNetwork)
                    .forEach(network -> {
                        String arg;
                        if (network instanceof IrrigationNetwork) {
                            arg = "" + ((IrrigationNetwork) network).getId();
                        } else {
                            arg = network.getClass().getName();
                        }
                        MessageUtil.messagePlayer(context.getPlayer(), "Network: {0}", arg);
                        this.logNetwork(network, context.getPlayer());
                    });
        });
    }

    protected void logNetwork(IAgriIrrigationNetwork network, PlayerEntity player) {
        MessageUtil.messagePlayer(player, " - {0} nodes", "" + network.nodes().size());
        MessageUtil.messagePlayer(player, " - {0} connections", "" + network.connections().size());
    }

    @Override
    public void debugActionClicked(ItemStack stack, World world, PlayerEntity player, Hand hand) {}

    @Override
    public void debugActionEntityClicked(ItemStack stack, PlayerEntity player, LivingEntity target, Hand hand) {}
}
