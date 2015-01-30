package com.InfinityRaider.AgriCraft.utility;

import com.InfinityRaider.AgriCraft.compatibility.ModIntegration;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.io.*;
import java.util.ArrayList;

//class containing the default mutations for each supported mod
public abstract class IOHelper {
    //reads and writes text files
    public static String readOrWrite(String directory, String fileName, String defaultData) {
        return readOrWrite(directory, fileName, defaultData, false);
    }

    //reads and writes text files
    public static String readOrWrite(String directory, String fileName, String defaultData, boolean reset) {
        File file = new File(directory, fileName+".txt");
        if(file.exists() && !file.isDirectory() && !reset) {
            try {
                FileInputStream inputStream = new FileInputStream(file);
                byte[] input = new byte[(int) file.length()];
                try {
                    inputStream.read(input);
                    inputStream.close();
                    return new String(input, "UTF-8");
                } catch (IOException e) {
                    LogHelper.info("Caught IOException when reading "+fileName+".txt");
                }
            } catch(FileNotFoundException e) {
                LogHelper.info("Caught IOException when reading "+fileName+".txt");
            }
        }
        else {
            BufferedWriter writer;
            try {
                writer = new BufferedWriter(new FileWriter(file));
                try {
                    writer.write(defaultData);
                    writer.close();
                    return defaultData;
                }
                catch(IOException e) {
                    LogHelper.info("Caught IOException when writing "+fileName+".txt");
                }
            }
            catch(IOException e) {
                LogHelper.info("Caught IOException when writing "+fileName+".txt");
            }
        }
        return null;
    }

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
        if(ConfigurationHandler.integration_Botania) {
            data = data + '\n' + botaniaMutations;
        }
        if(ConfigurationHandler.integration_Nat && ConfigurationHandler.integration_HC && ModIntegration.LoadedMods.harvestcraft && ModIntegration.LoadedMods.natura)  {
            data = data + '\n' + harvestcraftMutations + '\n' + barleyNaturaMutations;      //harvestcraft with natura barley
        }
        else {
            if(ConfigurationHandler.integration_HC && ModIntegration.LoadedMods.harvestcraft) {
                data = data + '\n' + harvestcraftMutations + '\n' + barleyHarvestCraftMutations;
            }
            if(ConfigurationHandler.integration_Nat && ModIntegration.LoadedMods.natura) {
                data = data + '\n' + naturaMutations;
            }
        }
        if(ConfigurationHandler.integration_WeeeFlowers && ModIntegration.LoadedMods.weeeFlowers) {
            data = data +'\n' + weeeFlowersMutations;
        }
        if(ConfigurationHandler.integration_PlantMegaPack && ModIntegration.LoadedMods.plantMegaPack) {
            data = data + '\n' + plantMegaPackMutations;
        }
        if(ConfigurationHandler.integration_Chococraft && ModIntegration.LoadedMods.chococraft) {
            if(ConfigurationHandler.integration_HC && ModIntegration.LoadedMods.harvestcraft) {
                data = data + '\n' + chococraft_harvestcraftMutations;
            }
            else {
                data = data + '\n' + chococraftMutations;
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
        return grassDropInstructions + "\n" + "AgriCraft:seedCarrot,10" + "\n" + "AgriCraft:seedPotato,10";
    }

    //mutation chances overrides file contents
    public static String getSpreadChancesOverridesInstructions() {
        return spreadChancesOverridesInstructions;
    }

    //seed tier overrides file contents
    public static String getSeedTierOverridesInstructions() {
        return seedTierOverridesInstructions;
    }

    //seed black list
    public static String getSeedBlackListInstructions() {
        return seedBlackListInstructions;
    }

    public static String getSoilwhitelistData() {
        String output = soilWhitelistInstructions;
        if(ModIntegration.LoadedMods.forestry) {
            output = output +"\n" + "Forestry:soil:0";
        }
        return soilWhitelistInstructions;
    }

    //turns the raw data string into an array (each array element is a line from the string)
    public static String[] getLinesArrayFromData(String input) {
        int count = 0;
        String unprocessed = input;
        for (int i=0;i<unprocessed.length();i++) {
            if (unprocessed.charAt(i) == '\n') {
                count++;
            }
        }
        ArrayList<String> data = new ArrayList<String>();
        if (unprocessed.length()>0) {
            for (int i=0;i<count;i++) {
                String line = (unprocessed.substring(0,unprocessed.indexOf('\n'))).trim();
                if ((line.trim()).length() > 0 && line.charAt(0) != '#') {
                    data.add(line.trim());
                }
                unprocessed = unprocessed.substring(unprocessed.indexOf('\n')+1);
            }
        }
        if ((unprocessed.trim()).length()>0 && unprocessed.charAt(0)!='#') {
            data.add(unprocessed.trim());
        }
        return data.toArray(new String[data.size()]);
    }

    //splits a comma seperated string into an array
    public static String[] getData(String input) {
        ArrayList<String> output = new ArrayList<String>();
        int start = 0;
        for(int i=0;i<input.length();i++) {
            if(input.charAt(i)==',') {
                String element = (input.substring(start, i)).trim();
                if(element.length()>0) {
                    output.add(element);
                }
                start = i+1;
            }
        }
        String element = (input.substring(start)).trim();
        if(element.length()>0) {
            output.add(element);
        }
        return output.toArray(new String[output.size()]);
    }

    //gets an itemstack from a string: name:meta
    public static ItemStack getStack(String input) {
        ItemStack output = null;
        int index1 = input.indexOf(':',0);
        if(index1>0) {
            int index2 = input.indexOf(':', index1 + 1);
            String name = input;
            String meta = "0";
            if (index2 > 0) {
                meta = input.substring(index2 + 1);
                name = input.substring(0, index2);
            }
            Block block = (Block) Block.blockRegistry.getObject(name);
            Item item = (Item) Item.itemRegistry.getObject(name);
            if (block != null && block != Blocks.air) {
                output = new ItemStack(block, 1, Integer.parseInt(meta));
            }else if(block==Blocks.air && name.equals("minecraft:air")) {
                output = new ItemStack(Blocks.air, 1, 0);
            } else if (item != null) {
                output = new ItemStack(item, 1, Integer.parseInt(meta));
            }
        }
        return output;
    }

    private static final String grassDropInstructions =
            "#Put a list of seeds here that will drop from tall grass with the following schematic: <seedname:seedmeta>,<weight>\n" +
            "#The seedname should be the name NEI gives you, the weight is the weighted chance for this seed to drop (for reference, minecraft wheat seeds have weight 10)\n" +
            "#Only define one seed per line, meta is optional. Example: minecraft:melon_seeds,10";

    private static final String customCropInstructions =
            "#Define custom crops here: <name>,<fruit:fruitmeta>,<baseblock:baseblockmeta>,<tier>,<rendermethod>,<information>\n" +
            "# - name:         is the name of the crop you want, for example: claysanthemum\n" +
            "# - fruit:        the fruit you want the crop to produce, for example: minecraft:clay:0 (metadata is optional, you can get this from NEI). Type \"null\" if you want the crop to have no fruit.\n" +
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

    private static final String seedBlackListInstructions =
            "#Define blacklisted seeds here: <mod>:<seedname>:<seedmeta>\n" +
            "#You can get these values from NEI\n" +
            "#Blacklisted seeds will not be able to planted on crops\n" +
            "#For example: AgriCraft:seedDandelion";

    private static final String soilWhitelistInstructions =
            "#Define blocks to whitelist as fertile soil here: <mod>:<blockname>:<blockmeta>\n" +
            "#You can get these values from NEI\n" +
            "#Whitelisting a block as a fertile soil means you can plant crops on them\n" +
            "#Note that this only works for crops that can be planted on farmland, crops that require a specific soil (e.g. nether wart or cactus) will still need that particular soil\n" +
            "#For example: Forestry:soil:0 (this will add forestry humus to the whitelist)";

    private static final String spreadChancesOverridesInstructions =
            "#Define overrides for spreading chances here: <mod>:<seedname>:<seedmeta>,<chance>\n" +
            "#You can get these values from NEI (example: minecraft:wheat_seeds is the vanilla seeds)\n" +
            "#The chance is an integer specified in percent, minimum is 0, maximum a 100. Spread chance is the chance that crops will spread to empty crosscrops\n" +
            "#For example: AgriCraft:seedDandelion,85";

    private static final String seedTierOverridesInstructions =
            "#Define overrides for seed tiers here: <mod>:<seedname>:<seedmeta>,<tier>\n" +
            "#You can get these values from NEI (example: minecraft:wheat_seeds is the vanilla seeds)\n" +
            "#The tier is an integer between 1 and 5 (1 and 5 included). The higher the tier, the slower it will grow, mutate, be analysed, ...\n" +
            "#For example: AgriCraft:seedDandelion,2";

    private static final String mutationInstructions =
            "#Define mutations here: <mutation>=<parent1>+<parent2>\n" +
            "#To specify a crop, write <mod>:<cropname>:<meta>, all in lowercase (meta is optional)\n" +
            "#You can get these values from NEI (example: minecraft:wheat_seeds is the vanilla seeds)\n" +
            "#Optionally you can also define a mutation like this: <mutation>=<parent1>+<parent2>,<id>,<block>\n" +
            "#The crops are specified in the same way, the id must be an integer: 1 requires a specified block to be below the farmland and 2 requires a specific block nearby\n" +
            "#The mutation will not occur if these requirements are not met. For example:\n" +
            "#magicalcrops:magicalcrops_ModMagicSeedsLead=magicalcrops:magicalcrops_ModMagicSeedsCopper+magicalcrops:magicalcrops_ModMagicSeedsTin,1,ThermalFoundation:Ore:3";

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
            "harvestcraft:teaseedItem=harvestcraft:seaweedseedItem+AgriCraft:seedDaisy\n" +
            "harvestcraft:tomatoseedItem=harvestcraft:sweetpotatoseedItem+AgriCraft:seedCarrot\n" +
            "harvestcraft:turnipseedItem=harvestcraft:parsnipseedItem+harvestcraft:radishseedItem\n" +
            "harvestcraft:wintersquashseedItem=minecraft:pumpkin_seeds+harvestcraft:zucchiniseedItem\n" +
            "harvestcraft:zucchiniseedItem=minecraft:pumpkin_seeds+harvestcraft:cucumberseedItem";

    private static final String weeeFlowersMutations =
            "weeeflowers:Red Flower Seed=AgriCraft:seedPoppy+AgriCraft:seedCarrot\n" +
            "weeeflowers:Yellow Flower Seed=AgriCraft:seedDandelion+minecraft:pumpkin_seeds\n" +
            "weeeflowers:Orange Flower Seed=weeeflowers:Yellow Flower Seed+weeeflowers:Red Flower Seed\n" +
            "weeeflowers:White Flower Seed=AgriCraft:seedTulipWhite+AgriCraft:seedAllium\n" +
            "weeeflowers:Pink Flower Seed=weeeflowers:Red Flower Seed+weeeflowers:White Flower Seed\n" +
            "weeeflowers:Blue Flower Seed=AgriCraft:seedOrchid+AgriCraft:seedDaisy\n" +
            "weeeflowers:Light Blue Flower Seed=weeeflowers:Blue Flower Seed+weeeflowers:White Flower Seed\n" +
            "weeeflowers:Purple Flower Seed=weeeflowers:Blue Flower Seed+weeeflowers:Red Flower Seed\n" +
            "weeeflowers:Magenta Flower Seed=weeeflowers:Purple Flower Seed+weeeflowers:Pink Flower Seed\n" +
            "weeeflowers:Green Flower Seed=weeeflowers:Yellow Flower Seed+weeeflowers:Blue Flower Seed\n" +
            "weeeflowers:Lime Flower Seed=weeeflowers:Green Flower Seed+weeeflowers:White Flower Seed\n" +
            "weeeflowers:Black Flower Seed=weeeflowers:Blue Flower Seed+weeeflowers:Green Flower Seed\n" +
            "weeeflowers:Light Grey Flower Seed=weeeflowers:Black Flower Seed+weeeflowers:White Flower Seed\n" +
            "weeeflowers:Dark Grey Flower Seed=weeeflowers:Light Grey Flower Seed+weeeflowers:Black Flower Seed\n" +
            "weeeflowers:Cyan Flower Seed=weeeflowers:Lime Flower Seed+weeeflowers:Light Blue Flower Seed\n" +
            "weeeflowers:Brown Flower Seed=weeeflowers:Green Flower Seed+weeeflowers:Red Flower Seed";

    private static final String botaniaMutations =
            "AgriCraft:seedBotaniaRed=AgriCraft:seedPoppy+AgriCraft:seedTulipRed\n" +
            "AgriCraft:seedBotaniaYellow=AgriCraft:seedDandelion+AgriCraft:seedTulipOrange\n" +
            "AgriCraft:seedBotaniaBlue=AgriCraft:seedOrchid+AgriCraft:seedAllium\n" +
            "AgriCraft:seedBotaniaOrange=AgriCraft:seedBotaniaYellow+AgriCraft:seedBotaniaRed\n" +
            "AgriCraft:seedBotaniaPurple=AgriCraft:seedBotaniaMagenta+AgriCraft:seedBotaniaBlue\n" +
            "AgriCraft:seedBotaniaGreen=AgriCraft:seedBotaniaBlue+AgriCraft:seedBotaniaYellow\n" +
            "AgriCraft:seedBotaniaMagenta=AgriCraft:seedBotaniaRed+AgriCraft:seedBotaniaBlue\n" +
            "AgriCraft:seedBotaniaPink=AgriCraft:seedBotaniaPurple+AgriCraft:seedBotaniaWhite\n" +
            "AgriCraft:seedBotaniaLime=AgriCraft:seedBotaniaWhite+AgriCraft:seedBotaniaGreen\n" +
            "AgriCraft:seedBotaniaCyan=AgriCraft:seedBotaniaGreen+AgriCraft:seedBotaniaBlue\n" +
            "AgriCraft:seedBotaniaLightBlue=AgriCraft:seedBotaniaBlue+AgriCraft:seedBotaniaWhite\n" +
            "AgriCraft:seedBotaniaBlack=AgriCraft:seedBotaniaBlue+AgriCraft:seedCactus\n" +
            "AgriCraft:seedBotaniaWhite=AgriCraft:seedTulipWhite+AgriCraft:seedDaisy\n" +
            "AgriCraft:seedBotaniaGray=AgriCraft:seedBotaniaBlack+AgriCraft:seedBotaniaWhite\n" +
            "AgriCraft:seedBotaniaLightGray=AgriCraft:seedBotaniaGray+AgriCraft:seedBotaniaWhite\n" +
            "AgriCraft:seedBotaniaBrown=AgriCraft:seedBotaniaPurple+AgriCraft:seedBotaniaGreen";

    private static final String minecraftMutations =
            "AgriCraft:seedSugarcane=minecraft:wheat_seeds+AgriCraft:seedCarrot\n" +
            "AgriCraft:seedCactus=AgriCraft:seedSugarcane+AgriCraft:seedPoppy\n" +
            "minecraft:pumpkin_seeds=AgriCraft:seedPotato+AgriCraft:seedCarrot\n" +
            "minecraft:melon_seeds=AgriCraft:seedCarrot+minecraft:pumpkin_seeds\n" +
            "AgriCraft:seedPoppy=AgriCraft:seedSugarcane+minecraft:pumpkin_seeds\n" +
            "AgriCraft:seedDandelion=AgriCraft:seedSugarcane+minecraft:melon_seeds\n" +
            "AgriCraft:seedOrchid=AgriCraft:seedPoppy+AgriCraft:seedDandelion\n" +
            "AgriCraft:seedAllium=AgriCraft:seedPoppy+AgriCraft:seedOrchid\n" +
            "AgriCraft:seedTulipRed=AgriCraft:seedPoppy+AgriCraft:seedAllium\n" +
            "AgriCraft:seedTulipOrange=AgriCraft:seedDaisy+AgriCraft:seedOrchid\n" +
            "AgriCraft:seedTulipWhite=AgriCraft:seedDaisy+AgriCraft:seedDandelion\n" +
            "AgriCraft:seedTulipPink=AgriCraft:seedAllium+AgriCraft:seedDandelion\n" +
            "AgriCraft:seedDaisy=AgriCraft:seedDandelion+AgriCraft:seedOrchid\n" +
            "AgriCraft:seedShroomRed=minecraft:nether_wart+AgriCraft:seedPoppy\n" +
            "AgriCraft:seedShroomBrown=minecraft:nether_wart+AgriCraft:seedPotato\n";

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
            "harvestcraft:ryeseedItem=Natura:barley.seed:0+minecraft:wheat_seeds\n" +
            "Natura:barley.seed:1=Natura:barley.seed:0+AgriCraft:seedDaisy";

    private static final String plantMegaPackMutations =
            "plantmegapack:seedCelery=minecraft:wheat_seeds+AgriCraft:seedTulipWhite\n" +
            "plantmegapack:seedTomato=minecraft:melon_seeds+AgriCraft:seedCarrot\n" +
            "plantmegapack:seedBeet=plantmegapack:seedTomato+AgriCraft:seedPotato\n" +
            "plantmegapack:seedLettuce=plantmegapack:seedCelery+AgriCraft:seedDaisy\n" +
            "plantmegapack:seedSpinach=plantmegapack:seedLettuce+AgriCraft:seedSugarcane\n" +
            "plantmegapack:seedCorn=minecraft:wheat_seeds+plantmegapack:seedCelery\n" +
            "plantmegapack:seedBellPepperYellow=AgriCraft:seedCarrot+AgriCraft:seedDandelion\n" +
            "plantmegapack:seedOnion=plantmegapack:seedBellPepperYellow+plantmegapack:seedLettuce\n" +
            "plantmegapack:seedCucumber=plantmegapack:seedBeet+plantmegapack:seedBellPepperYellow";

    private static final String chococraftMutations =
            "chococraft:Gysahl_Seeds=Agricraft:seedPotato+AgriCraft:seedTulipRed";

    private static final String chococraft_harvestcraftMutations =
            "chococraft:Gysahl_Seeds=harvestcraft:rutabagaseedItem+harvestcraft:beetseedItem";

    private static final String copperMutation =
            "AgriCraft:seedCuprosia=AgriCraft:seedRedstodendron+AgriCraft:seedTulipRed";

    private static final String tinMutation =
            "AgriCraft:seedPetinia=AgriCraft:seedLapender+AgriCraft:seedDaisy";

    private static final String leadMutation =
            "AgriCraft:seedPlombean=AgriCraft:seedFerranium+AgriCraft:seedPotato";

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
