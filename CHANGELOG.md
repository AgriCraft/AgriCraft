# Changelog

## 4.0.2-beta

- ADDED: WTHIT plugin (unilock)
- CHANGED: allow harvesting crops with items in hand (unilock)
- FIXED: crash bonemealing empty crops (unilock)
- FIXED: only add nbt to item produces when it's not empty (unilock)
- FIXED: item tags for recipes (unilock)
- FIXED: crash when accessing server side registry from client (unilock)
- FIXED: crop sticks block translation key (unilock)

## 4.0.1-beta

- FIXED: fabric accesswideners (unilock)
- FIXED: crash because of missing override in CropBlockEntity (unilock)
- FIXED: crash in EMI when redering soil tooltip with no suitable soils for current settings (unilock)
- FIXED: recipes/tags for fabric (unilock)

## 4.0.0-beta

- rewrite for 1.20.4 in multiloader (forge, neoforge, fabric)
- changes in the json configurations, they are now a datapack+resourcepack
- changes in the json for the plants, some keys have been removed, notably the ones for the models/textures as they are defined as a resourcepack now
- changes in the java api, lots of things were removed/added
