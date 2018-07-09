package com.onaple.harvester.data.serializers;

import java.util.HashMap;
import java.util.Map;

public class BlockStateSerializer {
    public static String serialize(Map<String, String> state) {
        String serializedState = "";
        for (Map.Entry<String, String> entry : state.entrySet()) {
            serializedState += entry.getKey() + ":" + entry.getValue() + ";";
        }
        return serializedState;
    }

    public static Map<String, String> deserialize(String serializedState) {
        Map<String, String> state = new HashMap<>();
        String[] entries = serializedState.split(";");
        for (String entry : entries) {
            String[] keyValue = entry.split(":");
            if (keyValue.length == 2) {
                state.put(keyValue[0], keyValue[1]);
            }
        }
        return state;
    }
}
