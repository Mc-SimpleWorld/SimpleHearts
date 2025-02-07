package org.nott;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.nott.global.GlobalFactory;
import org.nott.global.KeyWord;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Logger;

public final class SimpleHearts extends JavaPlugin {

    public static Economy ECONOMY;

    public final Logger logger = super.getLogger();

    public static YamlConfiguration MESSAGE_YML_FILE;

    public static YamlConfiguration CONFIG_YML_FILE;

    public static BukkitScheduler SCHEDULER;

    public static SimpleHearts INSTANCE;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.pluginInit();
        PluginManager pluginManager = this.getServer().getPluginManager();
        this.getCommand(KeyWord.COMMAND.SIMPLE_HEARTS).setExecutor(new SimpleHearts());
        SCHEDULER = this.getServer().getScheduler();
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        ECONOMY = rsp.getProvider();
        INSTANCE = this;
    }

    public void pluginInit(){
        YamlConfiguration message = new YamlConfiguration();
        YamlConfiguration config = new YamlConfiguration();
        String path = this.getDataFolder() + File.separator + GlobalFactory.MESSAGE_YML;
        String configPath = this.getDataFolder() + File.separator + GlobalFactory.CONFIG_YML;
        File file = new File(path);
        File configFile = new File(configPath);
        if (!file.exists()) {
            this.saveResource(GlobalFactory.MESSAGE_YML, false);
            try {
                message.load(Objects.requireNonNull(this.getTextResource(GlobalFactory.MESSAGE_YML)));
            } catch (IOException | InvalidConfigurationException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                message.load(file);
            } catch (IOException | InvalidConfigurationException e) {
                throw new RuntimeException(e);
            }
        }
        if (!configFile.exists()) {
            this.saveResource(GlobalFactory.MESSAGE_YML, false);
            try {
                config.load(Objects.requireNonNull(this.getTextResource(GlobalFactory.MESSAGE_YML)));
            } catch (IOException | InvalidConfigurationException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                config.load(configFile);
            } catch (IOException | InvalidConfigurationException e) {
                throw new RuntimeException(e);
            }
        }
        MESSAGE_YML_FILE = message;
        CONFIG_YML_FILE = config;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
