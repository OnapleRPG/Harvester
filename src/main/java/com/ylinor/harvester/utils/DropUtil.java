package com.ylinor.harvester.utils;

import com.ylinor.harvester.Harvester;
import com.ylinor.harvester.data.beans.HarvestDropBean;
import com.ylinor.harvester.data.handlers.ConfigurationHandler;
import com.ylinor.itemizer.service.IItemService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DropUtil {

    /**
     * Spawn configured drops for a given block at a given location
     * @param blockState Block that has been destroyed
     * @param location Location of the block
     */
    public static void spawnConfiguredDrop(BlockState blockState, Location<World> location) {
        Optional<HarvestDropBean> optionalHarvestDrop = identifyHarvestDrop(blockState);
        if (optionalHarvestDrop.isPresent()) {
            HarvestDropBean harvestDrop = optionalHarvestDrop.get();
            try {
                Optional<IItemService> optionalIItemService = Sponge.getServiceManager().provide(IItemService.class);
                if (optionalIItemService.isPresent()) {
                    IItemService iItemService = optionalIItemService.get();
                    if (harvestDrop.getItemRef() > 0) {
                        Optional<ItemStack> refItem = iItemService.retrieve(harvestDrop.getItemRef());
                        if (refItem.isPresent()) {
                            spawnItemStack(refItem.get(), location);
                        }
                    }
                    if (harvestDrop.getPoolRef() > 0) {
                        Optional<ItemStack> poolItem = iItemService.fetch(harvestDrop.getPoolRef());
                        if (poolItem.isPresent()) {
                            spawnItemStack(poolItem.get(), location);
                        }
                    }
                }
            } catch (NoClassDefFoundError e) {
            }
            if (harvestDrop.getName() != null) {
                Optional<ItemType> optionalType = Sponge.getRegistry().getType(ItemType.class, harvestDrop.getName());
                if (optionalType.isPresent()) {
                    ItemStack namedItem = ItemStack.builder().itemType(optionalType.get()).build();
                    spawnItemStack(namedItem, location);
                }
            }
        }
    }

    /**
     * Return harvestable drop if present in configuration
     * @param blockState Block to identify drop from
     * @return Optional of harvest drop
     */
    private static Optional<HarvestDropBean> identifyHarvestDrop(BlockState blockState) {
        String blockTypeName = blockState.getType().getName().trim();
        List<HarvestDropBean> harvestDrops = ConfigurationHandler.getHarvestDropList();
        for (HarvestDropBean harvestDrop: harvestDrops) {
            if (harvestDrop.getBlockType().trim().equals(blockTypeName)) {
                boolean statesMatch = true;
                Map<String, String> blockTraits = harvestDrop.getBlockStates();
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
                    return Optional.of(harvestDrop);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Spawn an itemstack at a given block position
     * @param itemStack Item to spawn
     * @param location Location of the block
     */
    public static void spawnItemStack(ItemStack itemStack, Location<World> location) {
        location = location.add(0.5, 0.25, 0.5);
        Extent extent = location.getExtent();
        Entity itemEntity = extent.createEntity(EntityTypes.ITEM, location.getPosition());
        itemEntity.offer(Keys.REPRESENTED_ITEM, itemStack.createSnapshot());
        extent.spawnEntity(itemEntity);
    }
}
