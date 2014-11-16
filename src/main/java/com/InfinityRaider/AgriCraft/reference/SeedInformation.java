package com.InfinityRaider.AgriCraft.reference;

//yes, I got the information for most harvestcraft plants from wikipedia, go ahead, call the fucking cops.

import com.InfinityRaider.AgriCraft.compatibility.LoadedMods;
import com.InfinityRaider.AgriCraft.items.ItemModSeed;
import com.InfinityRaider.AgriCraft.utility.SeedHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;

public final class SeedInformation {
    //agricraft seeds
    public static final String potato = "The potato is the base of almost every meal, however some have even found ways to turn it into a beverage.";
    public static final String carrot = "The carrot is a vegetable suited for many diets.";
    public static final String sugarcane = "Usually reeds only grow near water, but an agriculturist should be able to mutate this plant into growing about everywhere.";
    public static final String dandelion = "Dandelions are yellow flowers found very commonly in every location exposed to the sun.";
    public static final String poppy = "Though it is very common, it is still a very pretty, deep red flower.";
    public static final String orchid = "A flower which looks good in many gardens, in fact no garden is complete without orchids in it.";
    public static final String allium = "Allium is a flower that is grown for it's purple petals.";
    public static final String tulipPink = "The tulip comes in many colors, but only a true agriculturist appreciates the pink ones.";
    public static final String tulipOrange = "Orange, one of the many colors in the tulip spectrum.";
    public static final String tulipRed = "There is a saying: 'there are 50 shades of tulip, but red is the most beautiful'.";
    public static final String tulipWhite = "There are many white flowers, but many say the white tulip is the white flower of white flowers.";
    public static final String daisy = "Daisies are quite common, but nonetheless pretty white flowers.";
    public static final String diamahlia = "Many still believe this flower only exists in tales and stories. However some agriculturists say they have succeeded in obtaining this legendary plant.";
    public static final String aurigold = "Aurigold is a rare and fabled flower that feeds on tiny fractions of gold in the soil, only a very patient and skilled agriculturist is able to cultivate this jewel.";
    public static final String ferranium = "This strange plant's roots do not care for water. They just burrow in the soil while absorbing iron particles to strengthen itself.";
    public static final String lapender = "Besides it's beautiful blue color this flower also produces shiny pebbles.";
    public static final String emeryllis = "If you talk to the village agriculturist, they will be able to tell you tales about this plant and the infinite riches it would provide.";
    public static final String redstodendron = "A strange aura seems to be emanating from redstodendron bushes.";
    public static final String cuprosia = "Cuprosia is an odd flower which extracts copper from the soil to spring it's beautiful petals.";
    public static final String petinia = "This flower only thrives on soils rich in tin.";
    public static final String plombean = "Sometimes people mistake this plant for regular beans and die of lead poisoning after consuming the fruits.";
    public static final String silverweed = "Sometimes, less competent agriculturists mistake this valuable plant for weeds and discard it.";
    public static final String jaslumine = "Blacksmiths frown down upon it, but many believe aluminium has it's uses. That's why some still try to grow this flower.";
    public static final String niccissus = "When it blooms, it's flowers reveal nuggets made of nickel, its still a mystery how this plant grows.";
    public static final String platiolus = "Platinum is a rare material, most metallurgists will go to agriculturist instead of miners to get hold of it.";
    public static final String osmonium = "An agriculturist managing to grow these flowers will find a good market of engineers.";

    //minecraft seeds
    public static final String wheat = "Wheat is a very common, but very nutritious crop.";
    public static final String pumpkin = "Most people consider pumpkins as a decorative vegetable, while others like the taste of pumpkin pies.";
    public static final String melon = "Because of it's sweet taste and high moisture content, the melon is a fruit loved by all.";

    //natura seeds
    public static final String barleyNatura = "Some people prefer the taste of barley bread over regular bread, while others claim it tastes exactly the same.";
    public static final String cottonNatura = "Cotton has a high demand in the textile industry and is therefore a very valuable asset.";

    //harvestcraft seeds
    public static final String hc_Artichoke = "The edible portion of the artichoke consists of the flower buds before the flowers come into bloom.";
    public static final String hc_Asparagus = "Asparagus has been used as a vegetable and medicine, it has a very delicate flavour.";
    public static final String hc_BambooShoot = "Bamboo shoots or bamboo sprouts are the edible shoots which are used in numerous Asian dishes and broths.";
    public static final String hc_Barley = barleyNatura;
    public static final String hc_Bean = "Beans are a summer crop that needs warm temperatures to grow.";
    public static final String hc_Beet = "The usually deep purple roots of beet are eaten either raw, grilled, boiled, or roasted.";
    public static final String hc_Bellpepper = "Most often bell peppers are green, yellow, orange, and red.";
    public static final String hc_Blackberry = "The soft fruit is popular for use in desserts, jams, seedless jelly, and sometimes wine.";
    public static final String hc_Blueberry = "Blueberries are perennial flowering plants with indigo-colored berries.";
    public static final String hc_Broccoli = "Broccoli is an edible green plant in the cabbage family, whose large flowering head is used as a vegetable.";
    public static final String hc_BrusselSproutSeed = "The Brussels sprout has long been popular in Brussels, Belgium, and may have originated there.";
    public static final String hc_Cabbage = "Cabbage is generally grown for its densely leaved heads, produced during the first year of its biennial cycle.";
    public static final String hc_CactusFruit = "The fruits typically grow spikes as well, handle with caution.";
    public static final String hc_CandleBerry = "The candleberry is a small shrub that produces small fruits.";
    public static final String hc_Cantaloupe = "Cantaloupe is normally eaten as a fresh fruit, as a salad, or as a dessert with ice cream or custard.";
    public static final String hc_Cauliflower = "Cauliflower can be roasted, boiled, fried, steamed, or eaten raw.";
    public static final String hc_Celery = "Celery is a plant variety commonly used as a vegetable, the stem is the edible part.";
    public static final String hc_ChiliPepper = "The spicy chili has many culinary uses, from dips to spicing up your meal.";
    public static final String hc_Coffee = "These beans are the base for a steaming cup of coffee to start your day, so basicly the base of your day.";
    public static final String hc_Corn = "Because of its shallow roots, corn is susceptible to droughts, intolerant of nutrient-deficient soils, and prone to be uprooted by severe winds";
    public static final String hc_Cotton = cottonNatura;
    public static final String hc_Cranbery = "Cranberry juice is usually sweetened or blended with other fruit juices to reduce its natural tartness.";
    public static final String hc_Cucumber = "The cucumber is a creeping vine that bears cylindrical fruits that are used as culinary vegetables.";
    public static final String hc_Eggplant = "The raw fruit can have a somewhat bitter taste, but becomes tender when cooked and develops a rich, complex flavor.";
    public static final String hc_Garlic = "Garlic is easy to grow and can be grown year-round in mild climates.";
    public static final String hc_Ginger = "Ginger produces clusters of white and pink flower buds that bloom into yellow flowers.";
    public static final String hc_Grape = "A grape is a fruiting berry of the deciduous woody vines and the fruits can be eaten raw or they can be used for making wine, jam, juice, jelly, grape seed extract, raisins, vinegar, and grape seed oil.";
    public static final String hc_Kiwi = "Kiwifruit can be grown in most temperate climates with adequate summer heat.";
    public static final String hc_Leek = "Rather than forming a tight bulb like the onion, the leek produces a long cylinder of bundled leaf sheaths that are generally blanched by pushing soil around them.";
    public static final String hc_Lettuce = "Generally grown as a hardy annual, lettuce is easily cultivated, although it requires relatively low temperatures to prevent it from flowering quickly.";
    public static final String hc_Mustard = "Mustard seeds generally take three to ten days to germinate if placed under the proper conditions, which include a cold atmosphere and relatively moist soil.";
    public static final String hc_Oats = "Oats are suitable for human consumption as oatmeal and rolled oats, but one of the most common uses is as livestock feed.";
    public static final String hc_Okra = "Okra plants are valued for their edible green seed pods.";
    public static final String hc_Onion = "The onion plant has a fan of hollow, bluish-green leaves and the bulb at the base of the plant begins to swell when a certain day-length is reached.";
    public static final String hc_Parsnip = "The parsnip is a root vegetable closely related to the carrot and parsley.";
    public static final String hc_Peanut = "Peanuts have many uses. They can be eaten raw, used in recipes, made into solvents and oils, medicines, textile materials, and peanut butter, as well as many other uses.";
    public static final String hc_Peas = "Peas are botanically a fruit, since they contain seeds developed from the ovary of a (pea) flower.";
    public static final String hc_Pineapple = "Pineapples are consumed fresh, cooked, juiced, and preserved, and are found in a wide array of cuisines.";
    public static final String hc_Radish = "Radishes are mostly used in salads but also appear in many European dishes.[";
    public static final String hc_Raspberry = "Raspberry plants should not be planted where potatoes, tomatoes, peppers, eggplants, or bulbs have previously been grown, without prior fumigation of the soil.";
    public static final String hc_Rhubarb = "For cooking, the stalks are often cut into small pieces and stewed (boiled in water) with added sugar.";
    public static final String hc_Rice = "The traditional method for cultivating rice is flooding the fields while, or after, setting the young seedlings.";
    public static final String hc_Rutabaga = "Some people carve out turnips in variable patterns (mostly faces) and used them as lanterns to ward off harmful spirits.";
    public static final String hc_Rye = "Rye is a grass grown extensively as a grain, a cover crop and as a forage crop.";
    public static final String hc_Scallion = "Scallion have a relatively mild onion flavour, and are used as a vegetable, either raw or cooked.";
    public static final String hc_Seaweed = "Seaweed is used in salad dressings and sauces, dietetic foods, and as a preservative in meat and fish products, dairy items and baked goods.";
    public static final String hc_Soybean = "Traditional nonfermented food uses of soybeans include soy milk, and from the latter tofu and tofu skin.";
    public static final String hc_SpiceLeaf = "Spices are distinguished from herbs, which are parts of leafy green plants used for flavoring or as a garnish.";
    public static final String hc_Strawberry = "Strawberries are popular and rewarding plants to grow in the domestic environment, be it for consumption or exhibition purposes.";
    public static final String hc_SweetPotato = "Although the leaves and shoots are also edible, the starchy tuberous roots are by far the most important product.";
    public static final String hc_Tea = "Tea can be prepared by pouring hot or boiling water over cured leaves of the tea plant";
    public static final String hc_Tomato = "The tomato is consumed in diverse ways, including raw, as an ingredient in many dishes, sauces, salads, and drinks.";
    public static final String hc_Turnip = "The turnip is a root vegetable commonly grown in temperate climates worldwide for its white, bulbous taproot.";
    public static final String hc_WhiteMushroom = "If you want to fly higher then a jetplane, these aren't the mushrooms you're looking for.";
    public static final String hc_WinterSquash = "Winter squash can be harvested whenever the fruits have turned a deep, solid color and the skin is hard.";
    public static final String hc_Zucchini = "Zucchini is usually served cooked, it can also be prepared using a variety of cooking techniques, including steamed, boiled, grilled, stuffed and baked, barbecued, fried, or incorporated in other recipes.";

    private static String getMinecraftSeedInformation(ItemSeeds seed) {
        if(seed == Items.wheat_seeds) {
            return wheat;
        }
        if(seed == Items.pumpkin_seeds) {
            return pumpkin;
        }
        if(seed == Items.melon_seeds) {
            return melon;
        }
        return null;
    }

    private static String getNaturaSeedInformation(int meta) {
        if(meta == 0) {
            return barleyNatura;
        }
        if(meta == 1) {
            return cottonNatura;
        }
        return null;
    }

    private static String getHarvestcraftSeedInformation(ItemSeeds seed) {
        if(LoadedMods.harvestcraft) {
            if (seed == com.pam.harvestcraft.ItemRegistry.cottonseedItem) {
                return hc_Cotton;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.asparagusseedItem) {
                return hc_Asparagus;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.barleyseedItem) {
                return hc_Barley;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.beanseedItem) {
                return hc_Bean;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.beetseedItem) {
                return hc_Beet;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.broccoliseedItem) {
                return hc_Broccoli;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.whitemushroomseedItem) {
                return hc_WhiteMushroom;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.cauliflowerseedItem) {
                return hc_Cauliflower;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.celeryseedItem) {
                return hc_Celery;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.cranberryseedItem) {
                return hc_Cranbery;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.garlicseedItem) {
                return hc_Garlic;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.gingerseedItem) {
                return hc_Ginger;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.leekseedItem) {
                return hc_Leek;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.lettuceseedItem) {
                return hc_Lettuce;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.oatsseedItem) {
                return hc_Oats;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.onionseedItem) {
                return hc_Onion;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.parsnipseedItem) {
                return hc_Parsnip;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.peanutseedItem) {
                return hc_Peanut;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.pineappleseedItem) {
                return hc_Pineapple;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.radishseedItem) {
                return hc_Radish;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.riceseedItem) {
                return hc_Rice;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.rutabagaseedItem) {
                return hc_Rutabaga;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.ryeseedItem) {
                return hc_Rye;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.scallionseedItem) {
                return hc_Scallion;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.soybeanseedItem) {
                return hc_Soybean;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.spiceleafseedItem) {
                return hc_SpiceLeaf;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.sweetpotatoseedItem) {
                return hc_SweetPotato;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.teaseedItem) {
                return hc_Tea;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.turnipseedItem) {
                return hc_Turnip;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.artichokeseedItem) {
                return hc_Artichoke;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.bellpepperseedItem) {
                return hc_Bellpepper;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.blackberryseedItem) {
                return hc_Blackberry;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.blueberryseedItem) {
                return hc_Blueberry;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.brusselsproutseedItem) {
                return hc_BrusselSproutSeed;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.cabbageseedItem) {
                return hc_Cabbage;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.cactusfruitseedItem) {
                return hc_CactusFruit;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.candleberryseedItem) {
                return hc_CandleBerry;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.cantaloupeseedItem) {
                return hc_Cantaloupe;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.chilipepperseedItem) {
                return hc_ChiliPepper;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.coffeeseedItem) {
                return hc_Coffee;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.cornseedItem) {
                return hc_Corn;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.cucumberseedItem) {
                return hc_Cucumber;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.eggplantseedItem) {
                return hc_Eggplant;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.grapeseedItem) {
                return hc_Grape;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.kiwiseedItem) {
                return hc_Kiwi;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.mustardseedItem) {
                return hc_Mustard;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.okraseedItem) {
                return hc_Okra;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.peasseedItem) {
                return hc_Peas;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.raspberryseedItem) {
                return hc_Raspberry;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.rhubarbseedItem) {
                return hc_Rhubarb;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.seaweedseedItem) {
                return hc_Seaweed;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.strawberryseedItem) {
                return hc_Strawberry;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.tomatoseedItem) {
                return hc_Tomato;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.wintersquashseedItem) {
                return hc_WinterSquash;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.zucchiniseedItem) {
                return hc_Zucchini;
            } else if (seed == com.pam.harvestcraft.ItemRegistry.bambooshootseedItem) {
                return hc_BambooShoot;
            }
        }
        return null;
    }

    public static String getSeedInformation(ItemStack seedStack) {
        if (seedStack.getItem() instanceof ItemSeeds) {
            if (seedStack.getItem() instanceof ItemModSeed) {
                return ((ItemModSeed) seedStack.getItem()).getInformation();
            }
            if (LoadedMods.natura && seedStack.getItem() instanceof mods.natura.items.NaturaSeeds) {
                return SeedInformation.getNaturaSeedInformation(seedStack.getItemDamage());
            }
            if (LoadedMods.harvestcraft && SeedHelper.getPlantDomain((ItemSeeds) seedStack.getItem()).equalsIgnoreCase("harvestcraft")) {
                return SeedInformation.getHarvestcraftSeedInformation((ItemSeeds) seedStack.getItem());
            }
            return SeedInformation.getMinecraftSeedInformation((ItemSeeds) seedStack.getItem());
        }
        return null;
    }
}
