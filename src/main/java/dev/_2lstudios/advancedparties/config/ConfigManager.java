package dev._2lstudios.advancedparties.config;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.InvalidConfigurationException;

import dev._2lstudios.advancedparties.AdvancedParties;

public class ConfigManager {
    private final Map<String, Configuration> configs;
    private final AdvancedParties plugin;

    public ConfigManager(final AdvancedParties plugin) {
        this.configs = new HashMap<>();
        this.plugin = plugin;
    }

    public Configuration getConfig(final String name) {
        if (this.configs.containsKey(name)) {
            return configs.get(name);
        }

        final File configFile = new File(this.plugin.getDataFolder(), name);
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            this.plugin.saveResource(name, false);
        }

        final Configuration config = new Configuration(configFile);
        try {
            config.load();
            this.configs.put(name, config);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        return config;
    }
}