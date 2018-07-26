package com.onaple.harvester;

import com.flowpowered.math.vector.Vector3d;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.mctester.api.junit.MinecraftRunner;
import org.spongepowered.mctester.internal.BaseTest;
import org.spongepowered.mctester.internal.event.StandaloneEventListener;
import org.spongepowered.mctester.junit.TestUtils;

@RunWith(MinecraftRunner.class)
public class ReloadTest extends BaseTest {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    public ReloadTest(TestUtils testUtils) {
        super(testUtils);
    }

    /**
     * Command /reload-harvester should returns confirmation messages
     */
    @Test
    public void testReloadHarvester() throws Throwable {
        String harvestableReloadedString = "Harvestables configuration successfully reloaded";
        String dropsReloadedString = "Drops configuration successfully reloaded";
        this.testUtils.listenOneShot(() -> {
            this.testUtils.getClient().sendMessage("/reload-harvester");
        }, new StandaloneEventListener<>(MessageChannelEvent.class, (MessageChannelEvent event) -> {
            Assert.assertTrue(event.getMessage().toPlain().contains(harvestableReloadedString)
                              || event.getMessage().toPlain().contains(dropsReloadedString));
        }));
    }

    /**
     * Mining protected block
     */
    /*@Test
    public void testMineProtectedBlock() throws Throwable {
        this.testUtils.getThePlayer().getInventory().offer(ItemStack.of(ItemTypes.STONE, 1));
        Vector3d blockPosition = this.testUtils.getThePlayer().getPosition().add(new Vector3d(2, 5, 0));
        this.testUtils.getClient().lookAt(blockPosition);
        this.testUtils.getClient().rightClick();
        this.testUtils.getThePlayer().getInventory().offer(ItemStack.of(ItemTypes.DIAMOND_PICKAXE, 1));
        this.testUtils.getClient().selectHotbarSlot(1);
        this.testUtils.sleepTicks(300);

        this.testUtils.listenOneShot(() -> {
            Sponge.getServer().getWorld(Sponge.getServer().getDefaultWorldName()).ifPresent(world -> {
                Assert.assertEquals(BlockTypes.STONE, world.getBlockType(blockPosition.toInt()));
            });
        }, new StandaloneEventListener<>(MessageChannelEvent.class, (MessageChannelEvent event) -> {
        }));
    }*/

}