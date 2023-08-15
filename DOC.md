# How to create new plant

Let's say I want to create a potato plant.

First I choose a namespace (`mood` in this exemple), and a plant id (`potato` in this exemple)

```
data
 |- agricraft
     |- mood
         |- potato.json  # define the plant
assets
 |- mood
     |- models
         |- crop
             |- potato_stage0.json # crop block models to use for this plant
         |- seed
             |- potato.json  # crop item model to use for the seed of this plant
     |- textures
         |- seed
             |- potato.png  # texture to use for the seed item (it is not required to be here, as long as the texture is loaded that's fine)
```
translation :
```
seed.agricraft.mood.potato: "Potato Seeds"  # the name of the seed
```