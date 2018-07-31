package com.onaple.harvester.data.beans;

import java.util.Map;
import java.util.Optional;

public class HarvestDropBean {
    /** Type of the block who's drop from **/
    private String blockType;
    /** States of the block **/
    private Map<String, String> blockStates;
    /** Name of the item to drop **/
    private String name;
    /** Reference of the item in Itemizer **/
    private String itemRef;
    /** Reference of the pool in Itemizer **/
    private String poolRef;

    public HarvestDropBean(String blockType, Map<String, String> blockStates, String name, String itemRef, String poolRef) {
        this.blockType = blockType;
        this.blockStates = blockStates;
        this.name = name;
        this.itemRef = itemRef;
        this.poolRef = poolRef;
    }

    public String getBlockType() {
        return blockType;
    }
    public void setBlockType(String blockType) {
        this.blockType = blockType;
    }

    public Map<String, String> getBlockStates() {
        return blockStates;
    }
    public void setBlockStates(Map<String, String> blockStates) {
        this.blockStates = blockStates;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getItemRef() {
        return itemRef;
    }
    public void setItemRef(String itemRef) {
        this.itemRef = itemRef;
    }

    public String getPoolRef() {
        return poolRef;
    }
    public void setPoolRef(String poolRef) {
        this.poolRef = poolRef;
    }
}
