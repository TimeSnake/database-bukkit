/*
 * database-bukkit.main
 * Copyright (C) 2022 timesnake
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; If not, see <http://www.gnu.org/licenses/>.
 */

package de.timesnake.database.bukkit.file;

import com.moandjiezana.toml.Toml;
import de.timesnake.database.core.file.DatabaseConfig;
import de.timesnake.database.core.file.DatabaseNotConfiguredException;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Config implements DatabaseConfig {

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
            } else Config.error();
        } else Config.error();
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
