package com.infinityraider.agricraft.impl.v1;

import com.agricraft.agricore.log.AgriLogAdapter;
import com.infinityraider.agricraft.AgriCraft;

import org.apache.logging.log4j.Level;

public class AgriLoggerImpl implements AgriLogAdapter {

    public void log(Level logLevel, Object source, String format, Object... objects) {
        try {
            AgriCraft.instance.getLogger().log(logLevel, "[" + source + "]" + format, objects);
        } catch (IllegalArgumentException ex) {
            // This is bad...
            AgriCraft.instance.getLogger().log(logLevel, "[" + source + "]" + format);
        }
    }

    @Override
    public void all(Object source, String format, Object... objects) {
        log(Level.ALL, source, format, objects);
    }

    @Override
    public void debug(Object source, String format, Object... objects) {
        if (AgriCraft.instance.getConfig().debugMode()) {
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
