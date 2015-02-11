package mcp.mobius.waila.api.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.handlers.HUDHandlerEntities;
import mcp.mobius.waila.overlay.OverlayConfig;
import mcp.mobius.waila.utils.Constants;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ConfigHandler implements IWailaConfigHandler {

	/* SINGLETON */
	private static ConfigHandler _instance = null;
	private ConfigHandler() { _instance = this; }
	public static ConfigHandler instance(){	return _instance == null ? new ConfigHandler() : _instance;	}	
	/* === */
	
	private LinkedHashMap<String, ConfigModule> modules = new LinkedHashMap<String, ConfigModule>();
	private ArrayList<String> serverconfigs             = new ArrayList<String>();
	public  HashMap<String, Boolean> forcedConfigs      = new HashMap<String, Boolean>();
	public  Configuration config = null;	
	
	
	public void addModule(String modName, HashMap<String, String> options){
		this.addModule(modName, new ConfigModule(modName, options));
	}

	public void addModule(String modName, ConfigModule options){
		this.modules.put(modName, options);
	}	
	
	@Override
	public Set<String> getModuleNames(){
		return this.modules.keySet();
	}
	
	@Override
	public HashMap<String, String> getConfigKeys(String modName){
		if (this.modules.containsKey(modName))
			return this.modules.get(modName).options;
		else
			return null;
	}
	
	private void saveModuleKey(String modName, String key){
		this.saveModuleKey(modName, key, Constants.CFG_DEFAULT_VALUE);
	}
	
	private void saveModuleKey(String modName, String key, boolean defvalue){
		config.get(Constants.CATEGORY_MODULES, key, defvalue);
		config.get(Constants.CATEGORY_SERVER , key, Constants.SERVER_FREE);			
		config.save();		
	}	

	public void addConfig(String modName, String key, String name){
		this.addConfig(modName, key, name, Constants.CFG_DEFAULT_VALUE);
	}
	
	public void addConfig(String modName, String key, String name, boolean defvalue){
		this.saveModuleKey(modName, key, defvalue);
		
		if (!this.modules.containsKey(modName))
			this.modules.put(modName, new ConfigModule(modName));
		
		this.modules.get(modName).addOption(key, name);
	}

	public void addConfigServer(String modName, String key, String name){
		this.addConfigServer(modName, key, name, Constants.CFG_DEFAULT_VALUE);
	}
	
	public void addConfigServer(String modName, String key, String name, boolean defvalue){
		this.saveModuleKey(modName, key, defvalue);
		
		if (!this.modules.containsKey(modName))
			this.modules.put(modName, new ConfigModule(modName));
		
		this.modules.get(modName).addOption(key, name);
		this.serverconfigs.add(key);
	}	
	
	@Override
	public boolean getConfig(String key){
		return this.getConfig(key, Constants.CFG_DEFAULT_VALUE);
	}	
	
	@Override
	public boolean getConfig(String key, boolean defvalue){
		if (this.serverconfigs.contains(key) && !Waila.instance.serverPresent)
			return false;
		
		if (this.forcedConfigs.containsKey(key))
			return this.forcedConfigs.get(key);
		
		Property prop = config.get(Constants.CATEGORY_MODULES, key, defvalue);
		return prop.getBoolean(defvalue);		
	}
	
	public boolean isServerRequired(String key){
		return this.serverconfigs.contains(key);
	}

	
	
	
	
	
	/* GENERAL ACCESS METHODS TO GET/SET VALUES IN THE CONFIG FILE */
	
	public boolean getConfig(String category, String key, boolean default_){
		Property prop = config.get(category, key, default_);
		return prop.getBoolean(default_);		
	}	
	
	public void setConfig(String category, String key, boolean state){
		config.getCategory(category).put(key, new Property(key,String.valueOf(state),Property.Type.BOOLEAN));
		config.save();		
	}
	
	public int getConfig(String category, String key, int default_){
		Property prop = config.get(category, key, default_);
		return prop.getInt();		
	}	
	
	public void setConfig(String category, String key, int state){
		config.getCategory(category).put(key, new Property(key,String.valueOf(state),Property.Type.INTEGER));
		config.save();		
	}

	
	
	
	
	
	/* Default config loading */
	
	public void loadDefaultConfig(FMLPreInitializationEvent event){
		config = new Configuration(event.getSuggestedConfigurationFile());
	
		config.get(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SHOW,       true);
		config.get(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_MODE,       true);
		config.get(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_LIQUID,     false);
		config.get(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_METADATA,   false);
		config.get(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_KEYBIND,    true);
		config.get(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_NEWFILTERS, true);
		
		OverlayConfig.posX = config.get(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_POSX,     5000).getInt();
		OverlayConfig.posY = config.get(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_POSY,     100).getInt();
	
		OverlayConfig.alpha =     config.get(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_ALPHA,     80).getInt();
		OverlayConfig.bgcolor =   config.get(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_BGCOLOR,   0x100010).getInt();
		OverlayConfig.gradient1 = config.get(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_GRADIENT1, 0x5000ff).getInt();
		OverlayConfig.gradient2 = config.get(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_GRADIENT2, 0x28007f).getInt();
		OverlayConfig.fontcolor = config.get(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_FONTCOLOR, 0xA0A0A0).getInt();
		OverlayConfig.scale     = config.get(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SCALE,     100).getInt() / 100.0f;

		HUDHandlerEntities.nhearts      = config.get(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_NHEARTS, 20).getInt();
		HUDHandlerEntities.maxhpfortext = config.get(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_MAXHP, 40).getInt();
		
		config.getCategory(Constants.CATEGORY_MODULES).setComment("Those are the config keys defined in modules.\nServer side, it is used to enforce keys client side using the next section.");		
		config.getCategory(Constants.CATEGORY_SERVER).setComment("Any key set to true here will ensure that the client is using the configuration set in the 'module' section above.\nThis is useful for enforcing false to 'cheating' keys like silverfish.");
		
		config.save();	
	}
}
