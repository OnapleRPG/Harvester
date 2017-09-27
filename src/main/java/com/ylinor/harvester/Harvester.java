package com.ylinor.harvester;

import javax.inject.Inject;

import com.ylinor.harvester.data.handlers.ConfigurationHandler;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;

import java.nio.file.Path;

@Plugin(id = "harvester", name = "Harvester", version = "0.0.1")
public class Harvester {

	@Inject
	private Logger logger;

	@Inject
    @ConfigDir(sharedRoot=true)
    private Path configDir;


	@Listener
	public void onServerStart(GameInitializationEvent event) {
        ConfigurationHandler.readHarvestablesConfiguration(ConfigurationHandler.loadConfiguration(configDir+"/harvestables.conf"));

        Sponge.getEventManager().registerListeners(this, new HarvestListener());

        logger.info("HARVESTER initialized.");
	}
}
