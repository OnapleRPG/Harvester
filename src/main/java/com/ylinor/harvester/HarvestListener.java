package com.ylinor.harvester;

import com.flowpowered.math.vector.Vector3i;
import com.ylinor.harvester.data.beans.HarvestDropBean;
import com.ylinor.harvester.data.beans.HarvestableBean;
import com.ylinor.harvester.data.beans.RespawningBlockBean;
import com.ylinor.harvester.data.dao.RespawningBlockDao;
import com.ylinor.harvester.data.handlers.ConfigurationHandler;
import com.ylinor.harvester.data.serializers.BlockStateSerializer;
import com.ylinor.harvester.utils.DropUtil;
import com.ylinor.harvester.utils.SpawnUtil;
import com.ylinor.itemizer.service.IItemService;
import jdk.nashorn.internal.ir.Block;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.EventContextKey;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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

    @Listener
    public void onBlockGrowEvent(ChangeBlockEvent.Modify event){
       event.getTransactions().stream().forEach(blockSnapshotTransaction ->  Harvester.getLogger().info(blockSnapshotTransaction.getDefault().toString()));
       // event.getContext().asMap().entrySet().stream().forEach(eventContextKeyObjectEntry ->  Harvester.getLogger().info(eventContextKeyObjectEntry.toString()));
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
               event.getEntities().add(DropUtil.getItemStackEntity(event.getCause().first(Player.class).get().getLocation(),itemStack));
                return;
            }
            else {
                List<String> defaultDrops = ConfigurationHandler.getHarvestDefaultDropList();
                event.filterEntities(entity -> ! defaultDrops.contains(entity.get(Keys.REPRESENTED_ITEM).get()));
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
}
