package com.ylinor.harvester;

import com.ylinor.harvester.data.beans.HarvestableBean;
import com.ylinor.harvester.data.handlers.ConfigurationHandler;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.property.item.HarvestingProperty;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.item.inventory.ItemStack;

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
                for (Transaction<BlockSnapshot> transaction: event.getTransactions()) {
                    BlockType destroyedBlockType = transaction.getOriginal().getState().getType();
                    if (!isBlockBreakable(destroyedBlockType, player.get().getItemInHand(HandTypes.MAIN_HAND))) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    /**
     * Check if a block type is breakable with given tool
     * @param blockType Type of block
     * @param optionalTool Tool in player's hand
     * @return Block is present in config file
     */
    private boolean isBlockBreakable(BlockType blockType, Optional<ItemStack> optionalTool) {
        String blockTypeName = blockType.getName().trim();
        List<HarvestableBean> harvestables = ConfigurationHandler.getHarvestableList();
        for (HarvestableBean harvestable: harvestables) {
            if (harvestable.getType().trim().equals(blockTypeName)) {
                if (harvestable.getBreakableByHand()) {
                    return true;
                } else {
                    if (optionalTool.isPresent()) {
                        ItemStack tool = optionalTool.get();
                        Optional<HarvestingProperty> optionalHarvestingProperty = tool.getProperty(HarvestingProperty.class);
                        if (optionalHarvestingProperty.isPresent()) {
                            HarvestingProperty harvestingProperty = optionalHarvestingProperty.get();
                            return harvestingProperty.getValue().contains(blockType);
                        }
                    }
                    return false;
                }
            }
        }
        return false;
    }
}
