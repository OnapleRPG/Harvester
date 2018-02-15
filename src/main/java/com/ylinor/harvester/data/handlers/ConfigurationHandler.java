package com.ylinor.harvester.data.handlers;

import com.google.common.reflect.TypeToken;
import com.ylinor.harvester.Harvester;
import com.ylinor.harvester.data.beans.HarvestableBean;
import com.ylinor.harvester.data.serializers.HarvestableSerializer;
import ninja.leaping.configurate.ConfigurationNode;
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
    private static List<String> harvestDefaultDropList;

    public static List<HarvestableBean> getHarvestableList(){
        return harvestableList;
    }
    public static List<String> getHarvestDefaultDropList(){
        return harvestDefaultDropList;
    }

    /**
     * Read harvestable configuration and interpret it
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
     * Read block drops configuration and interpret it
     * @param configurationNode ConfigurationNode to read from
     */
    public static void readHarvestDropsConfiguration(CommentedConfigurationNode configurationNode) {
        harvestDefaultDropList = new ArrayList<>();
        List<? extends ConfigurationNode> defaultDropNodeList = configurationNode.getNode("default").getChildrenList();
        for (ConfigurationNode defaultDropNode : defaultDropNodeList) {
            String defaultDropType = defaultDropNode.getString();
            if (!defaultDropType.isEmpty()) {
                harvestDefaultDropList.add(defaultDropType);
            }
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
