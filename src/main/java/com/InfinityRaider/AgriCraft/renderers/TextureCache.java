package com.InfinityRaider.AgriCraft.renderers;

import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.google.common.base.Charsets;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.block.model.ModelBlockDefinition;
import net.minecraft.client.renderer.block.statemap.BlockStateMapper;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nullable;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** This class is used to obtain and cache TextureAtlasSprites which AgriCraft uses for rendering */
@SideOnly(Side.CLIENT)
public class TextureCache implements IIconCache {
    /** The static instance */
    private static TextureCache INSTANCE;

    private static final List<IBlockState> cachedObjects = new ArrayList<>();

    /** Pointer to the IResourceManager instance */
    private IReloadableResourceManager resourceManager;
    /** DefaultStateMapper object used for blocks without their own IStateMapper object */
    private final DefaultStateMapper defaultMapper;
    /** Map containing all blocks with their own IStateMapper object and their respective IStateMapper instance */
    private final Map<Block, IStateMapper> blockStateMap;

    /** Map to cache ModelResourceLocations for IBlockStates */
    private final Map<IBlockState, ModelResourceLocation> modelLocationsMap;
    /** Map to cache Variants for ModelResourceLocations */
    private final Map<ModelResourceLocation, ModelBlockDefinition.Variants> variantsMap;
    /** Map to cache ModelBlocks for ResourceLocations */
    private final Map<ResourceLocation, ModelBlock> modelMap;
    /** Map to cache ModelBlock instances for IBlockStates */
    private final Map<IBlockState, List<ModelBlock>> modelCache;
    /** Map to cache TextureAtlasSprite instances for IBlockStates */
    private final Map<IBlockState, List<TextureAtlasSprite>> textureCache;

    private TextureCache(IReloadableResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        this.defaultMapper = new DefaultStateMapper();

        this.blockStateMap = retrieveBlockStateMap(retrieveModelManager());
        this.modelLocationsMap = Maps.<IBlockState, ModelResourceLocation>newLinkedHashMap();
        this.variantsMap = Maps.<ModelResourceLocation, ModelBlockDefinition.Variants>newLinkedHashMap();
        this.modelMap = Maps.<ResourceLocation, ModelBlock>newLinkedHashMap();
        this.modelCache = Maps.<IBlockState, List<ModelBlock>>newLinkedHashMap();
        this.textureCache = Maps.<IBlockState, List<TextureAtlasSprite>>newLinkedHashMap();

        resourceManager.registerReloadListener(this);
    }

    public static TextureCache getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new TextureCache((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager());
        }
        return INSTANCE;
    }

    @Override
    public void addBlockStateToCache(IBlockState state) {
        cachedObjects.add(state);
    }

    @Override
    public List<TextureAtlasSprite> queryIcons(IBlockState state) {
        return textureCache.get(state);
    }

    @Override
    public List<TextureAtlasSprite> retrieveIcons(IBlockState state) {
        if(textureCache.containsKey(state)) {
            return queryIcons(state);
        } else {
            addBlockStateToCache(state);
            return retrieveBlockIcons(state);
        }
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        if(resourceManager instanceof IReloadableResourceManager) {
            initializeCache((IReloadableResourceManager) resourceManager);
        } else {
            cachedObjects.forEach(INSTANCE::retrieveBlockIcons);
        }
    }

    private void initializeCache(IReloadableResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        cachedObjects.forEach(INSTANCE::retrieveBlockIcons);
    }

    private List<TextureAtlasSprite> retrieveBlockIcons(IBlockState state) {
        if(!textureCache.containsKey(state)) {
            List<TextureAtlasSprite> list = new ArrayList<>();
            for(ModelBlock model : retrieveBlockModels(state)) {
                Map<String, String> textures = model.textures;
                for(String path : textures.values()) {
                    ResourceLocation resourceLocation = new ResourceLocation(modelLocationsMap.get(state).getResourceDomain(), path);
                    TextureAtlasSprite textureAtlasSprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(resourceLocation.toString());
                    list.add(textureAtlasSprite);
                }
            }
            textureCache.put(state, list);
        }
        return textureCache.get(state);
    }

    /** Retrieves a list of block models from a block state */
    private List<ModelBlock> retrieveBlockModels(IBlockState state) {
        if(!modelCache.containsKey(state)) {
            List<ModelBlock> list = new ArrayList<>();
            ModelBlockDefinition.Variants variants = getModelVariants(state);
            if (variants != null) {
                list.addAll(getModelFromVariants(variants));
            }
            modelCache.put(state, list);
        }
        return modelCache.get(state);
    }

    /** Finds a list of BlockModel from the modelMap, or tries to parse it from a json file if it doesn't find it */
    private List<ModelBlock> getModelFromVariants(ModelBlockDefinition.Variants variants) {
        List<ModelBlock> list = new ArrayList<>();
        for(ModelBlockDefinition.Variant variant : variants.getVariants()) {
            ResourceLocation resourcelocation = variant.getModelLocation();
            if (!this.modelMap.containsKey(resourcelocation)) {
                try {
                    ModelBlock modelblock = this.loadModel(resourcelocation);
                    this.modelMap.put(resourcelocation, modelblock);
                } catch (Exception e) {
                    LogHelper.printStackTrace(e);
                }
            }
            list.add(modelMap.get(resourcelocation));
        }
        return list;
    }

    /** Parses a model from a json file */
    private ModelBlock loadModel(ResourceLocation resourceLocation) throws IOException {
        String s = resourceLocation.getResourcePath();
        Reader reader;
        if (s.startsWith("builtin/")) {
            //If everything goes well, this should never require a built in model
            throw new FileNotFoundException(resourceLocation.toString());
        } else {
            IResource resource = this.resourceManager.getResource(this.getModelLocation(resourceLocation));
            reader = new InputStreamReader(resource.getInputStream(), Charsets.UTF_8);
        }
        ModelBlock actualModel;
        try {
            ModelBlock modelblock = ModelBlock.deserialize(reader);
            modelblock.name = resourceLocation.toString();
            actualModel = modelblock;
        } finally {
            reader.close();
        }
        return actualModel;

    }

    /** Method to get the variants containing the model locations for an IBlockState, the are stored in the variantsMap after retrieval */
    @Nullable
    private ModelBlockDefinition.Variants getModelVariants(IBlockState state) {
        ModelResourceLocation resourceLocation = getModelResourceLocation(state);
        ModelBlockDefinition modelBlockDefinition = getModelBlockDefinition(resourceLocation);
        if(resourceLocation != null && modelBlockDefinition != null) {
            try {
                ModelBlockDefinition.Variants variant = modelBlockDefinition.getVariants(resourceLocation.getVariant());
                variantsMap.put(resourceLocation, variant);
            } catch(Exception e) {
                LogHelper.printStackTrace(e);
            }
        }
        return variantsMap.get(resourceLocation);
    }

    /**
     * Gets a ModelResourceLocation from an IBlockState, returns null if the reflection fails
     * It does so by checking if there is already an entry for it in the modelLocationsMap
     * If there is none, it parses it from the json file and adds it to the modelLocationsMap
     */
    @Nullable
    private ModelResourceLocation getModelResourceLocation(IBlockState state) {
        if(!modelLocationsMap.containsKey(state)) {
            StateMapperBase mapper = (StateMapperBase) Objects.firstNonNull(this.blockStateMap.get(state.getBlock()), defaultMapper);
            invokeModelResourceLocationMethod(mapper, state);
        }
        return modelLocationsMap.get(state);
    }

    /** Reads a ModelBlockDefinition from an IBlockState ResourceLocation, returns null if the reflection fails */
    @Nullable
    private ModelBlockDefinition getModelBlockDefinition(ResourceLocation resourceLocation) {
        if(resourceLocation == null) {
            return null;
        }
        ResourceLocation blockStateLocation = this.getBlockStateLocation(resourceLocation);
        List<ModelBlockDefinition> list = Lists.<ModelBlockDefinition>newArrayList();
        try {
            List<IResource> resources = this.resourceManager.getAllResources(blockStateLocation);
            for (IResource iresource : resources) {
                InputStream inputstream = null;
                try {
                    inputstream = iresource.getInputStream();
                    ModelBlockDefinition subModelDefinition = ModelBlockDefinition.parseFromReader(new InputStreamReader(inputstream, Charsets.UTF_8));
                    list.add(subModelDefinition);
                } catch (Exception e) {
                    LogHelper.printStackTrace(e);
                } finally {
                    IOUtils.closeQuietly(inputstream);
                }
            }
        } catch (Exception e) {
            LogHelper.printStackTrace(e);
            return null;
        }
        return new ModelBlockDefinition(list);
    }

    /** Converts a raw ResourceLocation into a ResourceLocation pointing towards the json file for the block state  */
    private ResourceLocation getBlockStateLocation(ResourceLocation resourceLocation) {
        return new ResourceLocation(resourceLocation.getResourceDomain(), "blockstates/" + resourceLocation.getResourcePath() + ".json");
    }

    /** Converts a raw ResourceLocation into a ResourceLocation pointing towards the json file for the model  */
    protected ResourceLocation getModelLocation(ResourceLocation resourceLocation) {
        return new ResourceLocation(resourceLocation.getResourceDomain(), "models/" + resourceLocation.getResourcePath() + ".json");
    }

    /**
     * METHODS USING REFLECTION TO READ PRIVATE/PROTECTED METHODS OR FIELDS FROM VANILLA CLASSES
     */

    /** Retrieves the model manager from the Minecraft instance */
    private ModelManager retrieveModelManager() {
        Minecraft minecraft = Minecraft.getMinecraft();
        ModelManager modelManager = null;
        for(Field field : minecraft.getClass().getDeclaredFields()) {
            if(field.getType() == ModelManager.class) {
                field.setAccessible(true);
                try {
                    modelManager = (ModelManager) field.get(minecraft);
                } catch(Exception e) {
                    LogHelper.printStackTrace(e);
                }
                field.setAccessible(false);
                break;
            }
        }
        return modelManager == null ? new ModelManager(minecraft.getTextureMapBlocks()) : modelManager;
    }

    /** Retrieves the Map containing the StateMapper objects for every block from a ModelManager instance */
    @SuppressWarnings("unchecked")
    private Map<Block, IStateMapper> retrieveBlockStateMap(ModelManager modelManager) {
        Map<Block, IStateMapper> map = null;
        BlockStateMapper mapper = modelManager.getBlockModelShapes().getBlockStateMapper();
        for(Field field : mapper.getClass().getDeclaredFields()) {
            if(field.getType() == Map.class) {
                field.setAccessible(true);
                try {
                    map = (Map<Block, IStateMapper>) field.get(mapper);
                } catch(Exception e) {
                    LogHelper.printStackTrace(e);
                }
                field.setAccessible(false);
                break;
            }
        }
        return map;
    }

    /** Invokes the method to get a ModelResourceLocation from a block state in a StateMapper instance and adds it to the modelMap */
    private void invokeModelResourceLocationMethod(StateMapperBase stateMapper, IBlockState state) {
        ModelResourceLocation resourceLocation = null;
        if(stateMapper == defaultMapper) {
            resourceLocation = new ModelResourceLocation(Block.blockRegistry.getNameForObject(state.getBlock()), stateMapper.getPropertyString(state.getProperties()));
        } else {
            for (Method method : stateMapper.getClass().getDeclaredMethods()) {
                if (method.getReturnType() == ModelResourceLocation.class) {
                    method.setAccessible(true);
                    try {
                        resourceLocation = (ModelResourceLocation) method.invoke(stateMapper, state);
                    } catch(Exception e) {
                        LogHelper.printStackTrace(e);
                    }
                    method.setAccessible(false);
                    break;
                }
            }
        }
        if(resourceLocation != null) {
            modelLocationsMap.put(state, resourceLocation);
        }
    }
}
