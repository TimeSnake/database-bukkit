/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.database.bukkit.main;

import de.timesnake.database.bukkit.file.Config;
import de.timesnake.database.core.DatabaseNotConfiguredException;
import de.timesnake.database.util.Database;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;


public class DatabaseBukkit extends JavaPlugin {

  public static void disconnect() {
    Database.getInstance().close();
  }

  @Override
  public void onEnable() {
    Config config = new Config();
    config.onEnable();
    try {
      Database.getInstance().connect(config);
    } catch (DatabaseNotConfiguredException e) {
      Bukkit.getLogger().log(Level.INFO, e.getMessage());
    }
  }
}
