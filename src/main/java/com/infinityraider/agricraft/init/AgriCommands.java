/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.infinityraider.agricraft.init;

import com.infinityraider.agricraft.commands.CommandNbt;
import com.infinityraider.agricraft.commands.CommandStat;
import javax.annotation.Nonnull;

/**
 *
 * @author Ryan
 */
public class AgriCommands {
    
    private static final AgriCommands INSTANCE = new AgriCommands();

    public static AgriCommands getInstance() {
        return INSTANCE;
    }    
    
    private AgriCommands() {
        this.STAT = new CommandStat();
        this.NBT = new CommandNbt();
    }
    
    @Nonnull
    public final CommandStat STAT;
    
    @Nonnull
    public final CommandNbt NBT;
    
}
