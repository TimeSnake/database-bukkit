/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.database.bukkit.file;

import com.moandjiezana.toml.Toml;
import de.timesnake.database.core.DatabaseConfig;
import de.timesnake.database.util.Database;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Config implements DatabaseConfig {

  private static void error() {
    Database.LOGGER.warn("[Database] ######################################");
    Database.LOGGER.warn("[Database] #                                    #");
    Database.LOGGER.warn("[Database] #     Error while loading config     #");
    Database.LOGGER.warn("[Database] #  Please set the proxy-config-path  #");
    Database.LOGGER.warn("[Database] #      The server restarts now       #");
    Database.LOGGER.warn("[Database] #                                    #");
    Database.LOGGER.warn("[Database] ######################################");
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
      if (proxyConfigFile.exists()) {
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

}
