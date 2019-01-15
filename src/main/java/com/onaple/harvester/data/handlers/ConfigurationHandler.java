package com.onaple.harvester.data.handlers;

import com.google.common.reflect.TypeToken;
import com.onaple.harvester.GlobalConfiguration;
import com.onaple.harvester.data.beans.HarvestableBean;
import com.onaple.harvester.data.serializers.HarvestDropSerializer;
import com.onaple.harvester.data.serializers.HarvestableSerializer;
import com.onaple.harvester.data.beans.HarvestDropBean;
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
    private static List<HarvestDropBean> harvestDropList;

    public static List<HarvestableBean> getHarvestableList(){
        return harvestableList;
    }
    public static List<String> getHarvestDefaultDropList(){
        return harvestDefaultDropList;
    }
    public static List<HarvestDropBean> getHarvestDropList() {
        return harvestDropList;
    }

    /**
     * Read harvestable configuration and interpret it
     * @param configurationNode ConfigurationNode to read from
     */
    public static int readHarvestablesConfiguration(CommentedConfigurationNode configurationNode) throws ObjectMappingException {
        harvestableList = new ArrayList<>();
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(HarvestableBean.class), new HarvestableSerializer());
        //try {
            harvestableList = configurationNode.getNode("harvestables").getList(TypeToken.of(HarvestableBean.class));
            return harvestableList.size();
      /*  } catch (ObjectMappingException e) {
            Harvester.getLogger().error("Error while reading configuration 'harvestables' : " + e.getMessage());
        }*/
    }
    public static GlobalConfiguration readGlobalConfiguration(CommentedConfigurationNode configurationNode) throws ObjectMappingException {
        List<String> worldnames = configurationNode.getNode("worlds").getList(TypeToken.of(String.class));
        return new GlobalConfiguration(worldnames);
    }

    /**
     * Read block drops configuration and interpret it
     * @param configurationNode ConfigurationNode to read from
     */
    public static int readHarvestDropsConfiguration(CommentedConfigurationNode configurationNode) throws ObjectMappingException {
        harvestDefaultDropList = new ArrayList<>();
        List<? extends ConfigurationNode> defaultDropNodeList = configurationNode.getNode("default").getChildrenList();
        for (ConfigurationNode defaultDropNode : defaultDropNodeList) {
            String defaultDropType = defaultDropNode.getString();
            if (!defaultDropType.isEmpty()) {
                harvestDefaultDropList.add(defaultDropType);
            }
        }
        harvestDropList = new ArrayList<>();
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(HarvestDropBean.class), new HarvestDropSerializer());
      //  try {
            harvestDropList = configurationNode.getNode("harvest_items").getList(TypeToken.of(HarvestDropBean.class));
            return harvestDropList.size();
       /* } catch (ObjectMappingException e) {
            Harvester.getLogger().error("Error while reading configuration 'harvest_drops' : " + e.getMessage());
        }*/
    }

    /**
     * Load configuration from file
     * @param configName Name of the configuration in the configuration folder
     * @return Configuration ready to be used
     */
    public static CommentedConfigurationNode loadConfiguration(String configName) throws IOException {
        ConfigurationLoader<CommentedConfigurationNode> configLoader = HoconConfigurationLoader.builder().setPath(Paths.get(configName)).build();
        CommentedConfigurationNode configNode = null;
      //  try {
            configNode = configLoader.load();
      /*  } catch (IOException e) {
            Harvester.getLogger().error("Error while loading configuration '" + configName + "' : " + e.getMessage());
        }*/
        return configNode;
    }
}
