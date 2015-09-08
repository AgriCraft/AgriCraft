package com.InfinityRaider.AgriCraft.utility;

import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.reference.Reference;
import cpw.mods.fml.common.FMLLog;
import org.apache.logging.log4j.Level;

/**
 * A class to assist in the logging of the mod.
 * 
 * Credits to Pahimar for this LogHelper class.
 */
public abstract class LogHelper {
	
    /**
     * Logs an object (normally a string), to the {@link FMLLog}.
     * 
     * Please use {@link #debug(Object)} for logs of {@link Level#DEBUG}.
     * 
     * @param logLevel the level at which to log the object, for filtering purposes.
     * @param object the object to be logged (interpreted into a string).
     */
    public static void log(Level logLevel, Object object) {
        FMLLog.log(Reference.MOD_NAME, logLevel, String.valueOf(object));
    }
    
    /**
     * Logs an object to the {@link FMLLog} at the level {@link Level#ALL}.
     * 
     * @param object the object to be logged (interpreted into a string).
     */
    public static void all(Object object) {
        log(Level.ALL, object);
    }
    
    /**
     * Logs an object to the {@link FMLLog} at the level {@link Level#ALL}.
     * 
     * @param object the object to be logged (interpreted into a string).
     */
    public static void debug(Object object) {
        if(ConfigurationHandler.debug) {
            log(Level.INFO, "[AGRI-DEBUG] "+object);
        }
    }
    
    /**
     * Logs an object to the {@link FMLLog} at the level {@link Level#ERROR}.
     * 
     * @param object the object to be logged (interpreted into a string).
     */
    public static void error(Object object) {
        log(Level.ERROR, object);
    }
    
    /**
     * Logs an object to the {@link FMLLog} at the level {@link Level#FATAL}.
     * 
     * @param object the object to be logged (interpreted into a string).
     */
    public static void fatal(Object object) {
        log(Level.FATAL, object);
    }
    
    /**
     * Logs an object to the {@link FMLLog} at the level {@link Level#INFO}.
     * 
     * @param object the object to be logged (interpreted into a string).
     */
    public static void info(Object object) {
            log(Level.INFO, object);
    }
    
    /**
     * Logs an object to the {@link FMLLog} at the level {@link Level#OFF}.
     * 
     * @param object the object to be logged (interpreted into a string).
     */
    public static void off(Object object) {
        log(Level.OFF, object);
    }
    
    /**
     * Logs an object to the {@link FMLLog} at the level {@link Level#TRACE}.
     * 
     * @param object the object to be logged (interpreted into a string).
     */
    public static void trace(Object object) {
        log(Level.TRACE, object);
    }
    
    /**
     * Logs an object to the {@link FMLLog} at the level {@link Level#WARN}.
     * 
     * @param object the object to be logged (interpreted into a string).
     */
    public static void warn(Object object) {
        log(Level.WARN, object);
    }
    
    /**
     * Logs an exception via {@link Exception#printStackTrace()} if debug mode is turned on in the configuration.
     * 
     * @param e an exception to log.
     */
    public static void printStackTrace(Exception e) {
    	if(ConfigurationHandler.debug) {
            e.printStackTrace();
        }
    }

}