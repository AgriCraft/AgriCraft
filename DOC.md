# How to create new plant

Let's say I want to create a potato plant.

First I choose a namespace (`mood` in this exemple), and a plant id (`potato` in this exemple)

```
data
 |- agricraft
 |   |- mood
 |       |- potato.json  # define the plant
assets
 |- mood
     |- models
     |   |- crop
     |   |   |- potato_stage0.json # crop block models to use for this plant
     |   |   |- potato_stage1.json # will use the model according to the growth stage
     |   |   |- potato_stage2.json # must be one for each stage
     |   |- seed
     |       |- potato.json  # crop item model to use for the seed of this plant
     |- textures
         |- seed
             |- potato.png  # texture to use for the seed item (it is not required to be here, as long as the texture is loaded that's fine)
```
translation :
```
seed.agricraft.mood.potato: "Potato Seeds"  # the name of the seed
```

Plant json (`data/agricraft/mood/potato.json`):
```json5
{
	"enabled": true,  // if the plant will be registered in the game
	"mods": [
		"agricraft",
		"minecraft"
	],
	"seeds": [  // seeds to use for this plant
		{
			"item": "minecraft:potato",  // tag or element id of items to use
			"override_planting": true,  // override default item behaviour to place an agricraft plant instead
			"nbt": "",  // [optional] (default="") nbt to match the item
			"grass_drop_chance": 0.0,
			"seed_drop_chance": 1.0,
			"seed_drop_bonus": 0.0
		}
	],
	"stages": [  // growth stages for the plant. each number correspond to the height of the plant at that stage.
		2,
		4,
		6,
		8,  // at the fourth stage, the plant is 8 pixel high
		10,
		12,
		14,
		16
	],
	"harvest_stage": 4,  // After harvest, the growth stage is set to be this one.
	"growth_chance": 0.75,  // The chance the plant has of growing on a random tick
	"growth_bonus": 0.025,  // The chance the plant has of growing bonus on a random tick
	"tier": 1,  // The tier of the plant. A legacy value.
	"cloneable": true,
	"spread_chance": 0.1,
	"products": [  // products produced on harvest
		{
			"item": "minecraft:wheat",  // tog or element id
			"min": 1,
			"max": 3,
			"chance": 0.95,
			"required": true,
			"nbt": "",  // [optional] (default="")
		}
	],
	"clip_products": [  // products produced on clipping. objects are the same as harvest products
	],
	"requirement": {
		"soil_humidity": {
			"condition": "wet",
			"type": "equal",
			"tolerance_factor": 0.15
		},
		"soil_acidity": {
			"condition": "slightly_acidic",
			"type": "equal",
			"tolerance_factor": 0.2
		},
		"soil_nutrients": {
			"condition": "high",
			"type": "equal_or_higher",
			"tolerance_factor": 0.1
		},
		"min_light": 10,
		"max_light": 16,
		"light_tolerance_factor": 0.5,
		"biomes": {  // [optional] (default=this)
			"values": [],
			"blacklist": true,
			"ignore_from_strength": -1 // [optional] (default=-1) {-1=never ignored}
		},
		"dimensions": {  // [optional] (default=this)
			"values": [],
			"blacklist": true,
			"ignore_from_strength": -1 // [optional] (default=this) {-1=never ignored}
		},
		"seasons": [  // [optional] (default=this)
			"spring",
			"summer",
			"autumn",
			"winter"
		],
		"block_conditions": [  // [optional] (default=[]) block condition
			{
				"block": "forge:ores/gold", // tag or element id
				"nbt": "",  // [optional] (default="")
				"states": [],  // [optional] (default=[])
				"strength": 11,
				"amount": 1, // the amount required in the given range
				"min_x": 0,  // The bounding box, relative to the crop at 0, 0, 0 in which the block(s) are to be placed.
				"min_y": -2, // In this case we specify the box {(0, -2, 0), (0, -2, 0)}, which is the single block directly
				"min_z": 0,  // below the soil block. Notice that the upper bound is inclusive, so that to specify a
				"max_x": 0,  // single block we use the same point twice, not the upper left and lower right points.
				"max_y": -2,
				"max_z": 0,
			}
		],
		"fluid_conditions": {  // [optional] (default=this)
			"fluid": "minecraft:empty",  // tag or element id
			"states": [],  // [optional] (default=[])
			"nbt": "",  // [optional] (default="")
		}
	},
	"callbacks": [],  // [optional] (default=[])
	"particle_effects": []  // [optional] (default=[])
}
```