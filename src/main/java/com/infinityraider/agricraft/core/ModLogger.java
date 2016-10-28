/*
 */
package com.infinityraider.agricraft.core;

import java.text.MessageFormat;

import org.apache.logging.log4j.Level;

import net.minecraftforge.fml.common.FMLLog;

import com.agricraft.agricore.log.AgriLogAdapter;

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
        log(Level.INFO, source, "[AGRI-DEBUG]: " + format, objects);
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
