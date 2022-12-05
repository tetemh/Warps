package fr.tetemh.warps;

import fr.mrmicky.fastinv.FastInvManager;
import fr.tetemh.warps.commands.WarpsCommands;
import fr.tetemh.warps.database.Loader;
import fr.tetemh.warps.managers.CWarpsManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class Warps extends JavaPlugin {

    private static Warps instance;
    private static Loader storage;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        saveDefaultConfig();

        //TODO Hikari
        try {
            storage = new Loader(this.getConfig());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //TODO FastInv
        FastInvManager.register(this);

        //TODO init CWarpManager
        CWarpsManager cWarpsManager = new CWarpsManager();
        cWarpsManager.init();

        //TODO commands
        getCommand("warps").setExecutor(new WarpsCommands());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        CWarpsManager cWarpsManager = CWarpsManager.getInstance();
        cWarpsManager.save();
        
        storage.getDataSource().close();
    }

    public static Warps getInstance() {
        return instance;
    }
    public static Loader getLoader(){
        return storage;
    }
}
