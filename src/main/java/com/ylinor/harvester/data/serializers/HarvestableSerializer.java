package com.ylinor.harvester.data.serializers;

import com.google.common.reflect.TypeToken;
import com.ylinor.harvester.data.beans.HarvestableBean;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import java.util.List;

public class HarvestableSerializer implements TypeSerializer<HarvestableBean> {

    @Override
    public HarvestableBean deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        String blocType = value.getNode("type").getString();
        boolean breakableByHand = value.getNode("byhand").getBoolean();
        return new HarvestableBean(blocType, breakableByHand);
    }

    @Override
    public void serialize(TypeToken<?> type, HarvestableBean obj, ConfigurationNode value) throws ObjectMappingException {

    }
}