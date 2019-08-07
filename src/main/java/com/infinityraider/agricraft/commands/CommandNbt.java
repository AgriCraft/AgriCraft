/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.infinityraider.agricraft.commands;

import com.agricraft.agricore.util.TypeHelper;
import com.google.common.collect.ComparisonChain;
import com.infinityraider.agricraft.utility.StackHelper;
import com.infinityraider.infinitylib.utility.MessageUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
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
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

/**
 *
 * @author Ryan
 */
public class CommandNbt implements ICommand {

    @Override
    public String getName() {
        return "ac.nbt";
    }

    @Override
    public String getUsage(ICommandSender ics) {
        return "ac.nbt <list|get> <name>";
    }

    @Override
    public List<String> getAliases() {
        return Collections.emptyList();
    }

    @Override
    public void execute(MinecraftServer ms, ICommandSender ics, String[] arguments) throws CommandException {
        // Validate the action argument.
        if (arguments.length < 1) {
            throw new CommandException("Too few arguments to command! Expected \"list\" or \"get\" as first argument!");
        }

        // Get the action argument.
        final String action = arguments[0].trim().toLowerCase();

        // Validate the action argument matches the number of arguments.
        if (action.equals("list") && arguments.length != 1) {
            throw new CommandException("Incorrect number of arguments!");
        } else if (action.equals("get") && arguments.length != 2) {
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
        final NBTTagCompound tag = StackHelper.getTag(stack);

        // Switch the action.
        switch (action) {
            case "list":
                listNbt(player, stack, tag);
                return;
            case "get":
                final String target = arguments[1].trim().toLowerCase();
                getNbt(player, stack, tag, target);
                return;
            default:
                throw new CommandException("Invalid action: \"%s\"!", action);
        }
    }

    private void listNbt(@Nonnull EntityPlayer player, @Nonnull ItemStack stack, @Nonnull NBTTagCompound tag) throws CommandException {
        for (String key : tag.getKeySet()) {
            final NBTBase entry = tag.getTag(key);
            final String type = NBTBase.NBT_TYPES[entry.getId()];
            MessageUtil.messagePlayer(player, "`l`3{0}`r `b{1}`r: `2{2}`r", type, key, entry);
        }
    }

    private void getNbt(@Nonnull EntityPlayer player, @Nonnull ItemStack stack, @Nonnull NBTTagCompound tag, @Nonnull String key) throws CommandException {
        if (tag.hasKey(key)) {
            final NBTBase entry = tag.getTag(key);
            final String type = NBTBase.NBT_TYPES[entry.getId()];
            MessageUtil.messagePlayer(player, "`l`3{0}`r `b{1}`r: `2{2}`r", type, key, entry);
        } else {
            throw new CommandException("Active player stack does not have NBT tag: \"%s\"!", key);
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
                completions.add("get");
                completions.add("list");
                break;
            case 2:
                if (arguments[0].trim().toLowerCase().equals("get")) {
                    TypeHelper.cast(ics.getCommandSenderEntity(), EntityPlayer.class)
                            .map(EntityPlayer::getHeldItemMainhand)
                            .map(ItemStack::getTagCompound)
                            .ifPresent(tag -> tag.getKeySet().forEach(completions::add));
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
