package com.InfinityRaider.AgriCraft.reference;

//yes, I got the information for most harvestcraft plants from wikipedia, go ahead, call the fucking cops.

import com.InfinityRaider.AgriCraft.compatibility.ModIntegration;
import com.InfinityRaider.AgriCraft.items.ItemModSeed;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.natura.common.NContent;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import plantmegapack.PlantMegaPack;

import java.util.HashMap;

public final class SeedInformation {
    private static final HashMap<Item, String[]> informationTable = new HashMap<Item, String[]>();

    @SideOnly(Side.CLIENT)
    public static void init() {
        informationTable.put(Items.wheat_seeds, new String[]{wheat});
        informationTable.put(Items.pumpkin_seeds, new String[]{pumpkin});
        informationTable.put(Items.melon_seeds, new String[]{melon});
        if(ModIntegration.LoadedMods.natura) {
            informationTable.put(NContent.seeds, new String[]{barleyNatura,cottonNatura});
        }
        if(ModIntegration.LoadedMods.harvestcraft) {
            informationTable.put(com.pam.harvestcraft.ItemRegistry.artichokeseedItem, new String[]{hc_Artichoke});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.asparagusseedItem, new String[]{hc_Asparagus});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.bambooshootseedItem, new String[]{hc_BambooShoot});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.barleyseedItem, new String[]{hc_Barley});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.beanseedItem, new String[]{hc_Bean});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.beetseedItem, new String[]{hc_Beet});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.bellpepperseedItem, new String[]{hc_Bellpepper});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.blackberryseedItem, new String[]{hc_Blackberry});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.blueberryseedItem, new String[]{hc_Blueberry});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.broccoliseedItem, new String[]{hc_Broccoli});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.brusselsproutseedItem, new String[]{hc_BrusselsSprout});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.cabbageseedItem, new String[]{hc_Cabbage});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.cactusfruitseedItem, new String[]{hc_CactusFruit});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.candleberryseedItem, new String[]{hc_CandleBerry});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.cantaloupeseedItem, new String[]{hc_Cantaloupe});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.cauliflowerseedItem, new String[]{hc_Cauliflower});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.celeryseedItem, new String[]{hc_Celery});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.chilipepperseedItem, new String[]{hc_ChiliPepper});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.coffeeseedItem, new String[]{hc_Coffee});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.cornseedItem, new String[]{hc_Corn});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.cottonseedItem, new String[]{hc_Cotton});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.cranberryseedItem, new String[]{hc_Cranberry});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.cucumberseedItem, new String[]{hc_Cucumber});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.eggplantseedItem, new String[]{hc_Eggplant});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.garlicseedItem, new String[]{hc_Garlic});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.gingerseedItem, new String[]{hc_Ginger});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.grapeseedItem, new String[]{hc_Grape});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.kiwiseedItem, new String[]{hc_Kiwi});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.leekseedItem, new String[]{hc_Leek});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.lettuceseedItem, new String[]{hc_Lettuce});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.mustardseedItem, new String[]{hc_Mustard});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.oatsseedItem, new String[]{hc_Oats});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.okraseedItem, new String[]{hc_Okra});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.onionseedItem, new String[]{hc_Onion});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.parsnipseedItem, new String[]{hc_Parsnip});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.peanutseedItem, new String[]{hc_Peanut});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.peasseedItem, new String[]{hc_Peas});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.pineappleseedItem, new String[]{hc_Pineapple});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.radishseedItem, new String[]{hc_Radish});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.raspberryseedItem, new String[]{hc_Raspberry});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.rhubarbseedItem, new String[]{hc_Rhubarb});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.riceseedItem, new String[]{hc_Rice});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.rutabagaseedItem, new String[]{hc_Rutabaga});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.ryeseedItem, new String[]{hc_Rye});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.scallionseedItem, new String[]{hc_Scallion});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.seaweedseedItem, new String[]{hc_Seaweed});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.soybeanseedItem, new String[]{hc_Soybean});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.spiceleafseedItem, new String[]{hc_SpiceLeaf});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.strawberryseedItem, new String[]{hc_Strawberry});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.sweetpotatoseedItem, new String[]{hc_SweetPotato});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.teaseedItem, new String[]{hc_Tea});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.tomatoseedItem, new String[]{hc_Tomato});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.turnipseedItem, new String[]{hc_Turnip});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.whitemushroomseedItem, new String[]{hc_WhiteMushroom});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.wintersquashseedItem, new String[]{hc_WinterSquash});
            informationTable.put(com.pam.harvestcraft.ItemRegistry.zucchiniseedItem, new String[]{hc_Zucchini});
        }
        if(ModIntegration.LoadedMods.plantMegaPack) {
            informationTable.put(PlantMegaPack.items.seedBeet, new String[]{pmp_Onion});
            informationTable.put(PlantMegaPack.items.seedSpinach, new String[]{pmp_Spinach});
            informationTable.put(PlantMegaPack.items.seedCelery, new String[]{pmp_Celery});
            informationTable.put(PlantMegaPack.items.seedLettuce, new String[]{pmp_lettuce});
            informationTable.put(PlantMegaPack.items.seedBellPepperYellow, new String[]{pmp_Bellpepper});
            informationTable.put(PlantMegaPack.items.seedCorn, new String[]{pmp_Corn});
            informationTable.put(PlantMegaPack.items.seedCucumber, new String[]{pmp_Cucumber});
            informationTable.put(PlantMegaPack.items.seedTomato, new String[]{pmp_Tomato});
            informationTable.put(PlantMegaPack.items.seedBeet, new String[]{pmp_Beet});
        }
    }

    //retrieve seed information
    public static String getSeedInformation(ItemStack seedStack) {
        String output = "";
        if (seedStack.getItem() instanceof ItemSeeds) {
            if (seedStack.getItem() instanceof ItemModSeed) {
                output = ((ItemModSeed) seedStack.getItem()).getInformation();
            }
            else {
                String[] info = informationTable.get(seedStack.getItem());
                if(info!=null && info.length>seedStack.getItemDamage()) {
                    output = info[seedStack.getItemDamage()];
                }
            }
        }
        return StatCollector.translateToLocal(output);
    }

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
}
