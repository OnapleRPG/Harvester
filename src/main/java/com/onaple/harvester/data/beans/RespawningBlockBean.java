package com.onaple.harvester.data.beans;

public class RespawningBlockBean {
    /** Database id **/
    private int id;
    /** X position of block **/
    private int x;
    /** Y position of block **/
    private int y;
    /** Z position of block **/
    private int z;
    /** Type of the block **/
    private String block_type;
    /** States to apply to block **/
    private String serializedBlockStates;
    /** World within which the block has to respawn **/
    private String world;
    /** Timestamp when block must respawn **/
    private int respawnTime;

    public RespawningBlockBean(int x, int y, int z, String block_type, String serializedBlockStates, String world, int respawnTime) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.block_type = block_type;
        this.serializedBlockStates = serializedBlockStates;
        this.world = world;
        this.respawnTime = respawnTime;
    }
    public RespawningBlockBean(int id, int x, int y, int z, String block_type, String serializedBlockStates, String world, int respawnTime) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
        this.block_type = block_type;
        this.serializedBlockStates = serializedBlockStates;
        this.world = world;
        this.respawnTime = respawnTime;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }
    public void setZ(int z) {
        this.z = z;
    }

    public String getBlockType() {
        return block_type;
    }
    public void setBlockType(String block_type) {
        this.block_type = block_type;
    }

    public String getSerializedBlockStates() {
        return serializedBlockStates;
    }
    public void setSerializedBlockStates(String serialized_block_states) {
        this.serializedBlockStates = serialized_block_states;
    }

    public String getWorld() {
        return world;
    }
    public void setWorld(String world) {
        this.world = world;
    }

    public int getRespawnTime() {
        return respawnTime;
    }
    public void setRespawnTime(int respawnTime) {
        this.respawnTime = respawnTime;
    }
}
