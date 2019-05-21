package com.onaple.harvester;

import com.onaple.harvester.data.beans.HarvestableBean;
import com.onaple.harvester.data.handlers.ConfigurationHandler;
import com.onaple.harvester.data.beans.HarvestDropBean;
import com.onaple.harvester.utils.DropUtil;
import com.onaple.harvester.utils.SpawnUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

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
                || player.gameMode().get().equals(GameModes.CREATIVE)
                || player.hasPermission("harvester.block.breaking")){
            return;
        }
            for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
                Optional<HarvestableBean> optionalHarvestable = identifyHarvestable(transaction.getOriginal().getState());
                if (optionalHarvestable.isPresent()) {
                    HarvestableBean harvestable = optionalHarvestable.get();
                    BlockSnapshot blockSnapshot = transaction.getOriginal();
                    blockSnapshot.getLocation().ifPresent(location ->
                            SpawnUtil.registerRespawningBlock(harvestable, blockSnapshot.getPosition(), location.getExtent().getName()));
                    String blockBreakCommand = Harvester.getGlobalConfiguration().getBlockBreakCommand();
                    if(!(blockBreakCommand == null || blockBreakCommand.isEmpty())){
                        Sponge.getCommandManager().process(Sponge.getServer().getConsole(),Harvester.getGlobalConfiguration().getBlockBreakCommand());
                    }
                    return;
                } else {
                    player.sendMessage(Text.of(TextColors.RED,"you don't have the rigth to break this block"));
                }
            }
            event.setCancelled(true);
    }


    /**
     * Prevent growing event
     * @param event Event happening when stuff grows
     */
    @Listener
    public void onBlockGrowEvent(ChangeBlockEvent.Modify.Grow event) {
        if (!Harvester.getGlobalConfiguration().getBlockGrowth()) {
            event.setCancelled(true);
        }
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
                Optional<HarvestDropBean> optionalHarvestable = DropUtil.identifyHarvestDrop(blockSnapshot.getState());
                if (optionalHarvestable.isPresent()) {
                    event.getEntities().clear();
                    HarvestDropBean harvestable = optionalHarvestable.get();

                    Optional<ItemStack> dropOptional = DropUtil.getConfiguredDrop(harvestable);
                    if(dropOptional.isPresent()){
                        event.getEntities().add(DropUtil.getItemStackEntity(player.getLocation(), dropOptional.get()));
                    } else {
                        Harvester.getLogger().warn("Item not found");
                    }
                }
            } else {
                List<String> defaultDrops = ConfigurationHandler.getHarvestDefaultDropList();
                event.filterEntities(entity -> !defaultDrops.contains(entity.get(Keys.REPRESENTED_ITEM).get().toString()));
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
       Harvester.getLogger().info(" block broken is {}",blockTypeName);
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
