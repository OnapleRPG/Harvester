package com.ylinor.harvester;

import com.ylinor.harvester.data.beans.HarvestableBean;
import com.ylinor.harvester.data.handlers.ConfigurationHandler;
import org.slf4j.Logger;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

public class HarvestListener {
    @Inject
    private static Logger logger;

    /**
     * Prevent resource destruction if resource is not registered and player is not in creative mode
     * @param breakEvent Resource destruction event
     */
    @Listener
    public void onDestroyBlockEvent(ChangeBlockEvent.Break breakEvent) {
        final Optional<Player> player = breakEvent.getCause().<Player>first(Player.class);
        if (player.isPresent() && player.get().gameMode() != GameModes.CREATIVE) {
            for(Transaction<BlockSnapshot> transaction: breakEvent.getTransactions()) {
                BlockType destroyedBlockType = transaction.getOriginal().getState().getType();
                boolean blockRegistered = false;
                List<HarvestableBean> harvestables = ConfigurationHandler.getHarvestableList();
                for (HarvestableBean harvestable: harvestables) {
                    if (harvestable.getType() == destroyedBlockType.getName()) {
                        blockRegistered = true;
                    }
                }
                if (!blockRegistered) {
                    breakEvent.setCancelled(true);
                }
            }
        }
    }
}
