package com.onaple.harvester.data.serializers;

import com.google.common.reflect.TypeToken;
import com.onaple.harvester.data.beans.HarvestableBean;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import java.util.HashMap;
import java.util.Map;

public class HarvestableSerializer implements TypeSerializer<HarvestableBean> {

    @Override
    public HarvestableBean deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        String blocType = value.getNode("type").getString();
        String toolType = value.getNode("toolType").getString();
        if(toolType == null){
            toolType = "hand";
        }
        int respawnMin = value.getNode("respawnmin").getInt();
        int respawnMax = value.getNode("respawnmax").getInt();
        Map<String, String> states = new HashMap<>();
        Map<Object, ?> statesNode = value.getNode("state").getChildrenMap();
        for (Map.Entry<Object, ?> entry : statesNode.entrySet()) {
            if (entry.getKey() instanceof String && entry.getValue() instanceof ConfigurationNode) {
                states.put((String)entry.getKey(), ((ConfigurationNode) entry.getValue()).getNode("value").getString());
            }
        }
        return new HarvestableBean(blocType, states, respawnMin, respawnMax,toolType);
    }

    @Override
    public void serialize(TypeToken<?> type, HarvestableBean obj, ConfigurationNode value) throws ObjectMappingException {

    }
}