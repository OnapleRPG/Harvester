package com.ylinor.harvester.data.beans;

import java.util.Optional;

public class HarvestDropBean {
    /** Name of the item to drop **/
    private Optional<String> name;
    /** Reference of the item in Itemizer **/
    private Optional<Integer> itemRef;
    /** Reference of the pool in Itemizer **/
    private Optional<Integer> poolRef;

    public Optional<String> getName() {
        return name;
    }
    public void setName(Optional<String> name) {
        this.name = name;
    }

    public Optional<Integer> getItemRef() {
        return itemRef;
    }
    public void setItemRef(Optional<Integer> itemRef) {
        this.itemRef = itemRef;
    }

    public Optional<Integer> getPoolRef() {
        return poolRef;
    }
    public void setPoolRef(Optional<Integer> poolRef) {
        this.poolRef = poolRef;
    }
}
