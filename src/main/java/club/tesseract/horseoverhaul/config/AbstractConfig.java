package club.tesseract.horseoverhaul.config;

import club.tesseract.horseoverhaul.HorseOverhaul;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class AbstractConfig {

    public AbstractConfig() {
        load();
    }

    protected Path path;
    protected YamlConfiguration customFile;


    public YamlConfiguration getConfig() {
        return customFile;
    }

    abstract public String configName();
    abstract protected void addDefaults();

    protected void postLoad() {

    }

    public void load(){
        this.path = ConfigManager.fetchConfigFile(configName());
        this.customFile = YamlConfiguration.loadConfiguration(path.toFile());

        addDefaults();
        postLoad();
    }

    public void save(){
        try {
            customFile.save(ConfigManager.fetchConfigFile(configName()).toFile());
        }
        catch(IOException e) {
            HorseOverhaul.getInstance().getLogger().warning("Error saving config, please report this to RealName_123#2570:\n" + e);
        }
    }

    public void reload(){
        if(!Files.exists(this.path)) {
            load();
        }
        else {
            customFile = YamlConfiguration.loadConfiguration(path.toFile());
            postLoad();
        }

    }



}
