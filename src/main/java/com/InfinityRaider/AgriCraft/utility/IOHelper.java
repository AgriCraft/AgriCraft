package com.InfinityRaider.AgriCraft.utility;

import com.InfinityRaider.AgriCraft.compatibility.LoadedMods;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.reference.Reference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;

//class containing the default mutations for each supported mod
public abstract class IOHelper {

    //get the mutations file contents
    public static String getDefaultMutations() {
        String data = mutationInstructions;                                     //mutationInstructions
        data = data + '\n' + minecraftMutations;                                 //minecraft mutations
        if(ConfigurationHandler.resourcePlants) {
            data = data + '\n' + agricraftMutations;                             //agricraft mutations
            if(OreDictHelper.oreCopper!=null) {
                data = data + '\n' + copperMutation;
            }
            if(OreDictHelper.oreTin!=null) {
                data = data + '\n' + tinMutation;
            }
            if(OreDictHelper.oreLead!=null) {
                data = data + '\n' + leadMutation;
            }
            if(OreDictHelper.oreSilver!=null) {
                data = data + '\n' + silverMutation;
            }
            if(OreDictHelper.oreAluminum!=null) {
                data = data + '\n' + aluminumMutation;
            }
            if(OreDictHelper.oreNickel!=null) {
                data = data + '\n' + nickelMutation;
            }
            if(OreDictHelper.orePlatinum!=null) {
                data = data + '\n' + platinumMutation;
            }
            if(OreDictHelper.oreOsmium!=null) {
                data = data + '\n' + osmiumMutation;
            }
        }
        if(ConfigurationHandler.integration_Nat && ConfigurationHandler.integration_HC && LoadedMods.harvestcraft && LoadedMods.natura)  {
            data = data + '\n' + harvestcraftMutations + '\n' + barleyNaturaMutations;      //harvestcraft with natura barley
        }
        else {
            if(ConfigurationHandler.integration_HC && LoadedMods.harvestcraft) {
                data = data + '\n' + harvestcraftMutations + '\n' + barleyHarvestCraftMutations;
            }
            if(ConfigurationHandler.integration_Nat && LoadedMods.natura) {
                data = data + '\n' + naturaMutations;
            }
        }
        return data;
    }

    //get the custom crop file contents
    public static String getCustomCropInstructions() {
        return customCropInstructions;
    }

    //get the grass drops file contents
    public static String getGrassDrops() {
        return grassDropInstructions;
    }

    //turns the raw data string into an array (each array element is a line from the string)
    public static String[] getLinesArrayFromData(String input) {
        int count = 0;
        for(int i=0;i<input.length();i++) {
            if(input.charAt(i)=='\n') {
                count++;
            }
            else if (input.charAt(i)=='#') {
                count--;
            }
        }
        count = count>=0?count+1:count;  //for the last line
        count = count<0?0:count;        //make sure it can't be negative
        String[] data = new String[count];
        if(input.length()>0) {
            int start = 0;
            int stop;
            for (int i = 0; i < data.length; i++) {
                stop = input.indexOf('\n', start);
                while (start<input.length() && input.charAt(start) == '#' && start<input.length()) {
                    start = stop + 1;
                    stop = input.indexOf('\n', start);
                }
                data[i] = i == data.length - 1 ? input.substring(start) : input.substring(start, stop);
                start = stop + 1;
            }
        }
        return data;

    }

    public static String[] getCropData(String input) {
        String[] output = new String[8];
        int start = 0;
        int stop;
        for(int i=0;i<output.length;i++) {
            if(output[i]==null || output[i].equals("")) {
                stop = input.indexOf(',', start);
                output[i] = i == output.length - 1 ? input.substring(start) : input.substring(start, stop);
                start = stop + 1;
                if (i == 1 || i == 3) {
                    if (output[i].indexOf(':', output[i].indexOf(':') + 1) >= 0) {
                        output[i + 1] = output[i].substring((output[i].indexOf(':', output[i].indexOf(':') + 1) + 1));
                        output[i] = output[i].substring(0, output[i].indexOf(':', output[i].indexOf(':') + 1));
                    }
                    else {
                        output[i+1] = "0";
                    }
                }
            }
            LogHelper.debug("CropData["+i+"]: "+output[i]);
        }
        return output;
    }

    //corrects the seed names
    public static String correctSeedName(String input) {
        String domain = input.substring(0,input.indexOf(':'));
        String name = input.substring(input.indexOf(':')+1);
        if(domain.equalsIgnoreCase("minecraft")) {
            if(name.indexOf(':')>=0) {
                name = name.substring(0,name.indexOf(':'));
            }
            if(name.equalsIgnoreCase("wheat") || name.equalsIgnoreCase("melon") || name.equalsIgnoreCase("pumpkin")) {
                name = name + "_seeds";
                domain = "minecraft";
            }
            else if(name.equalsIgnoreCase("wheat_seeds") || name.equalsIgnoreCase("melon_seeds")|| name.equalsIgnoreCase("pumpkin_seeds") || name.equalsIgnoreCase("nether_wart")) {
                domain = "minecraft";
            }
            else {
                if(name.length()<4 || !name.substring(0,4).equals(Names.seed)) {
                    if (name.substring(0, 5).equalsIgnoreCase("tulip")) {
                        name = "Tulip" + Character.toUpperCase(name.charAt(5)) + name.substring(6);
                    }
                }
                domain =  Reference.MOD_ID;
            }
        }
        if(domain.equalsIgnoreCase( Reference.MOD_ID)) {
            domain = Reference.MOD_ID;
            if(name.indexOf(':')>=0) {
                name = name.substring(0,name.indexOf(':'));
            }
            if(name.length()<4 || !name.substring(0,4).equals(Names.seed)) {
                name = "seed" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
            }
        }
        else if(domain.equalsIgnoreCase("harvestcraft")) {
            domain = "harvestcraft";
            if(name.indexOf(':')>=0) {
                name = name.substring(0,name.indexOf(':'));
            }
            if(name.length()<8 || !name.substring(name.length()-8).equals("seedItem")) {
                name = name + "seedItem";
            }
        }
        else if(domain.equalsIgnoreCase("natura")) {
            domain = "Natura";
            int meta = 0;
            if (name.indexOf(':') >= 0) {
                meta = Integer.parseInt(name.substring(name.indexOf(':') + 1));
                name = name.substring(0, name.indexOf(':'));
            }
            if (name.equalsIgnoreCase("cotton")) {
                meta = 1;
            }
            name = "barley.seed:" + meta;
        }
       return domain + ':' + name;
    }

    //gets an item stack with the correct meta
    public static ItemStack getSeedStack(String input) {
        String domain = input.substring(0,input.indexOf(':'));
        String name = input.substring(input.indexOf(':')+1);
        int meta = 0;
        if(name.indexOf(':')>=0) {
            meta = Integer.parseInt(name.substring(name.indexOf(':')+1));
            name = name.substring(0, name.indexOf(':'));
        }
        return new ItemStack((ItemSeeds) Item.itemRegistry.getObject(domain+':'+name),1,meta);
    }

    private static final String grassDropInstructions =
            "";


    private static final String customCropInstructions =
            "#Define custom crops here: <name>,<fruit:fruitmeta>,<baseblock:baseblockmeta>,<tier>,<rendermethod>,<information>\n" +
            "# - name:         is the name of the crop you want, for example: claysanthemum\n" +
            "# - fruit:        the fruit you want the crop to produce, for example: minecraft:clay:0 (metadata is optional, you can get this from NEI)\n" +
            "# - baseblock:    this is the block that has to be underneath for the plant to grow, for example: (this can also be gotten from NEI, if you don't want to specify, type null)\n" +
            "# - tier:         from 1 to 5, the higher, the slower the crop will grow\n" +
            "# - rendermethod: put 1 to render like a flower (in an X-pattern), put 6 to render like wheat (hashtag-pattern)\n" +
            "# - information:  put a short description (in the same line) of the crop. This will appear in the journal<n" +
            "#you will need to make a texture pack and add textures for the crops in agricraft/blocks with the name cropName1, cropName2, cropName3, cropName4\n" +
            "#where name is the name you specified here, there have to be 4 textures, texture 4 is the mature one\n" +
            "#for the seed texture, put a texture named seedName in the agricraft/items of your resourcepack\n" +
            "#Example: claysanthemum,minecraft:clay,minecraft:hardened_clay,2,6,a crop that extracts clay from the soil to grow clay buds.\n" +
            "#this will need textures cropClaysanthemum1, cropClaysanthemum2, Claysanthemum3, Claysanthemum4 in the agricraft/textures/blocks folder and seedClaysanthemum in the agricraft/textures/items folder of your resourcepack.\n" +
            "#Note the capitalization, always put a capital in the texture name. Also, this crop will not grow unless it has a hardened clay block underneath the block of farmland it's on";

    private static final String mutationInstructions =
            "#Define mutations here: <mutation>=<parent1>+<parent2>\n" +
            "#To specify a crop, write <mod>:<cropname>:<meta>, all in lowercase (meta is optional)\n" +
            "#You can these values from NEI, the data you put here will be corrected up to a certain level, but you should always try to use the values NEI gives you\n" +
            "#For example if you write harvestcraft:tomato, it will be corrected to harvestcraft=tomatoseedItem, or if you write minecraft:dandelion, it will become AgriCraft:seedDandelion";

    private static final String harvestcraftMutations =
            "harvestcraft:artichokeseedItem=harvestcraft:asparagusseedItem+harvestcraft:lettuceseedItem\n" +
            "harvestcraft:asparagusseedItem=harvestcraft:scallionseedItem+harvestcraft:cornseedItem\n" +
            "harvestcraft:bambooshootseedItem=harvestcraft:cornseedItem+harvestcraft:riceseedItem\n" +
            "harvestcraft:beanseedItem=minecraft:pumpkin_seeds+AgriCraft:seedPotato\n" +
            "harvestcraft:beetseedItem=harvestcraft:radishseedItem+AgriCraft:seedCarrot\n" +
            "harvestcraft:bellpepperseedItem=harvestcraft:chilipepperseedItem+harvestcraft:spiceleafseedItem\n" +
            "harvestcraft:blackberryseedItem=harvestcraft:strawberryseedItem+harvestcraft:blueberryseedItem\n" +
            "harvestcraft:blueberryseedItem=harvestcraft:strawberryseedItem+AgriCraft:seedOrchid\n" +
            "harvestcraft:broccoliseedItem=harvestcraft:lettuceseedItem+AgriCraft:seedDaisy\n" +
            "harvestcraft:brusselsproutseedItem=harvestcraft:cabbageseedItem+harvestcraft:peasseedItem\n" +
            "harvestcraft:cabbageseedItem=harvestcraft:broccoliseedItem+harvestcraft:lettuceseedItem\n" +
            "harvestcraft:cactusfruitseedItem=harvestcraft:kiwiseedItem+harvestcraft:bambooshootseedItem\n" +
            "harvestcraft:candleberryseedItem=harvestcraft:cactusfruitseedItem+harvestcraft:grapeseedItem\n" +
            "harvestcraft:cantaloupeseedItem=minecraft:melon_seeds+harvestcraft:strawberryseedItem\n" +
            "harvestcraft:cauliflowerseedItem=harvestcraft:cabbageseedItem+harvestcraft:lettuceseedItem\n" +
            "harvestcraft:celeryseedItem=minecraft:wheat_seeds+AgriCraft:seedAllium\n" +
            "harvestcraft:chilipepperseedItem=harvestcraft:tomatoseedItem+harvestcraft:onionseedItem\n" +
            "harvestcraft:coffeeseedItem=harvestcraft:beanseedItem+AgriCraft:seedSugarcane\n" +
            "harvestcraft:cranberryseedItem=harvestcraft:blueberryseedItem+harvestcraft:grapeseedItem\n" +
            "harvestcraft:cucumberseedItem=harvestcraft:peasseedItem+harvestcraft:okraseedItem\n" +
            "harvestcraft:eggplantseedItem=harvestcraft:zucchiniseedItem+harvestcraft:tomatoseedItem\n" +
            "harvestcraft:garlicseedItem=harvestcraft:onionseedItem+harvestcraft:gingerseedItem\n" +
            "harvestcraft:gingerseedItem=harvestcraft:mustardseedItem+harvestcraft:peanutseedItem\n" +
            "harvestcraft:grapeseedItem=harvestcraft:blueberryseedItem+harvestcraft:cantaloupeseedItem\n" +
            "harvestcraft:kiwiseedItem=harvestcraft:cantaloupeseedItem+harvestcraft:strawberryseedItem\n" +
            "harvestcraft:leekseedItem=harvestcraft:scallionseedItem+harvestcraft:celeryseedItem\n" +
            "harvestcraft:lettuceseedItem=AgriCraft:seedDaisy+harvestcraft:celeryseedItem\n" +
            "harvestcraft:mustardseedItem=harvestcraft:chilipepperseedItem+harvestcraft:beanseedItem\n" +
            "harvestcraft:oatsseedItem=harvestcraft:cornseedItem+harvestcraft:riceseedItem\n" +
            "harvestcraft:okraseedItem=harvestcraft:beanseedItem+harvestcraft:leekseedItem\n" +
            "harvestcraft:onionseedItem=harvestcraft:celeryseedItem+harvestcraft:brusselsproutseedItem\n" +
            "harvestcraft:parsnipseedItem=AgriCraft:seedCarrot+harvestcraft:beetseedItem\n" +
            "harvestcraft:peanutseedItem=harvestcraft:peasseedItem+harvestcraft:bambooshootseedItem\n" +
            "harvestcraft:peasseedItem=harvestcraft:soybeanseedItem+harvestcraft:okraseedItem\n" +
            "harvestcraft:pineappleseedItem=harvestcraft:bambooshootseedItem+harvestcraft:cantaloupeseedItem\n" +
            "harvestcraft:radishseedItem=harvestcraft:tomatoseedItem+harvestcraft:brusselsproutseedItem\n" +
            "harvestcraft:raspberryseedItem=harvestcraft:strawberryseedItem+AgriCraft:seedTulipRed\n" +
            "harvestcraft:rhubarbseedItem=AgriCraft:seedSugarcane+harvestcraft:lettuceseedItem\n" +
            "harvestcraft:riceseedItem=harvestcraft:ryeseedItem+AgriCraft:seedSugarcane\n" +
            "harvestcraft:rutabagaseedItem=harvestcraft:beetseedItem+harvestcraft:turnipseedItem\n" +
            "harvestcraft:scallionseedItem=AgriCraft:seedCarrot+AgriCraft:seedSugarcane\n" +
            "harvestcraft:seaweedseedItem=harvestcraft:lettuceseedItem+harvestcraft:celeryseedItem\n" +
            "harvestcraft:soybeanseedItem=harvestcraft:beanseedItem+harvestcraft:riceseedItem\n" +
            "harvestcraft:spiceleafseedItem=harvestcraft:teaseedItem+harvestcraft:chilipepperseedItem\n" +
            "harvestcraft:sweetpotatoseedItem=AgriCraft:seedPotato+AgriCraft:seedSugarcane\n" +
            "harvestcraft:teaseedItem=harvestcraft:seaweedseedItem+AgriCraft:Daisy\n" +
            "harvestcraft:tomatoseedItem=harvestcraft:sweetpotatoseedItem+AgriCraft:seedCarrot\n" +
            "harvestcraft:turnipseedItem=harvestcraft:parsnipseedItem+harvestcraft:radishseedItem\n" +
            "harvestcraft:wintersquashseedItem=minecraft:pumpkin_seeds+harvestcraft:zucchiniseedItem\n" +
            "harvestcraft:zucchiniseedItem=minecraft:pumpkin_seeds+harvestcraft:cucumberseedItem";

    private static final String minecraftMutations =
            "AgriCraft:seedSugarcane=minecraft:wheat_seeds+AgriCraft:seedCarrot\n" +
            "minecraft:pumpkin_seeds=AgriCraft:seedPotato+AgriCraft:seedCarrot\n" +
            "minecraft:melon_seeds=AgriCraft:carrot+minecraft:pumpkin_seeds\n" +
            "AgriCraft:seedPoppy=AgriCraft:seedSugarcane+minecraft:pumpkin_seeds\n" +
            "AgriCraft:seedDandelion=AgriCraft:seedSugarcane+minecraft:melon_seeds\n" +
            "AgriCraft:seedOrchid=AgriCraft:seedPoppy+AgriCraft:seedDandelion\n" +
            "AgriCraft:seedAllium=AgriCraft:seedPoppy+AgriCraft:seedOrchid\n" +
            "AgriCraft:seedTulipRed=AgriCraft:seedPoppy+AgriCraft:seedAllium\n" +
            "AgriCraft:seedTulipOrange=AgriCraft:seedDaisy+AgriCraft:seedOrchid\n" +
            "AgriCraft:seedTulipWhite=AgriCraft:seedDaisy+AgriCraft:seedDandelion\n" +
            "AgriCraft:seedTulipPink=AgriCraft:seedAllium+AgriCraft:seedDandelion\n" +
            "AgriCraft:seedDaisy=AgriCraft:seedDandelion+AgriCraft:seedOrchid";

    private static final String agricraftMutations =
            "AgriCraft:seedRedstodendron=AgriCraft:seedTulipRed+AgriCraft:seedDaisy\n" +
            "AgriCraft:seedLapender=AgriCraft:seedTulipPink+AgriCraft:seedOrchid\n" +
            "AgriCraft:seedFerranium=AgriCraft:seedLapender+AgriCraft:seedTulipWhite\n" +
            "AgriCraft:seedAurigold=AgriCraft:seedRedstodendron+AgriCraft:seedTulipOrange\n" +
            "AgriCraft:seedDiamahlia=AgriCraft:seedAurigold+AgriCraft:seedLapender\n" +
            "AgriCraft:seedEmeryllis=AgriCraft:seedFerranium+AgriCraft:seedRedstodendron";

    private static final String barleyHarvestCraftMutations =
            "harvestcraft:barleyseedItem=minecraft:wheat_seeds+AgriCraft:seedSugarcane\n" +
            "harvestcraft:cornseedItem=harvestcraft:barleyseedItem+harvestcraft:ryeseedItem\n" +
            "harvestcraft:ryeseedItem=harvestcraft:barleyseedItem+minecraft:wheat_seeds\n" +
            "harvestcraft:cottonseedItem=harvestcraft:barleyseedItem+AgriCraft:seedDaisy";

    private static final String naturaMutations =
            "Natura:barley.seed:0=minecraft:wheat_seeds+AgriCraft:seedSugarcane\n" +
            "Natura:barley.seed:1=Natura:barley.seed:0+AgriCraft:seedDaisy";

    private static final String barleyNaturaMutations =
            "Natura:barley.seed:0=minecraft:wheat_seeds+AgriCraft:seedSugarcane\n" +
            "harvestcraft:cornseedItem=Natura:barley.seed:0+harvestcraft:ryeseedItem\n" +
            "harvestcraft:ryeseedItem=Natura:barley+minecraft:wheat_seeds\n" +
            "Natura:barley.seed:1=Natura:barley.seed:0+AgriCraft:seedDaisy";

    private static final String copperMutation =
            "AgriCraft:seedCuprosia=AgriCraft:seedRedstodendron+AgriCraft:seedTulipOrange";

    private static final String tinMutation =
            "AgriCraft:seedPetinia=AgriCraft:seedLapender+AgriCraft:seedDaisy";

    private static final String leadMutation =
            "AgriCraft:seedPlombean=Agricraft:seedFerranium+AgriCraft:seedPotato";

    private static final String silverMutation =
            "AgriCraft:seedSilverweed=AgriCraft:seedAurigold+AgriCraft:seedAllium";

    private static final String aluminumMutation =
            "AgriCraft:seedJaslumine=AgriCraft:seedFerranium+minecraft:wheat_seeds";

    private static final String nickelMutation =
            "AgriCraft:seedNiccissus=AgriCraft:seedAurigold+AgriCraft:seedDandelion";

    private static final String platinumMutation =
            "AgriCraft:seedPlatiolus=AgriCraft:seedDiamahlia+AgriCraft:seedFerranium";

    private static final String osmiumMutation =
            "AgriCraft:seedOsmonium=AgriCraft:seedFerranium+AgriCraft:seedOrchid";

}
