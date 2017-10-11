package com.ylinor.harvester;

import javax.inject.Inject;

import com.ylinor.harvester.data.dao.RespawningBlockDao;
import com.ylinor.harvester.data.handlers.ConfigurationHandler;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Plugin(id = "harvester", name = "Harvester", version = "0.0.1")
public class Harvester {

	@Inject
    @ConfigDir(sharedRoot=true)
    private Path configDir;

	private static Logger logger;
	@Inject
	private void setLogger(Logger logger) {
		Harvester.logger = logger;
	}
	public static Logger getLogger() {
		return logger;
	}

	@Listener
	public void onServerStart(GameInitializationEvent event) {
		RespawningBlockDao.createTableIfNotExist();
        ConfigurationHandler.readHarvestablesConfiguration(ConfigurationHandler.loadConfiguration(configDir+"/harvestables.conf"));

        Sponge.getEventManager().registerListeners(this, new HarvestListener());
        Task.builder().execute(() -> HarvestListener.checkBlockRespawn())
                .async().delay(5, TimeUnit.SECONDS).interval(30, TimeUnit.SECONDS)
                .name("Task respawning mined resources.").submit(this);

        logger.info("HARVESTER initialized.");
	}
}
