package de.timesnake.database.bukkit.file;

import de.timesnake.database.core.file.DatabaseConfig;
import de.timesnake.database.core.file.DatabaseNotConfiguredException;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Config implements DatabaseConfig {

    private final File configFile = new File("plugins/database/config.yml");
    private final YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

    private File proxyFile;
    private YamlConfiguration proxyConfig;

    public void onEnable() {

        //ConfigFile
        File dir = new File("plugins/database");
        if (!dir.exists()) {
            dir.mkdir();
        }


        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }


            this.load();

            config.set("proxy.path", "nopath");

            this.save();
            this.load();


        }

        String proxyPath = config.getString("proxy.path");
        if (proxyPath != null) {
            if (!proxyPath.equalsIgnoreCase("nopath")) {
                proxyFile = new File(proxyPath + "/plugins/database/config.yml");
                if (proxyFile != null && proxyFile.exists()) {
                    proxyConfig = YamlConfiguration.loadConfiguration(proxyFile);
                } else Config.error();
            } else Config.error();
        } else Config.error();
    }

    private static void error() {
        System.out.println("[Database] ######################################");
        System.out.println("[Database] #                                    #");
        System.out.println("[Database] #     Error while loading config     #");
        System.out.println("[Database] #  Please set the proxy-config-path  #");
        System.out.println("[Database] #      The server restarts now       #");
        System.out.println("[Database] #                                    #");
        System.out.println("[Database] ######################################");
        Bukkit.shutdown();

    }

    public void load() {
        try {
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadProxy() {
        try {
            proxyConfig.load(proxyFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getString(String path) {
        return proxyConfig.getString(path);
    }

    @Override
    public String getDatabaseName(String databaseType) throws DatabaseNotConfiguredException {
        String name = proxyConfig.getString("database." + databaseType + ".name");
        if (name != null) {
            return name;
        }
        throw new DatabaseNotConfiguredException(databaseType, "name");
    }

    @Override
    public String getDatabaseUrl(String databaseType) throws DatabaseNotConfiguredException {
        String url = proxyConfig.getString("database." + databaseType + ".url");
        if (url != null) {
            return url;
        }
        throw new DatabaseNotConfiguredException(databaseType, "url");
    }

    @Override
    public String getDatabaseTable(String databaseType, String tableType, String defaultName) throws DatabaseNotConfiguredException {
        String tableName = proxyConfig.getString("database." + databaseType + ".tables." + tableType);
        if (tableName != null) {
            return tableName;
        }
        return defaultName;
    }

}
