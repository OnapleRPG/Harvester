package com.ylinor.harvester;

import com.ylinor.harvester.data.beans.HarvestableBean;
import com.ylinor.harvester.data.handlers.ConfigurationHandler;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;

import java.util.List;
import java.util.Optional;

public class HarvestListener {
    /**
     * Handle actions occurring when blocks are destroyed
     * @param event Resource destruction event
     */
    @Listener
    public void onBlockBreakEvent(ChangeBlockEvent.Break event) {
        final Optional<Player> player = event.getCause().first(Player.class);
        if (player.isPresent()) {
            if (player.get().gameMode().get() != GameModes.CREATIVE) {
                event.setCancelled(true);
                /*for (Transaction<BlockSnapshot> transaction: event.getTransactions()) {
                    BlockType destroyedBlockType = transaction.getOriginal().getState().getType();
                    if (isBlockRegistered(destroyedBlockType)) {
                        event.setCancelled(true);
                    }
                }*/
            }
        }
    }

    /**
     * Check if a block type is registered in configuration file
     * @param blockType Type of block
     * @return Block is present in config file
     */
    private boolean isBlockRegistered(BlockType blockType) {
        boolean blockRegistered = false;
        List<HarvestableBean> harvestables = ConfigurationHandler.getHarvestableList();
        for (HarvestableBean harvestable: harvestables) {
            if (harvestable.getType() == blockType.getName()) {
                blockRegistered = true;
            }
        }
        return blockRegistered;
    }
}
