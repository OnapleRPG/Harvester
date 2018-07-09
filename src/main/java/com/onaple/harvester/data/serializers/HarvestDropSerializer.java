package com.onaple.harvester.data.serializers;

import com.google.common.reflect.TypeToken;
import com.onaple.harvester.data.beans.HarvestDropBean;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import java.util.HashMap;
import java.util.Map;

public class HarvestDropSerializer implements TypeSerializer<HarvestDropBean> {

    @Override
    public HarvestDropBean deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        String blockType = value.getNode("type").getString();
        Map<String, String> states = new HashMap<>();
        Map<Object, ?> statesNode = value.getNode("state").getChildrenMap();
        for (Map.Entry<Object, ?> entry : statesNode.entrySet()) {
            if (entry.getKey() instanceof String && entry.getValue() instanceof ConfigurationNode) {
                states.put((String)entry.getKey(), ((ConfigurationNode) entry.getValue()).getNode("value").getString());
            }
        }
        String itemName = value.getNode("item_name").getString();
        int itemRef = value.getNode("item_ref").getInt();
        int poolRef = value.getNode("pool_ref").getInt();
        return new HarvestDropBean(blockType, states, itemName, itemRef, poolRef);
    }

    @Override
    public void serialize(TypeToken<?> type, HarvestDropBean obj, ConfigurationNode value) throws ObjectMappingException {

    }
}