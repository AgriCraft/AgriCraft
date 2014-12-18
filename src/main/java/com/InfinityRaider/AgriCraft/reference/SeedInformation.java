package com.InfinityRaider.AgriCraft.reference;

//yes, I got the information for most harvestcraft plants from wikipedia, go ahead, call the fucking cops.

import com.InfinityRaider.AgriCraft.compatibility.ModIntegration;
import com.InfinityRaider.AgriCraft.items.ItemModSeed;
import com.InfinityRaider.AgriCraft.utility.SeedHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import plantmegapack.PlantMegaPack;

public final class SeedInformation {
//agricraft seeds
    public static final String potato = "agricraft_journal.potato";
    public static final String carrot = "agricraft_journal.carrot";
    public static final String sugarcane = "agricraft_journal.sugarcane";
    public static final String dandelion = "agricraft_journal.dandelion";
    public static final String poppy = "agricraft_journal.poppy";
    public static final String orchid = "agricraft_journal.orchid";
    public static final String allium = "agricraft_journal.allium";
    public static final String tulipPink = "agricraft_journal.tulipPink";
    public static final String tulipOrange = "agricraft_journal.tulipOrange";
    public static final String tulipRed = "agricraft_journal.tulipRed";
    public static final String tulipWhite = "agricraft_journal.tulipWhite";
    public static final String daisy = "agricraft_journal.daisy";
    public static final String diamahlia = "agricraft_journal.diamahlia";
    public static final String aurigold = "agricraft_journal.aurigold";
    public static final String ferranium = "agricraft_journal.ferranium";
    public static final String lapender = "agricraft_journal.lapender";
    public static final String emeryllis = "agricraft_journal.emeryllis";
    public static final String redstodendron = "agricraft_journal.redstodendron";
    public static final String cuprosia = "agricraft_journal.cuprosia";
    public static final String petinia = "agricraft_journal.petinia";
    public static final String plombean = "agricraft_journal.plombean";
    public static final String silverweed = "agricraft_journal.silverweed";
    public static final String jaslumine = "agricraft_journal.jaslumine";
    public static final String niccissus = "agricraft_journal.niccissus";
    public static final String platiolus = "agricraft_journal.platiolus";
    public static final String osmonium = "agricraft_journal.osmonium";

    //minecraft seeds
    public static final String wheat = "agricraft_journal.wheat";
    public static final String pumpkin = "agricraft_journal.pumpkin";
    public static final String melon = "agricraft_journal.melon";

    //natura seeds
    public static final String barleyNatura = "agricraft_journal.barleyNatura";
    public static final String cottonNatura = "agricraft_journal.cottonNatura";

    //harvestcraft seeds
    public static final String hc_Artichoke = "agricraft_journal.hc_Artichoke";
    public static final String hc_Asparagus = "agricraft_journal.hc_Asparagus";
    public static final String hc_BambooShoot = "agricraft_journal.hc_BambooShoot";
    public static final String hc_Barley = barleyNatura;
    public static final String hc_Bean = "agricraft_journal.hc_Bean";
    public static final String hc_Beet = "agricraft_journal.hc_Beet";
    public static final String hc_Bellpepper = "agricraft_journal.hc_Bellpepper";
    public static final String hc_Blackberry = "agricraft_journal.hc_Blackberry";
    public static final String hc_Blueberry = "agricraft_journal.hc_Blueberry";
    public static final String hc_Broccoli = "agricraft_journal.hc_Broccoli";
    public static final String hc_BrusselsSprout = "agricraft_journal.hc_BrusselsSprout";
    public static final String hc_Cabbage = "agricraft_journal.hc_Cabbage";
    public static final String hc_CactusFruit = "agricraft_journal.hc_CactusFruit";
    public static final String hc_CandleBerry = "agricraft_journal.hc_CandleBerry";
    public static final String hc_Cantaloupe = "agricraft_journal.hc_Cantaloupe";
    public static final String hc_Cauliflower = "agricraft_journal.hc_Cauliflower";
    public static final String hc_Celery = "agricraft_journal.hc_Celery";
    public static final String hc_ChiliPepper = "agricraft_journal.hc_ChiliPepper";
    public static final String hc_Coffee = "agricraft_journal.hc_Coffee";
    public static final String hc_Corn = "agricraft_journal.hc_Corn";
    public static final String hc_Cotton = cottonNatura;
    public static final String hc_Cranberry = "agricraft_journal.hc_Cranberry";
    public static final String hc_Cucumber = "agricraft_journal.hc_Cucumber";
    public static final String hc_Eggplant = "agricraft_journal.hc_Eggplant";
    public static final String hc_Garlic = "agricraft_journal.hc_Garlic";
    public static final String hc_Ginger = "agricraft_journal.hc_Ginger";
    public static final String hc_Grape = "agricraft_journal.hc_Grape";
    public static final String hc_Kiwi = "agricraft_journal.hc_Kiwi";
    public static final String hc_Leek = "agricraft_journal.hc_Leek";
    public static final String hc_Lettuce = "agricraft_journal.hc_Lettuce";
    public static final String hc_Mustard = "agricraft_journal.hc_Mustard";
    public static final String hc_Oats = "agricraft_journal.hc_Oats";
    public static final String hc_Okra = "agricraft_journal.hc_Okra";
    public static final String hc_Onion = "agricraft_journal.hc_Onion";
    public static final String hc_Parsnip = "agricraft_journal.hc_Parsnip";
    public static final String hc_Peanut = "agricraft_journal.hc_Peanut";
    public static final String hc_Peas = "agricraft_journal.hc_Peas";
    public static final String hc_Pineapple = "agricraft_journal.hc_Pineapple";
    public static final String hc_Radish = "agricraft_journal.hc_Radish";
    public static final String hc_Raspberry = "agricraft_journal.hc_Raspberry";
    public static final String hc_Rhubarb = "agricraft_journal.hc_Rhubarb";
    public static final String hc_Rice = "agricraft_journal.hc_Rice";
    public static final String hc_Rutabaga = "agricraft_journal.hc_Rutabaga";
    public static final String hc_Rye = "agricraft_journal.hc_Rye";
    public static final String hc_Scallion = "agricraft_journal.hc_Scallion";
    public static final String hc_Seaweed = "agricraft_journal.hc_Seaweed";
    public static final String hc_Soybean = "agricraft_journal.hc_Soybean";
    public static final String hc_SpiceLeaf = "agricraft_journal.hc_SpiceLeaf";
    public static final String hc_Strawberry = "agricraft_journal.hc_Strawberry";
    public static final String hc_SweetPotato = "agricraft_journal.hc_SweetPotato";
    public static final String hc_Tea = "agricraft_journal.hc_Tea";
    public static final String hc_Tomato = "agricraft_journal.hc_Tomato";
    public static final String hc_Turnip = "agricraft_journal.hc_Turnip";
    public static final String hc_WhiteMushroom = "agricraft_journal.hc_WhiteMushroom";
    public static final String hc_WinterSquash = "agricraft_journal.hc_WinterSquash";
    public static final String hc_Zucchini = "agricraft_journal.hc_Zucchini";

    //plant mega pack seeds
    public static final String pmp_Onion = hc_Onion;
    public static final String pmp_Spinach = "agricraft_journal.pmp_Spinach";
    public static final String pmp_Celery = hc_Celery;
    public static final String pmp_lettuce = hc_Lettuce;
    public static final String pmp_Bellpepper = hc_Bellpepper;
    public static final String pmp_Corn = hc_Corn;
    public static final String pmp_Cucumber = hc_Cucumber;
    public static final String pmp_Tomato = hc_Tomato;
    public static final String pmp_Beet = hc_Beet;

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
        return "";
    }

    private static String getNaturaSeedInformation(int meta) {
        if(meta == 0) {
            return barleyNatura;
        }
        if(meta == 1) {
            return cottonNatura;
        }
        return "";
    }

    private static String getHarvestcraftSeedInformation(ItemSeeds seed) {
        if(ModIntegration.LoadedMods.harvestcraft) {
            if (seed==com.pam.harvestcraft.ItemRegistry.cottonseedItem) {
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
                return hc_Cranberry;
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
                return hc_BrusselsSprout;
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
        return "";
    }

    private static String getPlantMegaPackSeedInformation(ItemSeeds seed) {
        if(ModIntegration.LoadedMods.plantMegaPack) {
            if (seed == PlantMegaPack.items.seedBeet) {
                return pmp_Beet;
            } else if (seed == PlantMegaPack.items.seedBellPepperYellow) {
                return pmp_Bellpepper;
            } else if (seed == PlantMegaPack.items.seedCelery) {
                return pmp_Celery;
            } else if (seed == PlantMegaPack.items.seedCorn) {
                return pmp_Corn;
            } else if (seed == PlantMegaPack.items.seedCucumber) {
                return pmp_Cucumber;
            } else if (seed == PlantMegaPack.items.seedLettuce) {
                return pmp_lettuce;
            } else if (seed == PlantMegaPack.items.seedOnion) {
                return pmp_Onion;
            } else if (seed == PlantMegaPack.items.seedSpinach) {
                return pmp_Spinach;
            } else if (seed == PlantMegaPack.items.seedTomato) {
                return pmp_Tomato;
            }
        }
        return "";
    }

    public static String getSeedInformation(ItemStack seedStack) {
        String output = "";
        if (seedStack.getItem() instanceof ItemSeeds) {
            if (seedStack.getItem() instanceof ItemModSeed) {
                output = ((ItemModSeed) seedStack.getItem()).getInformation();
            }
            else if (ModIntegration.LoadedMods.natura && seedStack.getItem() instanceof mods.natura.items.NaturaSeeds) {
                output = SeedInformation.getNaturaSeedInformation(seedStack.getItemDamage());
            }
            else if (ModIntegration.LoadedMods.harvestcraft && SeedHelper.getPlantDomain((ItemSeeds) seedStack.getItem()).equalsIgnoreCase("harvestcraft")) {
                output = SeedInformation.getHarvestcraftSeedInformation((ItemSeeds) seedStack.getItem());
            }
            else if (ModIntegration.LoadedMods.plantMegaPack && SeedHelper.getPlantDomain((ItemSeeds) seedStack.getItem()).equalsIgnoreCase("plantmegapack")) {
                output = SeedInformation.getPlantMegaPackSeedInformation((ItemSeeds) seedStack.getItem());
            }
            else {
                output = SeedInformation.getMinecraftSeedInformation((ItemSeeds) seedStack.getItem());
            }
        }
        return StatCollector.translateToLocal(output);
    }
}
