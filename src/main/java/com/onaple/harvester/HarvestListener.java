package com.onaple.harvester;

import com.onaple.harvester.data.beans.HarvestableBean;
import com.onaple.harvester.data.handlers.ConfigurationHandler;
import com.onaple.harvester.data.beans.HarvestDropBean;
import com.onaple.harvester.utils.DropUtil;
import com.onaple.harvester.utils.SpawnUtil;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.*;

public class HarvestListener {

    /**
     * Cancel block breaking dropping event unless specified in config
     *
     * @param event Item dropping event
     */

    @Listener
    public void onBlockBreak(ChangeBlockEvent.Break event , @First Player player) {

        if((!Harvester.getGlobalConfiguration().getWorldNames().contains(player.getWorld().getName()))
                ||player.gameMode() == GameModes.CREATIVE
                || player.hasPermission("harvester.block.breaking")){
            return;
        }
            for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
                Optional<HarvestableBean> optionalHarvestable = identifyHarvestable(transaction.getOriginal().getState());
                if (optionalHarvestable.isPresent()) {
                    HarvestableBean harvestable = optionalHarvestable.get();
                    Optional<ItemStack> optitemStack =player.getItemInHand(HandTypes.MAIN_HAND);
                    /*if(optitemStack.isPresent()){
                       String type = getToolType(optitemStack.get());
                      // player.sendMessage(Text.of("player tool type = "+ type + "| block tool type" + harvestable.getToolType()));
                       if (!type.equals(harvestable.getToolType())) {
                         event.setCancelled(true);
                       }
                    }*/
                    BlockSnapshot blockSnapshot = transaction.getOriginal();
                    blockSnapshot.getLocation().ifPresent(location ->
                            SpawnUtil.registerRespawningBlock(harvestable, blockSnapshot.getPosition(), location.getExtent().getName()));
                    return;
                }
            }
            event.setCancelled(true);

    }


    /**
     * Prevent growing event
     * @param event Event happening when stuff grows
     */
    @Listener
    public void onBlockGrowEvent(ChangeBlockEvent.Modify.Grow event){
        event.setCancelled(true);
    }



    @Listener
    public void onDropItemEvent(DropItemEvent.Destruct event){
        Harvester.getLogger().info(event.getClass().getName());
        Optional<Player> optionalPlayerCause = event.getCause().first(Player.class);
        Object source = event.getSource();
        Harvester.getLogger().info(source.toString());
        if(optionalPlayerCause.isPresent()) {
            Player player = optionalPlayerCause.get();

            if (source instanceof BlockSnapshot) {
                BlockSnapshot blockSnapshot = (BlockSnapshot) source;

                 Optional<ItemStack> toolOptional =  player.getItemInHand(HandTypes.MAIN_HAND);
                int toolLevel = 0;
            if (toolOptional.isPresent()){
                toolLevel = getToolLevel(toolOptional.get());
            }
                Optional<HarvestDropBean> optionalHarvestable = DropUtil.identifyHarvestDrop(blockSnapshot.getState(), toolLevel);
                if (optionalHarvestable.isPresent()) {
                    event.getEntities().clear();
                    HarvestDropBean harvestable = optionalHarvestable.get();


                    Optional<ItemStack> dropOptional = DropUtil.getConfiguredDrop(harvestable);
                    if(dropOptional.isPresent()){
                        event.getEntities().add(DropUtil.getItemStackEntity(player.getLocation(), dropOptional.get()));
                    } else {
                        Harvester.getLogger().warn("item not found");
                    }

                }

            } else {
                List<String> defaultDrops = ConfigurationHandler.getHarvestDefaultDropList();
                event.filterEntities(entity -> !defaultDrops.contains(entity.get(Keys.REPRESENTED_ITEM).get().toString()));
            }
        } else {
            Harvester.getLogger().info("no player present");
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

    /**
     * Get yhe item data at the given path
     * @param itemStack the item to get the data
     * @param path the path of the data
     * @return an optional object with the data
     */
   private Optional<Object> getToolData(ItemStack itemStack,String path) {
       return itemStack.toContainer().get(DataQuery.of("UnsafeData",path));
   }

    /**
     * Get the tool type of the item (set by itemizer)
     * @param toolItem the item to get the data
     * @return the type of the tool, return "hand" if no value present
     */
   private String getToolType(ItemStack toolItem) {
       Optional<Object> data = getToolData(toolItem,"ToolType");
       if(data.isPresent()){
           return data.get().toString();
       } else {
           return "hand";
       }
   }
    /**
     * Get the tool level of the item (set by itemizer)
     * @param toolItem the item to get the data
     * @return the level of the tool, 0 if no value present
     */
    private int getToolLevel(ItemStack toolItem) {
        Optional<Object> data = getToolData(toolItem,"ToolLevel");
        if(data.isPresent()){
            return (int) data.get();
        } else {
            return 0;
        }
    }
}
