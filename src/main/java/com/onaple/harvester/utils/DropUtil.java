package com.onaple.harvester.utils;

import com.onaple.harvester.Harvester;
import com.onaple.harvester.data.handlers.ConfigurationHandler;
import com.onaple.harvester.data.beans.HarvestDropBean;
import com.onaple.itemizer.Itemizer;
import com.onaple.itemizer.service.IItemService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;

import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.extent.Extent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DropUtil {

    /**
     * Get ItemStack from harvester drop configuration
     *
     * @param harvestDropBean
     * @return the matching itemStack by id,pool or name
     */
    public static List<Optional<ItemStack>> getConfiguredDrops(HarvestDropBean harvestDropBean) {
        List<Optional<ItemStack>> drops = new ArrayList<>();
        if (harvestDropBean.getName() != null && !harvestDropBean.getName().isEmpty()) {
            Optional<ItemType> optionalType = Sponge.getRegistry().getType(ItemType.class, harvestDropBean.getName());
            if (optionalType.isPresent()) {
                drops.add(Optional.of(ItemStack.builder().itemType(optionalType.get()).build()));
            }
        }
        try {
            Optional<IItemService> optionalIItemService = Harvester.getItemService();
            if (optionalIItemService.isPresent()) {
                IItemService iItemService = optionalIItemService.get();
                if (harvestDropBean.getItemRef() != null) {
                    Optional<ItemStack> refItem = iItemService.retrieve(harvestDropBean.getItemRef());
                    if (refItem.isPresent()) {
                        drops.add(Optional.of(refItem.get()));
                    }
                }
                if (harvestDropBean.getPoolRef() != null) {
                    Optional<ItemStack> poolItem = iItemService.fetch(harvestDropBean.getPoolRef());
                    if (poolItem.isPresent()) {
                        drops.add(Optional.of(poolItem.get()));
                    }
                }
            }
        } catch (NoClassDefFoundError e) {
            Harvester.getLogger().error("Could not contact Itemizer plugin : " + e.getMessage());
        }
        return drops;
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
        return harvestDrops.stream().filter(harvestDropBean -> harvestDropBean.getBlockType().equals(blockTypeName) &&
                blockHasTraits(harvestDropBean.getBlockStates(), blockState)).findFirst();
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
