package com.onaple.harvester;


import java.util.List;

public class GlobalConfiguration {
    private List<String> worldNames;

    public GlobalConfiguration(List<String> worldNames) {
        this.worldNames = worldNames;
    }

    public boolean addWorldName(String newWorldName){
        if(worldNames.contains(newWorldName)){
            return false;
        }
        worldNames.add(newWorldName);
        return true;
    }

    public List<String> getWorldNames() {
        return worldNames;
    }
}
