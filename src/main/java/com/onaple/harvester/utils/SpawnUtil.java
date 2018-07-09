package com.onaple.harvester.utils;

import com.flowpowered.math.vector.Vector3i;
import com.onaple.harvester.data.beans.HarvestableBean;
import com.onaple.harvester.data.beans.RespawningBlockBean;
import com.onaple.harvester.data.dao.RespawningBlockDao;
import com.onaple.harvester.Harvester;
import com.onaple.harvester.data.serializers.BlockStateSerializer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.sql.Timestamp;
import java.util.*;

public class SpawnUtil {

    /**
     * Register a mined block in database so it can be respawn later
     * @param harvestable Block to respawn later
     */
    public static void registerRespawningBlock(HarvestableBean harvestable, Vector3i position) {
        Random random = new Random();
        int respawnMin = harvestable.getRespawnMin()*60;
        int respawnMax = harvestable.getRespawnMax()*60;
        int respawnDelay = random.nextInt((respawnMax - respawnMin)+1) + respawnMin;
        Timestamp respawnDate = new Timestamp(Calendar.getInstance().getTime().getTime());
        respawnDate.setTime(respawnDate.getTime()/1000 + respawnDelay);
        RespawningBlockBean respawningBlock = new RespawningBlockBean(position.getX(), position.getY(), position.getZ(),
                harvestable.getType(), BlockStateSerializer.serialize(harvestable.getStates()), (int)respawnDate.getTime());
        RespawningBlockDao.addRespawningBlock(respawningBlock);
    }

    /**
     * Check if resources need to be respawn and do it if necessary
     */
    public static void checkBlockRespawn() {
        Optional<World> optionalWorld = Sponge.getServer().getWorld("world");
        if (!optionalWorld.isPresent()) {
            return;
        }
        World world = optionalWorld.get();
        List<RespawningBlockBean> respawningBlocks = RespawningBlockDao.getRespawningBlocks();
        if (!respawningBlocks.isEmpty()) {
            Harvester.getLogger().info("Respawning resources : " + respawningBlocks.size() + " resources.");
        }
        for (RespawningBlockBean block: respawningBlocks) {
            Location<World> location = new Location<>(world, block.getX(), block.getY(), block.getZ());
            Optional<BlockType> replacingType = Sponge.getRegistry().getType(BlockType.class, block.getBlockType());
            if (replacingType.isPresent()) {
                Map<String, String> state = BlockStateSerializer.deserialize(block.getSerializedBlockStates());
                location.setBlock(addTraits(replacingType.get(), state));
            }
        }
        RespawningBlockDao.removeRespawningBlocks(respawningBlocks);
    }

    /**
     * Add block traits to a future block
     * @param blockType Type of the block
     * @param traits Map containing all the traits
     * @return BlockState of the future block
     */
    private static BlockState addTraits(BlockType blockType, Map<String, String> traits) {
        BlockState blockState = blockType.getDefaultState();
        for (Map.Entry<String, String> trait : traits.entrySet()) {
            Optional<BlockTrait<?>> optTrait = blockState.getTrait(trait.getKey());
            if (optTrait.isPresent()) {
                Optional<BlockState> newBlockState = blockState.withTrait(optTrait.get(), trait.getValue());
                if (newBlockState.isPresent()) {
                    blockState = newBlockState.get();
                }
            }
        }
        return blockState;
    }
}
