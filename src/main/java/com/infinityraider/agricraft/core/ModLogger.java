/*
 */
package com.infinityraider.agricraft.core;

import com.agricraft.agricore.log.AgriLogAdapter;
import java.text.MessageFormat;
import net.minecraftforge.fml.common.FMLLog;
import org.apache.logging.log4j.Level;

/**
 *
 * @author RlonRyan
 */
public class ModLogger implements AgriLogAdapter {
	
	public void log(Level logLevel, Object source, String format, Object... objects) {
		FMLLog.log(String.valueOf(source), logLevel, MessageFormat.format(format, objects));
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
		debug(source, "{0}", e);
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
