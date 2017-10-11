package com.ylinor.harvester;

import com.flowpowered.math.vector.Vector3i;
import com.ylinor.harvester.data.beans.HarvestableBean;
import com.ylinor.harvester.data.beans.RespawningBlockBean;
import com.ylinor.harvester.data.dao.RespawningBlockDao;
import com.ylinor.harvester.data.handlers.ConfigurationHandler;
import com.ylinor.harvester.data.serializers.BlockStateSerializer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.sql.Timestamp;
import java.util.*;

public class HarvestListener {
    /**
     * Handle actions occurring when blocks are destroyed
     * @param event Resource destruction event
     */
    @Listener
    public void onBlockBreakEvent(ChangeBlockEvent.Break event) {
        final Optional<Player> player = event.getCause().first(Player.class);
        if (player.isPresent() && player.get().gameMode().get() != GameModes.CREATIVE) {
            for (Transaction<BlockSnapshot> transaction: event.getTransactions()) {
                Optional<HarvestableBean> optionalHarvestable = identifyHarvestable(transaction.getOriginal().getState());
                if (optionalHarvestable.isPresent()) {
                    HarvestableBean harvestable = optionalHarvestable.get();
                    registerRespawningBlock(harvestable, transaction.getOriginal().getPosition());
                    return;
                }
            }
            event.setCancelled(true);
        }
    }

    /**
     * Return harvestable if present in configuration
     * @param blockState Block to identify
     * @return Optional of harvestable
     */
    private Optional<HarvestableBean> identifyHarvestable(BlockState blockState) {
        String blockTypeName = blockState.getType().getName().trim();
        List<HarvestableBean> harvestables = ConfigurationHandler.getHarvestableList();
        for (HarvestableBean harvestable: harvestables) {
            if (harvestable.getType().trim().equals(blockTypeName)) {
                boolean statesMatch = true;
                Map<String, String> blockTraits = harvestable.getStates();
                for (Map.Entry<String, String> entry : blockTraits.entrySet()) {
                    Optional<BlockTrait<?>> blockTrait = blockState.getTrait(entry.getKey());
                    if (blockTrait.isPresent()) {
                        Optional<?> traitValue = blockState.getTraitValue(blockTrait.get());
                        if (traitValue.isPresent()) {
                            if (!traitValue.get().toString().equals(entry.getValue())) {
                                statesMatch = false;
                            }
                        } else {
                            statesMatch = false;
                        }
                    } else {
                        statesMatch = false;
                    }
                }
                if (statesMatch) {
                    return Optional.of(harvestable);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Register a mined block in database so it can be respawn later
     * @param harvestable Block to respawn later
     */
    private void registerRespawningBlock(HarvestableBean harvestable, Vector3i position) {
        Random random = new Random();
        int respawnMin = harvestable.getRespawnMin()*60, respawnMax = harvestable.getRespawnMax()*60;
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
    static public void checkBlockRespawn() {
        World world = Sponge.getServer().getWorld("world").get();
        List<RespawningBlockBean> respawningBlocks = RespawningBlockDao.getRespawningBlocks();
        if (respawningBlocks.size() > 0) {
            Harvester.getLogger().info("Respawning resources : " + respawningBlocks.size() + " resources.");
        }
        for (RespawningBlockBean block: respawningBlocks) {
            Location<World> location = new Location<World>(world, block.getX(), block.getY(), block.getZ());
            Optional<BlockType> replacingType = Sponge.getRegistry().getType(BlockType.class, block.getBlockType());
            if (replacingType.isPresent()) {
                Map<String, String> state = BlockStateSerializer.deserialize(block.getSerializedBlockStates());
                location.setBlock(addTraits(replacingType.get(), state), Cause.source(Sponge.getPluginManager().getPlugin("harvester").get()).build());
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
    static private BlockState addTraits(BlockType blockType, Map<String, String> traits) {
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
