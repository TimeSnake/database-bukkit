/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.database.bukkit.file;

import com.moandjiezana.toml.Toml;
import de.timesnake.database.core.DatabaseConfig;
import de.timesnake.database.core.DatabaseNotConfiguredException;
import de.timesnake.database.util.Database;
import java.io.File;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config implements DatabaseConfig {

    private static void error() {
        Database.LOGGER.warning("[Database] ######################################");
        Database.LOGGER.warning("[Database] #                                    #");
        Database.LOGGER.warning("[Database] #     Error while loading config     #");
        Database.LOGGER.warning("[Database] #  Please set the proxy-config-path  #");
        Database.LOGGER.warning("[Database] #      The server restarts now       #");
        Database.LOGGER.warning("[Database] #                                    #");
        Database.LOGGER.warning("[Database] ######################################");
        Bukkit.shutdown();

    }

    private final File configFile = new File("plugins/database/config.yml");
    private final YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
    protected File proxyConfigFile;
    protected Toml proxyConfig;

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

            try {
                config.load(configFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }

        String proxyPath = config.getString("proxy.path");
        if (proxyPath != null) {
            proxyConfigFile = new File(proxyPath + "/plugins/database/config.toml");
            if (proxyConfigFile != null && proxyConfigFile.exists()) {
                proxyConfig = new Toml().read(proxyConfigFile);
            } else {
                Config.error();
            }
        } else {
            Config.error();
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

}
