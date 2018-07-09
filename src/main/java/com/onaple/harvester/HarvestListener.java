package com.onaple.harvester;

import com.onaple.harvester.data.beans.HarvestableBean;
import com.onaple.harvester.data.handlers.ConfigurationHandler;
import com.onaple.harvester.data.beans.HarvestDropBean;
import com.onaple.harvester.utils.DropUtil;
import com.onaple.harvester.utils.SpawnUtil;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;

import java.util.*;

public class HarvestListener {

    /**
     * Cancel block breaking dropping event unless specified in config
     * @param event Item dropping event
     */

    @Listener
    public void onBlockBreakEvent(ChangeBlockEvent.Break event) {
        final Optional<Player> player = event.getCause().first(Player.class);
        if (player.isPresent() && player.get().gameMode().get() != GameModes.CREATIVE) {

            for (Transaction<BlockSnapshot> transaction: event.getTransactions()) {
                Optional<HarvestableBean> optionalHarvestable = identifyHarvestable(transaction.getOriginal().getState());

                if (optionalHarvestable.isPresent()) {
                    HarvestableBean harvestable = optionalHarvestable.get();
                    BlockSnapshot blockSnapshot = transaction.getOriginal();
                    SpawnUtil.registerRespawningBlock(harvestable, blockSnapshot.getPosition());

                    return;
                }
            }
            event.setCancelled(true);
        }
    }

    /**
     * prevent wheat growing.
     * @param event
     */
    @Listener
    public void onBlockGrowEvent(ChangeBlockEvent.Modify event){
       //event.getTransactions().stream().forEach(blockSnapshotTransaction ->  Harvester.getLogger().info(blockSnapshotTransaction.getDefault().toString()));
        event.setCancelled(true);
    }


    @Listener
    public void onDropItemEvent(DropItemEvent.Destruct event) {
        Object source = event.getSource();
        if(source instanceof BlockSnapshot){
            BlockSnapshot blockSnapshot = (BlockSnapshot) source;

            Optional<HarvestDropBean> optionalHarvestable = DropUtil.identifyHarvestDrop(blockSnapshot.getState());
            if (optionalHarvestable.isPresent()) {
                event.getEntities().clear();
                HarvestDropBean harvestable = optionalHarvestable.get();
                ItemStack itemStack = DropUtil.getConfiguredDrop(harvestable);
                Optional<Player> optionalPlayerCause = event.getCause().first(Player.class);
                if (optionalPlayerCause.isPresent()) {
                    event.getEntities().add(DropUtil.getItemStackEntity(optionalPlayerCause.get().getLocation(),itemStack));
                }
            }
            else {
                List<String> defaultDrops = ConfigurationHandler.getHarvestDefaultDropList();
                event.filterEntities(entity -> ! defaultDrops.contains(entity.get(Keys.REPRESENTED_ITEM).get().toString()));
            }
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
               boolean statesMatch = DropUtil.blockHasTraits(harvestable.getStates(), blockState);
               if (statesMatch) {
                   return Optional.of(harvestable);
               }
           }
       }
       return Optional.empty();
   }
}
