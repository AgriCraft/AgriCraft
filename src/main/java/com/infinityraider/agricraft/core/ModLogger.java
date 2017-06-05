/*
 */
package com.infinityraider.agricraft.core;

import com.agricraft.agricore.log.AgriLogAdapter;
import com.infinityraider.agricraft.reference.AgriCraftConfig;
import java.text.MessageFormat;
import net.minecraftforge.fml.common.FMLLog;
import org.apache.logging.log4j.Level;

/**
 *
 *
 */
public class ModLogger implements AgriLogAdapter {

    public void log(Level logLevel, Object source, String format, Object... objects) {
        try {
            FMLLog.log(String.valueOf(source), logLevel, MessageFormat.format(format, objects));
        } catch (IllegalArgumentException ex) {
            // This is bad...
            FMLLog.log(String.valueOf(source), logLevel, format);
        }
    }

    @Override
    public void all(Object source, String format, Object... objects) {
        log(Level.ALL, source, format, objects);
    }

    @Override
    public void debug(Object source, String format, Object... objects) {
        if (AgriCraftConfig.debug) {
            log(Level.INFO, source, "[DEBUG]: " + format, objects);
        }
    }

    @Override
    public void trace(Object source, Exception e) {
        debug(source, e.getLocalizedMessage());
    }

    @Override
    public void error(Object source, String format, Object... objects) {
        log(Level.ERROR, source, format, objects);
    }

    @Override
    public void info(Object source, String format, Object... objects) {
        log(Level.INFO, source, format, objects);
    }

    @Override
    public void warn(Object source, String format, Object... objects) {
        log(Level.WARN, source, format, objects);
    }

    @Override
    public void severe(Object source, String format, Object... objects) {
        log(Level.FATAL, source, format, objects);
    }

}
