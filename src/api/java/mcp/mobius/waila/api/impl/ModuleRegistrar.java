package mcp.mobius.waila.api.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.logging.log4j.Level;

import au.com.bytecode.opencsv.CSVReader;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IWailaBlockDecorator;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.api.IWailaFMPDecorator;
import mcp.mobius.waila.api.IWailaFMPProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.IWailaSummaryProvider;
import mcp.mobius.waila.api.IWailaTooltipRenderer;
import mcp.mobius.waila.cbcore.LangUtil;
import mcp.mobius.waila.utils.Constants;

public class ModuleRegistrar implements IWailaRegistrar {

	private static ModuleRegistrar instance = null;

	public LinkedHashMap<Class, ArrayList<IWailaDataProvider>> headBlockProviders  = new LinkedHashMap<Class, ArrayList<IWailaDataProvider>>();
	public LinkedHashMap<Class, ArrayList<IWailaDataProvider>> bodyBlockProviders  = new LinkedHashMap<Class, ArrayList<IWailaDataProvider>>();
	public LinkedHashMap<Class, ArrayList<IWailaDataProvider>> tailBlockProviders  = new LinkedHashMap<Class, ArrayList<IWailaDataProvider>>();	
	public LinkedHashMap<Class, ArrayList<IWailaDataProvider>> stackBlockProviders = new LinkedHashMap<Class, ArrayList<IWailaDataProvider>>();	
	public LinkedHashMap<Class, ArrayList<IWailaDataProvider>> NBTDataProviders    = new LinkedHashMap<Class, ArrayList<IWailaDataProvider>>();
	
	public LinkedHashMap<Class, ArrayList<IWailaBlockDecorator>> blockClassDecorators = new LinkedHashMap<Class,   ArrayList<IWailaBlockDecorator>>();
	
	public LinkedHashMap<Class, ArrayList<IWailaEntityProvider>> headEntityProviders      = new LinkedHashMap<Class, ArrayList<IWailaEntityProvider>>();
	public LinkedHashMap<Class, ArrayList<IWailaEntityProvider>> bodyEntityProviders      = new LinkedHashMap<Class, ArrayList<IWailaEntityProvider>>();
	public LinkedHashMap<Class, ArrayList<IWailaEntityProvider>> tailEntityProviders      = new LinkedHashMap<Class, ArrayList<IWailaEntityProvider>>();
	public LinkedHashMap<Class, ArrayList<IWailaEntityProvider>> overrideEntityProviders  = new LinkedHashMap<Class, ArrayList<IWailaEntityProvider>>();	
	public LinkedHashMap<Class, ArrayList<IWailaEntityProvider>> NBTEntityProviders       = new LinkedHashMap<Class, ArrayList<IWailaEntityProvider>>();
	
	public LinkedHashMap<String, ArrayList<IWailaFMPProvider>> headFMPProviders = new LinkedHashMap<String, ArrayList<IWailaFMPProvider>>();
	public LinkedHashMap<String, ArrayList<IWailaFMPProvider>> bodyFMPProviders = new LinkedHashMap<String, ArrayList<IWailaFMPProvider>>();
	public LinkedHashMap<String, ArrayList<IWailaFMPProvider>> tailFMPProviders = new LinkedHashMap<String, ArrayList<IWailaFMPProvider>>();	

	public LinkedHashMap<String, ArrayList<IWailaFMPDecorator>> FMPClassDecorators = new LinkedHashMap<String, ArrayList<IWailaFMPDecorator>>();	
	
	public LinkedHashMap<Class, HashSet<String>> syncedNBTKeys = new LinkedHashMap<Class, HashSet<String>>();
	
	public LinkedHashMap<String, LinkedHashMap <String, LinkedHashMap <String, String>>> wikiDescriptions = new LinkedHashMap<String, LinkedHashMap <String, LinkedHashMap <String, String>>>();
	public LinkedHashMap<Class, ArrayList<IWailaSummaryProvider>> summaryProviders = new LinkedHashMap<Class, ArrayList<IWailaSummaryProvider>>();
	
	public LinkedHashMap<String, String> IMCRequests = new LinkedHashMap<String, String>();	
	
	public LinkedHashMap<String, IWailaTooltipRenderer> tooltipRenderers = new LinkedHashMap<String, IWailaTooltipRenderer>();
	
	private ModuleRegistrar() {
		instance = this;
	}

	public static ModuleRegistrar instance(){ 
		if (ModuleRegistrar.instance == null)
			ModuleRegistrar.instance = new ModuleRegistrar();
		return ModuleRegistrar.instance;
	}

	/* IMC HANDLING */
	public void addIMCRequest(String method, String modname){
		this.IMCRequests.put(method, modname);
	}
	
	/* CONFIG HANDLING */
	@Override
	public void addConfig(String modname, String key, String configname) {
		this.addConfig(modname, key, configname, Constants.CFG_DEFAULT_VALUE);
	}

	@Override
	public void addConfigRemote(String modname, String key, String configname) {
		this.addConfigRemote(modname, key, configname, Constants.CFG_DEFAULT_VALUE);
	}	
	
	@Override
	public void addConfig(String modname, String key) {
		this.addConfig(modname, key, Constants.CFG_DEFAULT_VALUE);
	}

	@Override
	public void addConfigRemote(String modname, String key) {
		this.addConfigRemote(modname, key, Constants.CFG_DEFAULT_VALUE);
	}	
	
	@Override
	public void addConfig(String modname, String key, String configname, boolean defvalue) {
		ConfigHandler.instance().addConfig(modname, key, LangUtil.translateG(configname), defvalue);
	}

	@Override
	public void addConfigRemote(String modname, String key, String configname, boolean defvalue) {
		ConfigHandler.instance().addConfigServer(modname, key, LangUtil.translateG(configname), defvalue);
	}	
	
	@Override
	public void addConfig(String modname, String key, boolean defvalue) {
		ConfigHandler.instance().addConfig(modname, key, LangUtil.translateG("option." + key), defvalue);
	}

	@Override
	public void addConfigRemote(String modname, String key, boolean defvalue) {
		ConfigHandler.instance().addConfigServer(modname, key, LangUtil.translateG("option." + key), defvalue);
	}	
	
	
	/* REGISTRATION METHODS */
	@Override
	public void registerHeadProvider(IWailaDataProvider dataProvider, Class block) {
		this.registerProvider(dataProvider, block, this.headBlockProviders);		
	}	

	@Override
	public void registerBodyProvider(IWailaDataProvider dataProvider, Class block) {
		this.registerProvider(dataProvider, block, this.bodyBlockProviders);
	}	
	
	@Override
	public void registerTailProvider(IWailaDataProvider dataProvider, Class block) {
		this.registerProvider(dataProvider, block, this.tailBlockProviders);
	}		
	
	@Override
	public void registerStackProvider(IWailaDataProvider dataProvider, Class block) {
		this.registerProvider(dataProvider, block, this.stackBlockProviders);
	}		

	@Override
	public void registerNBTProvider(IWailaDataProvider dataProvider, Class entity) {
		this.registerProvider(dataProvider, entity, this.NBTDataProviders);	
	}	
	
	@Override
	public void registerHeadProvider(IWailaEntityProvider dataProvider, Class entity) {
		this.registerProvider(dataProvider, entity, this.headEntityProviders);		
	}	

	@Override
	public void registerBodyProvider(IWailaEntityProvider dataProvider, Class entity) {
		this.registerProvider(dataProvider, entity, this.bodyEntityProviders);			
	}	
	
	@Override
	public void registerTailProvider(IWailaEntityProvider dataProvider, Class entity) {
		this.registerProvider(dataProvider, entity, this.tailEntityProviders);		
	}	
	
	@Override
	public void registerNBTProvider(IWailaEntityProvider dataProvider, Class entity) {
		this.registerProvider(dataProvider, entity, this.NBTEntityProviders);	
	}		
	
	@Override
	public void registerHeadProvider(IWailaFMPProvider dataProvider, String name) {
		this.registerProvider(dataProvider, name, this.headFMPProviders);		
	}	

	@Override
	public void registerBodyProvider(IWailaFMPProvider dataProvider, String name) {
		this.registerProvider(dataProvider, name, this.bodyFMPProviders);			
	}	
	
	@Override
	public void registerTailProvider(IWailaFMPProvider dataProvider, String name) {
		this.registerProvider(dataProvider, name, this.tailFMPProviders);		
	}		
	
	@Override
	public void registerOverrideEntityProvider (IWailaEntityProvider dataProvider, Class entity){
		this.registerProvider(dataProvider, entity, this.overrideEntityProviders);			
	}	

	/*
	@Override
	public void registerShortDataProvider(IWailaSummaryProvider dataProvider, Class item) {
		this.registerProvider(dataProvider, item, this.summaryProviders);	
	}
	*/	

	@Override
	public void registerDecorator(IWailaBlockDecorator decorator, Class block) {
		this.registerProvider(decorator, block, this.blockClassDecorators);	
	}	
	
	@Override
	public void registerDecorator(IWailaFMPDecorator decorator, String name) {
		this.registerProvider(decorator, name, this.FMPClassDecorators);	
	}	
	
	private <T, V> void registerProvider(T dataProvider, V clazz, LinkedHashMap<V, ArrayList<T>> target) {
		if (clazz == null || dataProvider == null)
			throw new RuntimeException(String.format("Trying to register a null provider or null block ! Please check the stacktrace to know what was the original registration method. [Provider : %s, Target : %s]", dataProvider.getClass().getName(), clazz));
		
		if (!target.containsKey(clazz))
			target.put(clazz, new ArrayList<T>());
		
		ArrayList<T> providers =target.get(clazz);
		if (providers.contains(dataProvider)) return;		
		
		target.get(clazz).add(dataProvider);		
	}	
	
	@Deprecated
	@Override
	public void registerSyncedNBTKey(String key, Class target){
		if (!this.syncedNBTKeys.containsKey(target))
			this.syncedNBTKeys.put(target, new HashSet<String>());
		
		this.syncedNBTKeys.get(target).add(key);		
	}	
	
	@Override
	public void registerTooltipRenderer(String name, IWailaTooltipRenderer renderer){
		if (!this.tooltipRenderers.containsKey(name))
			this.tooltipRenderers.put(name, renderer);
		else
			Waila.log.warn(String.format("A renderer named %s already exists (Class : %s). Skipping new renderer.", name, renderer.getClass().getName()));
	}
	
	/* PROVIDER GETTERS */
	
	public Map<Integer, List<IWailaDataProvider>> getHeadProviders(Object block) {
		return getProviders(block, this.headBlockProviders);
	}

	public Map<Integer, List<IWailaDataProvider>> getBodyProviders(Object block) {
		return getProviders(block, this.bodyBlockProviders);		
	}	

	public Map<Integer, List<IWailaDataProvider>> getTailProviders(Object block) {
		return getProviders(block, this.tailBlockProviders);
	}	

	public Map<Integer, List<IWailaDataProvider>> getStackProviders(Object block) {
		return getProviders(block, this.stackBlockProviders);
	}		
	
	public Map<Integer, List<IWailaDataProvider>> getNBTProviders(Object block) {
		return getProviders(block, this.NBTDataProviders);
	}	
	
	public Map<Integer, List<IWailaEntityProvider>> getHeadEntityProviders(Object entity) {
		return getProviders(entity, this.headEntityProviders);		
	}

	public Map<Integer, List<IWailaEntityProvider>> getBodyEntityProviders(Object entity) {
		return getProviders(entity, this.bodyEntityProviders);
	}	

	public Map<Integer, List<IWailaEntityProvider>> getTailEntityProviders(Object entity) {
		return getProviders(entity, this.tailEntityProviders);
	}		
	
	public Map<Integer, List<IWailaEntityProvider>> getOverrideEntityProviders(Object entity) {
		return getProviders(entity, this.overrideEntityProviders);
	}		

	public Map<Integer, List<IWailaEntityProvider>> getNBTEntityProviders(Object entity) {
		return getProviders(entity, this.NBTEntityProviders);
	}		
	
	public Map<Integer, List<IWailaFMPProvider>> getHeadFMPProviders(String name) {
		return getProviders(name, this.headFMPProviders);		
	}	
	
	public Map<Integer, List<IWailaFMPProvider>> getBodyFMPProviders(String name) {
		return getProviders(name, this.bodyFMPProviders);
	}	

	public Map<Integer, List<IWailaFMPProvider>> getTailFMPProviders(String name) {
		return getProviders(name, this.tailFMPProviders);
	}		
	
	public Map<Integer, List<IWailaSummaryProvider>> getSummaryProvider(Object item){
		return getProviders(item, this.summaryProviders);
	}	
	
	public Map<Integer, List<IWailaBlockDecorator>> getBlockDecorators(Object block){
		return getProviders(block, this.blockClassDecorators);
	}	
	
	public Map<Integer, List<IWailaFMPDecorator>> getFMPDecorators(String name){
		return getProviders(name, this.FMPClassDecorators);
	}		
	
	public IWailaTooltipRenderer getTooltipRenderer(String name){
		return this.tooltipRenderers.get(name);
	}
	
	private <T> Map<Integer, List<T>> getProviders(Object obj, LinkedHashMap<Class, ArrayList<T>> target){
		Map<Integer, List<T>> returnList = new TreeMap<Integer, List<T>>();
		Integer index = 0;
		
		for (Class clazz : target.keySet()){
			if (clazz.isInstance(obj))
				returnList.put(index, target.get(clazz));
			
			index++;
		}
		
		return returnList;		
	}	
	
	private <T> Map<Integer, List<T>> getProviders(String name, LinkedHashMap<String, ArrayList<T>> target){
		Map<Integer, List<T>> returnList = new TreeMap<Integer, List<T>>();
		returnList.put(0, target.get(name));
		return returnList;		
	}		
	
	@Deprecated
	public HashSet<String> getSyncedNBTKeys(Object target){
		HashSet<String> returnList = new HashSet<String>();
		for (Class clazz : this.syncedNBTKeys.keySet())
			if (clazz.isInstance(target))
				returnList.addAll(this.syncedNBTKeys.get(clazz));
				
		return returnList;		
	}
	
	/* HAS METHODS */
	
	public boolean hasStackProviders(Object block){
		return hasProviders(block, this.stackBlockProviders);
	}	
	
	public boolean hasHeadProviders(Object block){
		return hasProviders(block, this.headBlockProviders);		
	}
	
	public boolean hasBodyProviders(Object block){
		return hasProviders(block, this.bodyBlockProviders);
	}

	public boolean hasTailProviders(Object block){
		return hasProviders(block, this.tailBlockProviders);
	}

	public boolean hasNBTProviders(Object block){
		return hasProviders(block, this.NBTDataProviders);
	}	
	
	public boolean hasHeadEntityProviders(Object entity){
		return hasProviders(entity, this.headEntityProviders);
	}
	
	public boolean hasBodyEntityProviders(Object entity){
		return hasProviders(entity, this.bodyEntityProviders);
	}

	public boolean hasTailEntityProviders(Object entity){
		return hasProviders(entity, this.tailEntityProviders);
	}	

	public boolean hasOverrideEntityProviders(Object entity){
		return hasProviders(entity, this.overrideEntityProviders);
	}		
	
	public boolean hasNBTEntityProviders(Object entity){
		return hasProviders(entity, this.NBTEntityProviders);
	}		
	
	public boolean hasHeadFMPProviders(String name){
		return hasProviders(name, this.headFMPProviders);
	}
	
	public boolean hasBodyFMPProviders(String name){
		return hasProviders(name, this.bodyFMPProviders);
	}

	public boolean hasTailFMPProviders(String name){
		return hasProviders(name, this.tailFMPProviders);
	}		

	public boolean hasBlockDecorator(Object block){
		return hasProviders(block, this.blockClassDecorators);
	}	

	public boolean hasFMPDecorator(String name){
		return hasProviders(name, this.FMPClassDecorators);
	}		
	
	private <T> boolean hasProviders(Object obj, LinkedHashMap<Class, ArrayList<T>> target){
		for (Class clazz : target.keySet())
			if (clazz.isInstance(obj))
				return true;
		return false;		
	}	
	
	private <T> boolean hasProviders(String name, LinkedHashMap<String, ArrayList<T>> target){
		return target.containsKey(name);
	}	
	
	public boolean hasSummaryProvider(Class item){
		return this.summaryProviders.containsKey(item);
	}	
	
	@Deprecated
	public boolean hasSyncedNBTKeys(Object target){
		for (Class clazz : this.syncedNBTKeys.keySet())
			if (clazz.isInstance(target))
				return true;
		return false;
	}		
	
	/* ----------------- */
	/*
	@Override
	public void registerDocTextFile(String filename) {
		List<String[]> docData  = null;
		int    nentries = 0;
		
		
		try{
			docData = this.readFileAsString(filename);
		} catch (IOException e){
			Waila.log.log(Level.WARN, String.format("Error while accessing file %s : %s", filename, e));
			return;
		}

		for (String[] ss : docData){
			String modid  = ss[0];
			String name   = ss[1];
			String meta   = ss[2];
			String desc   = ss[5].replace('$', '\n');
			if (!(desc.trim().equals(""))){
				if (!this.wikiDescriptions.containsKey(modid))
					this.wikiDescriptions.put(modid, new LinkedHashMap <String, LinkedHashMap <String, String>>());
				if (!this.wikiDescriptions.get(modid).containsKey(name))
					this.wikiDescriptions.get(modid).put(name, new LinkedHashMap<String, String>());
				
				this.wikiDescriptions.get(modid).get(name).put(meta, desc);
				System.out.printf("Registered %s %s %s\n", modid, name, meta);
				nentries += 1;			
			}
		}
		
		
//		String[] sections = docData.split(">>>>");
//		for (String s : sections){
//			s.trim();
//			if (!s.equals("")){
//				try{
//					String name   = s.split("\r?\n",2)[0].trim();
//					String desc   = s.split("\r?\n",2)[1].trim();
//					if (!this.wikiDescriptions.containsKey(modid))
//						this.wikiDescriptions.put(modid, new LinkedHashMap <String, String>());
//					this.wikiDescriptions.get(modid).put(name, desc);
//					nentries += 1;
//				}catch (Exception e){
//					System.out.printf("%s\n", e);
//				}
//			}
//		}
		
		Waila.log.log(Level.INFO, String.format("Registered %s entries from %s", nentries, filename));
	}	
	*/

	public boolean hasDocTextModID(String modid){
		return this.wikiDescriptions.containsKey(modid);
	}

	public boolean hasDocTextItem(String modid, String item){
		if (this.hasDocTextModID(modid))
			return this.wikiDescriptions.get(modid).containsKey(item);
		return false;
	}
	
	public boolean hasDocTextMeta(String modid, String item, String meta){
		if (this.hasDocTextItem(modid, item))
			return this.wikiDescriptions.get(modid).get(item).containsKey(meta);
		return false;		
	}	

	public LinkedHashMap<String, String> getDocText(String modid, String name){
		return this.wikiDescriptions.get(modid).get(name);
	}	
	
	public String getDocText(String modid, String name, String meta){
		return this.wikiDescriptions.get(modid).get(name).get(meta);
	}
	
	public boolean hasDocTextSpecificMeta(String modid, String name, String meta){
		for (String s : this.getDocText(modid, name).keySet())
			if (s.equals(meta))
				return true;
		return false;
	}
	
	public String getDoxTextWildcardMatch(String modid, String name){
		Set<String> keys = this.wikiDescriptions.get(modid).keySet();
		for (String s : keys){
			String regexed = s;
			regexed = regexed.replace(".", "\\.");
			regexed = regexed.replace("*", ".*");
			
			if (name.matches(s))
				return s;			
		}
		return null;
	}
	
	private List<String[]> readFileAsString(String filePath) throws IOException {
//		URL fileURL   = this.getClass().getResource(filePath);
//		File filedata = new File(fileURL);
//		Reader paramReader = new InputStreamReader(this.getClass().getResourceAsStream(filePath));
		
		InputStream in = getClass().getResourceAsStream(filePath);
		BufferedReader input = new BufferedReader(new InputStreamReader(in));		
		CSVReader reader = new CSVReader(input);
		
		List<String[]> myEntries = reader.readAll();
		reader.close();
		
		return myEntries;
		/*
		StringBuffer fileData = new StringBuffer();
        //BufferedReader reader = new BufferedReader(paramReader);
		
        char[] buf = new char[1024];
        int numRead=0;
        while((numRead=input.read(buf)) != -1){
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        input.close();
        return fileData.toString();
        */
	}
}
