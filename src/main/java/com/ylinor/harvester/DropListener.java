package com.ylinor.harvester;

import com.ylinor.harvester.data.beans.HarvestableBean;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.spawn.EntitySpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class DropListener {
    @Listener
    public void onDropItemEvent(DropItemEvent.Destruct event) {
        Optional<Player> player = event.getCause().first(Player.class);
        if (player.isPresent()) {
            for (Entity entity: event.getEntities()) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Handle actions occurring when blocks are destroyed
     * @param event Resource destruction event
     */
    @Listener(order= Order.POST)
    public void onBlockBreakEvent(ChangeBlockEvent.Break event) {
        final Optional<Player> player = event.getCause().first(Player.class);
        if (player.isPresent() && player.get().gameMode().get() != GameModes.CREATIVE) {
            for (Transaction<BlockSnapshot> transaction: event.getTransactions()) {
                spawnItemStack(ItemStack.builder().itemType(ItemTypes.COOKED_PORKCHOP).build(), transaction.getOriginal().getLocation().get());
                return;
            }
        }
    }

    public void spawnItemStack(ItemStack itemStack, Location<World> location) {
        Extent extent = location.getExtent();
        Entity itemEntity = extent.createEntity(EntityTypes.ITEM, location.getPosition());
        itemEntity.offer(Keys.REPRESENTED_ITEM, itemStack.createSnapshot());
        Task.Builder taskBuilder = Task.builder();
        taskBuilder.execute(new Runnable() {
            public void run() {
                extent.spawnEntity(itemEntity, Cause.source(EntitySpawnCause.builder().entity(itemEntity).type(SpawnTypes.PLUGIN).build()).build());
            }
        }).delay(350, TimeUnit.MILLISECONDS).submit(Sponge.getPluginManager().getPlugin("harvester").get());

    }
}
