package com.onaple.harvester;


import java.util.List;

public class GlobalConfiguration {
    private List<String> worldNames;
    private String blockBreakCommand;
    private boolean blockGrowth;

    public GlobalConfiguration(List<String> worldNames, String blockBreakCommand, boolean blockGrowth)
    {
        this.worldNames = worldNames;
        this.blockBreakCommand = blockBreakCommand;
        this.blockGrowth = blockGrowth;
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
    public boolean getBlockGrowth() {
        return blockGrowth;
    }
}
