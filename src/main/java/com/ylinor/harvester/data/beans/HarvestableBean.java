package com.ylinor.harvester.data.beans;

public class HarvestableBean {
    /** Nom du type du bloc **/
    private String type;

    public HarvestableBean(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
}
