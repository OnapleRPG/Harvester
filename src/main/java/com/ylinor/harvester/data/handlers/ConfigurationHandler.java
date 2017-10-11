package com.ylinor.harvester.data.handlers;

import com.google.common.reflect.TypeToken;
import com.ylinor.harvester.Harvester;
import com.ylinor.harvester.data.beans.HarvestableBean;
import com.ylinor.harvester.data.serializers.HarvestableSerializer;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ConfigurationHandler {
    private ConfigurationHandler() {}

    private static List<HarvestableBean> harvestableList;

    public static List<HarvestableBean> getHarvestableList(){
        return harvestableList;
    }

    /**
     * Read configuration and interpret it
     * @param configurationNode ConfigurationNode to read from
     */
    public static void readHarvestablesConfiguration(CommentedConfigurationNode configurationNode){
        harvestableList = new ArrayList<>();
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(HarvestableBean.class), new HarvestableSerializer());
        try {
            harvestableList = configurationNode.getNode("harvestables").getList(TypeToken.of(HarvestableBean.class));
            for (HarvestableBean harvestable: harvestableList) {
                Harvester.getLogger().debug("Harvestable from config : " + harvestable.getType());
            }
        } catch (ObjectMappingException e) {
            Harvester.getLogger().error("Error while reading configuration 'harvestables' : " + e.getMessage());
        }
    }

    /**
     * Load configuration from file
     * @param configName Name of the configuration in the configuration folder
     * @return Configuration ready to be used
     */
    public static CommentedConfigurationNode loadConfiguration(String configName) {
        ConfigurationLoader<CommentedConfigurationNode> configLoader = HoconConfigurationLoader.builder().setPath(Paths.get(configName)).build();
        CommentedConfigurationNode configNode = null;
        try {
            configNode = configLoader.load();
        } catch (IOException e) {
            Harvester.getLogger().error("Error while loading configuration '" + configName + "' : " + e.getMessage());
        }
        return configNode;
    }
}
