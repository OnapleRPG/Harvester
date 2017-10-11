package com.ylinor.harvester.data.beans;

public class HarvestableBean {
    /** Type of the block **/
    private String type;
    /** Minimum time before resource respawn **/
    private int respawnMin;
    /** Maximum time before resource respawn **/
    private int respawnMax;

    public HarvestableBean(String type, int respawnMin, int respawnMax) {
        this.type = type;
        this.respawnMin = respawnMin;
        this.respawnMax = respawnMax;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
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
