package com.ylinor.harvester.data.beans;

import java.util.List;

public class HarvestableBean {
    /** Type of the block **/
    private String type;
    /** Block is harvestable by hand **/
    private boolean breakableByHand;

    public HarvestableBean(String type, boolean breakableByHand) {
        this.type = type;
        this.breakableByHand = breakableByHand;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public boolean getBreakableByHand() {
        return breakableByHand;
    }
    public void setBreakableByHand(boolean breakableByHand) {
        this.breakableByHand = breakableByHand;
    }
}
