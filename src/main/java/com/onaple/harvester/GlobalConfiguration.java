package com.onaple.harvester;


import java.util.List;

public class GlobalConfiguration {
    private List<String> worldNames;
    private String blockBreakCommand;



    public GlobalConfiguration(List<String> worldNames,String blockBreakCommand)
    {
        this.worldNames = worldNames;
        this.blockBreakCommand = blockBreakCommand;
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
    public String getBlockBreakCommand() { return blockBreakCommand; }
}
