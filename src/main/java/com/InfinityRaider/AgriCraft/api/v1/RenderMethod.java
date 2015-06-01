package com.InfinityRaider.AgriCraft.api.v1;

public enum RenderMethod {
    CROSSED(1),
    HASHTAG(6);

    private int renderId;

    RenderMethod(int renderId) {
        this.renderId = renderId;
    }

    public int renderId() {return renderId;}

    public static RenderMethod getRenderMethod(int renderId) {
        for(RenderMethod renderMethod:RenderMethod.values()) {
            if(renderMethod.renderId == renderId) {
                return renderMethod;
            }
        }
        return null;
    }
}


