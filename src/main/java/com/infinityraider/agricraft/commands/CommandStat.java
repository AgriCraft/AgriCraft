/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.infinityraider.agricraft.commands;

import com.agricraft.agricore.util.MathHelper;
import com.agricraft.agricore.util.TypeHelper;
import com.google.common.collect.ComparisonChain;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import com.infinityraider.agricraft.reference.AgriCraftConfig;
import com.infinityraider.agricraft.utility.StackHelper;
import com.infinityraider.infinitylib.utility.MessageUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

/**
 *
 * @author Ryan
 */
public class CommandStat implements ICommand {

    @Override
    public String getName() {
        return "ac.stat";
    }

    @Override
    public String getUsage(ICommandSender ics) {
        return "ac.stat <set|get> <growth|gain|strength|all> <value>";
    }

    @Override
    public List<String> getAliases() {
        return Collections.emptyList();
    }

    @Override
    public void execute(MinecraftServer ms, ICommandSender ics, String[] arguments) throws CommandException {
        // Validate the action argument.
        if (arguments.length < 1) {
            throw new CommandException("Too few arguments to command! Expected \"set\" or \"get\" as first argument!");
        }

        // Get the action argument.
        final String action = arguments[0].trim().toLowerCase();

        // Validate the action argument matches the number of arguments.
        if (action.equals("get") && arguments.length != 2) {
            throw new CommandException("Incorrect number of arguments!");
        } else if (action.equals("set") && arguments.length != 3) {
            throw new CommandException("Incorrect number of arguments!");
        }

        // Validate the command sender.
        @Nonnull
        final EntityPlayer player = TypeHelper.cast(ics.getCommandSenderEntity(), EntityPlayer.class)
                .orElseThrow(() -> new CommandException("Command must be invoked by a player!"));

        // Validate the command target stack.
        @Nonnull
        final ItemStack stack = Optional.ofNullable(player.getHeldItemMainhand())
                .filter(StackHelper::isValid)
                .orElseThrow(() -> new CommandException("Player must be holding valid itemstack to be used as a target!"));

        // Validate the command stat.
        @Nonnull
        final IAgriStat stat = AgriApi.getStatRegistry().valueOf(stack)
                .orElseThrow(() -> new CommandException("Player must be holding an AgriCraft seed item."));

        // Switch the action.
        switch (action) {
            case "get":
                getStat(player, stack, stat, arguments[1].trim().toLowerCase());
                return;
            case "set":
                final int value = MathHelper.parseIntOr(arguments[2], -1);
                setStat(player, stack, stat, arguments[1].trim().toLowerCase(), value);
                return;
            default:
                throw new CommandException("Invalid action: \"%s\"!", action);
        }
    }

    private void setStat(@Nonnull EntityPlayer player, @Nonnull ItemStack stack, @Nonnull IAgriStat stat, @Nonnull String target, @Nonnull int value) throws CommandException {
        switch (target) {
            case "growth":
                MessageUtil.messagePlayer(player, "Growth: {0} -> {1}", stat.getGrowth(), value);
                stat.withGrowth(value);
                break;
            case "gain":
                MessageUtil.messagePlayer(player, "Gain: {0} -> {1}", stat.getGain(), value);
                stat.withGain(value);
                break;
            case "strength":
                MessageUtil.messagePlayer(player, "Strength: {0} -> {1}", stat.getStrength(), value);
                stat.withStrength(value);
                break;
            case "all":
                MessageUtil.messagePlayer(player, "Growth: {0} -> {1}", stat.getGrowth(), value);
                stat.withGrowth(value);
                MessageUtil.messagePlayer(player, "Gain: {0} -> {1}", stat.getGain(), value);
                stat.withGain(value);
                MessageUtil.messagePlayer(player, "Strength: {0} -> {1}", stat.getStrength(), value);
                stat.withStrength(value);
                break;
            default:
                throw new CommandException("Invalid target: \"{0}\"!", target);
        }
    }

    private void getStat(@Nonnull EntityPlayer player, @Nonnull ItemStack stack, @Nonnull IAgriStat stat, @Nonnull String target) throws CommandException {
        switch (target) {
            case "id":
                MessageUtil.messagePlayer(player, "ID: {0}", stat.getId());
                break;
            case "growth":
                MessageUtil.messagePlayer(player, "Growth: {0}", stat.getGrowth());
                break;
            case "gain":
                MessageUtil.messagePlayer(player, "Gain: {0}", stat.getGrowth());
                break;
            case "strength":
                MessageUtil.messagePlayer(player, "Strength: {0}", stat.getGrowth());
                break;
            case "all":
                MessageUtil.messagePlayer(player, "ID: {0}", stat.getId());
                MessageUtil.messagePlayer(player, "Growth: {0}", stat.getGrowth());
                MessageUtil.messagePlayer(player, "Gain: {0}", stat.getGain());
                MessageUtil.messagePlayer(player, "Strength: {0}", stat.getStrength());
                break;
            default:
                throw new CommandException("Invalid target: \"%s\"!", target);
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer ms, ICommandSender ics) {
        // Temporary for testing purposes (only enabled in creative mode atm, since quick hack).
        return TypeHelper.cast(ics.getCommandSenderEntity(), EntityPlayer.class)
                .filter(EntityPlayer::isCreative)
                .isPresent();
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer ms, ICommandSender ics, String[] arguments, BlockPos bp) {
        // The list of possible completions.
        final List<String> completions = new ArrayList<>();

        // Determine the possible completions, based off of the number of arguments.
        switch (arguments.length) {
            case 1:
                completions.add("set");
                completions.add("get");
                break;
            case 2:
                completions.add("growth");
                completions.add("gain");
                completions.add("strength");
                completions.add("all");
                break;
            case 3:
                if (arguments[0].trim().toLowerCase().equals("set")) {
                    completions.add(String.valueOf(0));
                    completions.add(String.valueOf(AgriCraftConfig.cropStatCap));
                }
                break;
        }

        // Return the list of possible completions.
        return completions;
    }

    @Override
    public boolean isUsernameIndex(String[] strings, int i) {
        return false;
    }

    @Override
    public int compareTo(ICommand o) {
        return ComparisonChain.start()
                .compare(this.getName(), o.getName())
                .result();
    }

}
