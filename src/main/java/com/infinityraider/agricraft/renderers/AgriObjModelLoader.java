/*
 */
package com.infinityraider.agricraft.renderers;

import com.agricraft.agricore.core.AgriCore;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 */
public class AgriObjModelLoader {

    private static final ConcurrentHashMap<String, OBJModel> models = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Pair<String, VertexFormat>, IBakedModel> baked = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Pair<String, VertexFormat>, List<BakedQuad>> quads = new ConcurrentHashMap<>();

    public static final Optional<OBJModel> getObjModel(String path) {
        if (!models.containsKey(path)) {
            AgriCore.getLogger("agricraft").debug("Fetching OBJ Model: {0}!", path);
            loadObjModel(path)
                    .ifPresent(m -> models.put(path, m));
        }
        return Optional.ofNullable(models.get(path));
    }

    private static final Optional<OBJModel> loadObjModel(String path) {
        AgriCore.getLogger("agricraft").debug("Loading OBJ Model: {0}!", path);
        final ResourceLocation loc = new ResourceLocation(path);
        try {
            return Optional.ofNullable(OBJLoader.INSTANCE.loadModel(loc))
                    .filter(m -> m instanceof OBJModel)
                    .map(m -> (OBJModel) m);
        } catch (Exception e) {
            AgriCore.getLogger("AgriCraft").debug("Exeption loading OBJ Model!");
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static final Optional<IBakedModel> getBakedObjModel(String path, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
        final Pair<String, VertexFormat> key = Pair.of(path, format);
        if (!baked.containsKey(key)) {
            AgriCore.getLogger("agricraft").debug("Baking OBJ Model: {0}!", path);
            getObjModel(path)
                    .map(m -> m.bake(m.getDefaultState(), format, textureGetter::apply))
                    .ifPresent(b -> baked.put(key, b));
        }
        return Optional.ofNullable(baked.get(key));
    }

    public static final Optional<List<BakedQuad>> getBasicObjQuads(String path, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
        final Pair<String, VertexFormat> key = Pair.of(path, format);
        if (!quads.containsKey(key)) {
            AgriCore.getLogger("agricraft").debug("Generating OBJ Model Quads: {0}!", path);
            getBakedObjModel(path, format, textureGetter)
                    .map(m -> m.getQuads(null, null, 0))
                    .filter(q -> !q.isEmpty())
                    .ifPresent(q -> quads.put(key, q));
            AgriCore.getLogger("agricraft").debug("Baked Quads: {0}", quads.get(path));
        }
        return Optional.ofNullable(quads.get(key));
    }

}
