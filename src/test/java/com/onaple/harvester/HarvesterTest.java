package com.onaple.harvester;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.mctester.api.junit.MinecraftRunner;
import org.spongepowered.mctester.internal.BaseTest;
import org.spongepowered.mctester.internal.event.StandaloneEventListener;
import org.spongepowered.mctester.junit.TestUtils;

import java.util.concurrent.atomic.AtomicBoolean;

@RunWith(MinecraftRunner.class)
public class HarvesterTest extends BaseTest {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    public HarvesterTest(TestUtils testUtils) {
        super(testUtils);
    }

    /**
     * Command /reload-harvester should returns confirmation messages
     */
    @Test
    public void testReloadHarvester() throws Throwable {
        String harvestableReloadedString = "Harvestables configuration successfully reloaded";
        String dropsReloadedString = "Drops configuration successfully reloaded";
        AtomicBoolean harvestableReloadedBool = new AtomicBoolean(false), dropsReloadedBool = new AtomicBoolean(false);
        this.testUtils.listenOneShot(() -> {
            this.testUtils.getClient().sendMessage("/reload-harvester");
        }, new StandaloneEventListener<>(MessageChannelEvent.class, (MessageChannelEvent event) -> {
            if (event.getMessage().toPlain().contains(harvestableReloadedString)) {
                harvestableReloadedBool.set(true);
            }
            if (event.getMessage().toPlain().contains(dropsReloadedString)) {
                dropsReloadedBool.set(true);
            }
        }));
        Assert.assertTrue(harvestableReloadedBool.get() && dropsReloadedBool.get());
    }

    /**
     * Mining protected block
     */
    @Test
    public void testMineProtectedBlock() throws Throwable {
        this.testUtils.getThePlayer().getInventory().offer(ItemStack.of(ItemTypes.STONE, 1));
        this.testUtils.getThePlayer().getInventory().offer(ItemStack.of(ItemTypes.DIAMOND_PICKAXE, 1));
        this.testUtils.waitForInventoryPropagation();

        this.testUtils.getClient().selectHotbarSlot(0);
        Vector3i blockPosition = this.testUtils.getThePlayer().getPosition().add(new Vector3d(2, -1, 0)).toInt();
        this.testUtils.getClient().lookAtBlock(blockPosition);
        this.testUtils.getClient().rightClick();

        this.testUtils.getClient().selectHotbarSlot(1);
        try {
            this.testUtils.runOnMainThread(() -> {
                Assert.assertEquals(BlockTypes.STONE, testUtils.getThePlayer().getWorld().getBlockType(blockPosition.toInt().add(0, 1, 0)));
                testUtils.getThePlayer().offer(Keys.GAME_MODE, GameModes.SURVIVAL);
            });
            this.testUtils.getClient().holdLeftClick(true);
            this.testUtils.sleepTicks(10);
            this.testUtils.getClient().holdLeftClick(false);
            this.testUtils.runOnMainThread(() -> {
                Assert.assertEquals(BlockTypes.STONE, testUtils.getThePlayer().getWorld().getBlockType(blockPosition.toInt().add(0, 1, 0)));
            });
        } catch (Throwable e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Mining allowed block
     */
    @Test
    public void testMineAllowedBlock() throws Throwable {
        this.testUtils.getThePlayer().getInventory().clear();
        this.testUtils.waitForInventoryPropagation();
        this.testUtils.getThePlayer().getInventory().offer(ItemStack.of(ItemTypes.DIRT, 1));
        this.testUtils.getThePlayer().getInventory().offer(ItemStack.of(ItemTypes.DIAMOND_SHOVEL, 1));
        this.testUtils.waitForInventoryPropagation();

        this.testUtils.getClient().selectHotbarSlot(0);
        Vector3i blockPosition = this.testUtils.getThePlayer().getPosition().add(new Vector3d(-2, -1, 0)).toInt();
        this.testUtils.getClient().lookAtBlock(blockPosition);
        this.testUtils.getClient().rightClick();
        this.testUtils.sleepTicks(5);

        this.testUtils.getClient().selectHotbarSlot(1);
        try {
            this.testUtils.runOnMainThread(() -> {
                Assert.assertEquals(BlockTypes.DIRT, testUtils.getThePlayer().getWorld().getBlockType(blockPosition.toInt().add(0, 1, 0)));
                testUtils.getThePlayer().offer(Keys.GAME_MODE, GameModes.SURVIVAL);
            });
            this.testUtils.getClient().holdLeftClick(true);
            this.testUtils.sleepTicks(10);
            this.testUtils.getClient().holdLeftClick(false);
            this.testUtils.runOnMainThread(() -> {
                Assert.assertEquals(BlockTypes.AIR, testUtils.getThePlayer().getWorld().getBlockType(blockPosition.toInt().add(0, 1, 0)));
            });
        } catch (Throwable e) {
            throw new AssertionError(e);
        }
    }

}
