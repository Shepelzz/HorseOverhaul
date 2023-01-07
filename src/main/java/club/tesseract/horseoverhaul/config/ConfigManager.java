package club.tesseract.horseoverhaul.config;

import club.tesseract.horseoverhaul.HorseOverhaul;
import club.tesseract.horseoverhaul.config.impl.GeneralConfig;
import club.tesseract.horseoverhaul.config.impl.ItemConfig;
import club.tesseract.horseoverhaul.config.impl.MessageConfig;
import club.tesseract.horseoverhaul.config.type.CraftableConfig;
import club.tesseract.horseoverhaul.config.type.SimpleItemConfig;
import club.tesseract.horseoverhaul.item.Item;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

public class ConfigManager {

    private ConfigManager(){}

    public MessageConfig messageConfig;
    public GeneralConfig generalConfig;
    public ItemConfig itemConfig;


    public void loadConfigs(){
        Path configPath = HorseOverhaul.getInstance().getDataFolder().toPath();
        if(!Files.exists(configPath)){
            try {
                Files.createDirectory(configPath);
            }catch (Exception e){
                HorseOverhaul.getInstance().getLogger().warning("Failed to create config directory");
                return;
            }
        }

        ConfigurationSerialization.registerClass(CraftableConfig.class);
        ConfigurationSerialization.registerClass(SimpleItemConfig.class);

        try {
            messageConfig = new MessageConfig();
            generalConfig = new GeneralConfig();
            itemConfig = new ItemConfig();
            messageConfig.reload();
            generalConfig.reload();
            itemConfig.reload();
        }catch (Exception e){
            HorseOverhaul.getInstance().getLogger().warning("Failed to load config files");
            e.printStackTrace();
        }

        registerRecipes();
        HorseOverhaul.getInstance().loadConfig();
    }


    public void registerRecipes(){
        unregisterRecipes();
        if(itemConfig.isBlankDeedItemEnabled()){
            CraftableConfig conf = itemConfig.getBlankDeedItem();
            Bukkit.addRecipe(conf.getRecipe(Item.BLANK_DEED));
        }
        if(itemConfig.isWhistleItemEnabled()){
            CraftableConfig conf = itemConfig.getWhistleItem();
            Bukkit.addRecipe(conf.getRecipe(Item.WHISTLE));
        }
    }

    public void unregisterRecipes(){
        Iterator<Recipe> it = Bukkit.getServer().recipeIterator();
        int found = 0;
        while(it.hasNext() && found < 2) {
            ItemStack n = it.next().getResult();
            if (Item.BLANK_DEED.isEqual(n) ||
                    Item.WHISTLE.isEqual(n) ) {
                it.remove();
                found++;
            }
        }
    }

    /**
     * Returns a file associated with the given path
     * @param filePath name of the file
     * @return Path to the file
     */
    public static Path fetchConfigFile(String filePath) {
        Path path =  HorseOverhaul.getInstance().getDataFolder().toPath().resolve(filePath);
        if(!Files.exists(path)){
            try{
                if(HorseOverhaul.getInstance().getResource(filePath) != null) {
                    HorseOverhaul.getInstance().saveResource(filePath, false);
                    return path;
                }
            }catch (Exception e){
                HorseOverhaul.getInstance().getLogger().warning("Failed to create config file: " + filePath);
            }
            try {
                Files.createFile(path);
                HorseOverhaul.getInstance().getLogger().info("creating " + filePath);
            }
            catch (IOException e) {
                String authorString = String.join(", ", HorseOverhaul.getInstance().getDescription().getAuthors());
                HorseOverhaul.getInstance().getLogger().warning("Error creating config, please report this to ["+authorString+"]:\n" + e);
            }
        }

        return path;
    }


    private static ConfigManager instance = null;
    public static ConfigManager get(){
        if(instance == null){
            instance = new ConfigManager();
        }
        return instance;
    }
}
