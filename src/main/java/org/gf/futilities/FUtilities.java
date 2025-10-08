package org.gf.futilities;

import org.bukkit.plugin.java.JavaPlugin;
import org.gf.futilities.listeners.*;
import org.gf.futilities.managers.ConfigManager;

public class FUtilities extends JavaPlugin {

    private static FUtilities instance;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        instance = this;

        // Initialiser le gestionnaire de config (avec backup automatique)
        configManager = new ConfigManager(this);
        configManager.loadConfig();

        // Enregistrer les commandes
        getCommand("fu").setExecutor(new MainCommand());

        // Enregistrer les listeners
        getServer().getPluginManager().registerEvents(new WeaponListener(this), this);
        getServer().getPluginManager().registerEvents(new FarmListener(this), this);
        getServer().getPluginManager().registerEvents(new BuilderListener(this), this);
        getServer().getPluginManager().registerEvents(new BowListener(this), this);
        getServer().getPluginManager().registerEvents(new HammerListener(this), this);
        getServer().getPluginManager().registerEvents(new DurabilityListener(this), this);

        getLogger().info("$$$$$$$$\\          $$\\     $$\\ $$\\ $$\\   $$\\     $$\\                     \n" +
                "$$  _____|         $$ |    \\__|$$ |\\__|  $$ |    \\__|                    \n" +
                "$$ |   $$\\   $$\\ $$$$$$\\   $$\\ $$ |$$\\ $$$$$$\\   $$\\  $$$$$$\\   $$$$$$$\\ \n" +
                "$$$$$\\ $$ |  $$ |\\_$$  _|  $$ |$$ |$$ |\\_$$  _|  $$ |$$  __$$\\ $$  _____|\n" +
                "$$  __|$$ |  $$ |  $$ |    $$ |$$ |$$ |  $$ |    $$ |$$$$$$$$ |\\$$$$$$\\  \n" +
                "$$ |   $$ |  $$ |  $$ |$$\\ $$ |$$ |$$ |  $$ |$$\\ $$ |$$   ____| \\____$$\\ \n" +
                "$$ |   \\$$$$$$  |  \\$$$$  |$$ |$$ |$$ |  \\$$$$  |$$ |\\$$$$$$$\\ $$$$$$$  |\n" +
                "\\__|    \\______/    \\____/ \\__|\\__|\\__|   \\____/ \\__| \\_______|\\_______/ \n" +
                "                                                                         \n" +
                "                                                                         \n" +
                "                                                                         " +
                "FUtilities a ete active avec succes!");
    }

    @Override
    public void onDisable() {
        getLogger().info("FUtilities a ete desactive!");
    }

    public static FUtilities getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}