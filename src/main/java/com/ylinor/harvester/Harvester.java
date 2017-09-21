package com.ylinor.harvester;

import javax.inject.Inject;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Plugin(id = "harvester", name = "Harvester", version = "0.0.1")
public class Harvester {

	@Inject
	private Logger logger;

	@Inject
    @ConfigDir(sharedRoot=true)
    private Path configDir;

    private CommentedConfigurationNode harvestables;
    public CommentedConfigurationNode getHarvestables() {
        return harvestables;
    }

	@Listener
	public void onServerInitialization(GameInitializationEvent event) {
        harvestables = loadConfiguration("harvestables.conf");

		logger.info("HARVESTER initialized.");
	}

    /**
     * Load configuration from file
     * @param configName Name of the configuration in the configuration folder
     * @return Configuration ready to be used
     */
	private CommentedConfigurationNode loadConfiguration(String configName) {
        ConfigurationLoader<CommentedConfigurationNode> configLoader = HoconConfigurationLoader.builder().setPath(Paths.get(configDir+"/"+configName)).build();
        CommentedConfigurationNode configNode = null;
        try {
             configNode = configLoader.load();
        } catch (IOException e) {
            logger.error("Error while loading configuration " + configName + " : " + e.getMessage());
        }
        return configNode;
    }
}
