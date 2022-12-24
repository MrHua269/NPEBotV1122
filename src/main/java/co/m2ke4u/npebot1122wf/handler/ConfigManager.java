package co.m2ke4u.npebot1122wf.handler;

import co.m2ke4u.npebot1122wf.data.ConfigEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class ConfigManager {
    private static final File CONFIG_FOLDER = new File("configs");
    private static final String GLOBAL_CONFIG_NAME = "global.json";
    private static ConfigEntry currentConfig;
    private static final Logger logger = LogManager.getLogger();

    static {
        if (!CONFIG_FOLDER.exists()){
            CONFIG_FOLDER.mkdirs();
        }
    }

    public synchronized static void init(){
        try {
            currentConfig = ConfigEntry.readFromFile(CONFIG_FOLDER,GLOBAL_CONFIG_NAME);
        }catch (IllegalStateException e){
            currentConfig = new ConfigEntry(
                    "1",
                    "1",
                    false,
                    false,
                    "",
                    1,
                    "",
                    1,
                    "",
                    false,
                    false,
                    true);
            currentConfig.saveToFile(CONFIG_FOLDER,GLOBAL_CONFIG_NAME);
        }
        logger.info("Config loaded!");
    }

    public static synchronized ConfigEntry getCurrentConfig(){
        return currentConfig;
    }
}
