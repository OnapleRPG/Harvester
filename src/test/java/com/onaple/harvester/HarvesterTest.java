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

        /*this.testUtils.listenOneShot(() -> {
            this.testUtils.getThePlayer().getInventory().offer(ItemStack.of(ItemTypes.STONE, 1));
            Vector3d blockPosition = this.testUtils.getThePlayer().getPosition().add(new Vector3d(2, -1, 0));
            this.testUtils.getClient().lookAt(blockPosition);
            this.testUtils.getClient().rightClick();
            this.testUtils.getThePlayer().getInventory().offer(ItemStack.of(ItemTypes.DIAMOND_PICKAXE, 1));
            this.testUtils.getClient().selectHotbarSlot(1);
            try {
                this.testUtils.runOnMainThread(() -> {
                    Sponge.getServer().getWorld(Sponge.getServer().getDefaultWorldName()).ifPresent(world -> {
                        Assert.assertEquals(BlockTypes.STONE, world.getBlockType(blockPosition.toInt().add(0, 1, 0)));
                    });
                });
            } catch (Throwable e) {
                Assert.fail(e.getMessage());
            }
            this.testUtils.sleepTicks(200);
        }, new StandaloneEventListener<>(ChangeBlockEvent.Break.class, (ChangeBlockEvent.Break event) -> {

            })
        );*/

    }

}