package com.ylinor.harvester;

import javax.inject.Inject;

import com.ylinor.harvester.command.ReloadCommand;
import com.ylinor.harvester.data.dao.RespawningBlockDao;
import com.ylinor.harvester.data.handlers.ConfigurationHandler;
import com.ylinor.harvester.utils.SpawnUtil;
import com.ylinor.itemizer.service.IItemService;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

import java.io.IOException;
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

	private static Harvester harvester;

	public static Harvester getHarvester() {
		return harvester;
	}

	@Listener
	public void onServerStart(GameInitializationEvent event) {
		harvester = this;
		RespawningBlockDao.createTableIfNotExist();
		try {
			logger.info("Number of Block in configuration : " + loadHarvestable());
		} catch (IOException e) {
			logger.error("IOException : " + e.getMessage());
		} catch (ObjectMappingException e) {
			logger.error("ObjectMappingException : " + e.getMessage());
		}
		try {
			logger.info("Number of drops in configuration : " + loadDrops());

		} catch (IOException e) {
			logger.error("IOException : " + e.getMessage());
		} catch (ObjectMappingException e) {
			logger.error("ObjectMappingException : " + e.getMessage());
		}
		Sponge.getEventManager().registerListeners(this, new HarvestListener());
        Task.builder().execute(() -> SpawnUtil.checkBlockRespawn())
                .delay(5, TimeUnit.SECONDS).interval(30, TimeUnit.SECONDS)
                .name("Task respawning mined resources.").submit(this);

		CommandSpec reload = CommandSpec.builder()
				.description(Text.of("Reaload Harvester configuration from files."))
				.permission("harvester.admin")
				.executor(new com.ylinor.harvester.command.ReloadCommand()).build();
		Sponge.getCommandManager().register(this, reload, "reload-harvester");



        logger.info("HARVESTER initialized.");
	}
	public int loadHarvestable() throws IOException, ObjectMappingException {
		return ConfigurationHandler.readHarvestablesConfiguration(ConfigurationHandler.loadConfiguration(configDir+"/harvester/harvestables.conf"));
	}

	public int loadDrops() throws IOException, ObjectMappingException {
		return ConfigurationHandler.readHarvestDropsConfiguration(ConfigurationHandler.loadConfiguration(configDir+"/harvester/drops.conf"));

	}
}
