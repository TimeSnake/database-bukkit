package de.timesnake.database.bukkit.main;

import de.timesnake.database.bukkit.file.Config;
import de.timesnake.database.core.file.DatabaseNotConfiguredException;
import de.timesnake.database.util.Database;
import org.bukkit.plugin.java.JavaPlugin;


public class DatabaseBukkit extends JavaPlugin {

    @Override
    public void onEnable() {
        Config config = new Config();
        config.onEnable();
        config.loadProxy();
        try {
            Database.getInstance().connect(config);
        } catch (DatabaseNotConfiguredException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("[Database] Databases loaded successfully!");

    }

    public static void disconnect() {
        Database.getInstance().close();
    }
}
