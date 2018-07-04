package com.ylinor.harvester.utils;

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
     *
     * @param blockState Block that has been destroyed
     * @param location   Location of the block
     */
    public static void spawnConfiguredDrop(BlockState blockState, Location<World> location) {
        Optional<HarvestDropBean> optionalHarvestDrop = identifyHarvestDrop(blockState);
        if (optionalHarvestDrop.isPresent()) {
            HarvestDropBean harvestDrop = optionalHarvestDrop.get();
            ItemStack itemDrop = getConfiguredDrop(harvestDrop);
            if (itemDrop != null) {
                spawnItemStack(location.getExtent(), getItemStackEntity(location, itemDrop));
            }
        }
    }

    /**
     * Get ItemStack from harvester drop configuration
     *
     * @param harvestDropBean
     * @return the matching itemStack by id,pool or name
     */
    public static ItemStack getConfiguredDrop(HarvestDropBean harvestDropBean) {
        try {
            Optional<IItemService> optionalIItemService = Sponge.getServiceManager().provide(IItemService.class);
            if (optionalIItemService.isPresent()) {
                IItemService iItemService = optionalIItemService.get();
                if (harvestDropBean.getItemRef() > 0) {
                    Optional<ItemStack> refItem = iItemService.retrieve(harvestDropBean.getItemRef());
                    if (refItem.isPresent()) {
                        return refItem.get();
                    }
                }
                if (harvestDropBean.getPoolRef() > 0) {
                    Optional<ItemStack> poolItem = iItemService.fetch(harvestDropBean.getPoolRef());
                    if (poolItem.isPresent()) {
                        return poolItem.get();
                    }
                }
            }
        } catch (NoClassDefFoundError e) {
        }
        if (harvestDropBean.getName() != null) {
            Optional<ItemType> optionalType = Sponge.getRegistry().getType(ItemType.class, harvestDropBean.getName());
            if (optionalType.isPresent()) {
                return ItemStack.builder().itemType(optionalType.get()).build();

            }
        }
        return null;
    }

    /**
     * Return harvestable drop if present in configuration
     *
     * @param blockState Block to identify drop from
     * @return Optional of harvest drop
     */
    public static Optional<HarvestDropBean> identifyHarvestDrop(BlockState blockState) {
        String blockTypeName = blockState.getType().getName().trim();
        List<HarvestDropBean> harvestDrops = ConfigurationHandler.getHarvestDropList();
        for (HarvestDropBean harvestDrop : harvestDrops) {
            if (harvestDrop.getBlockType().trim().equals(blockTypeName)) {
                boolean statesMatch = blockHasTraits(harvestDrop.getBlockStates(), blockState);
                if (statesMatch) {
                    return Optional.of(harvestDrop);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Return true if block state has the given traits
     * @param traits Traits required
     * @param blockState State of the block to check
     * @return boolean true if block matches
     */
    public static boolean blockHasTraits(Map<String, String> traits, BlockState blockState) {
        boolean statesMatch = true;
        for (Map.Entry<String, String> entry : traits.entrySet()) {
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
        return statesMatch;
    }

    /**
     * Spawn the itemStack
     * @param extent
     * @param itemEntity
     */
    public static void spawnItemStack(Extent extent, Entity itemEntity) {
        extent.spawnEntity(itemEntity);
    }

    /**
     * get an itemstack at a given block position
     *
     * @param itemStack Item to spawn
     * @param location  Location of the block
     */
    public static Entity getItemStackEntity(Location location, ItemStack itemStack){
        Extent extent = location.getExtent();
        Entity itemEntity = extent.createEntity(EntityTypes.ITEM, location.getPosition());
        itemEntity.offer(Keys.REPRESENTED_ITEM, itemStack.createSnapshot());
        return itemEntity;
    }
}
