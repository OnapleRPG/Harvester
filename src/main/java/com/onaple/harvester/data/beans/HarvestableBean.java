package com.onaple.harvester.data.beans;

import java.util.Map;

public class HarvestableBean {
    /** Type of the block **/
    private String type;
    /** States of the block **/
    private Map<String, String> states;
    /** Minimum time before resource respawn **/
    private int respawnMin;
    /** Maximum time before resource respawn **/
    private int respawnMax;

    public HarvestableBean(String type, Map<String, String> states, int respawnMin, int respawnMax) {
        this.type = type;
        this.states = states;
        this.respawnMin = respawnMin;
        this.respawnMax = respawnMax;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getStates() {
        return states;
    }
    public void setStates(Map<String, String> states) {
        this.states = states;
    }

    public int getRespawnMin() {
        return respawnMin;
    }
    public void setRespawnMin(int respawnMin) {
        this.respawnMin = respawnMin;
    }

    public int getRespawnMax() {
        return respawnMax;
    }
    public void setRespawnMax(int respawnMax) {
        this.respawnMax = respawnMax;
    }
}
