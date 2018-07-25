package com.onaple.harvester;

import com.flowpowered.math.vector.Vector3d;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.mctester.api.junit.MinecraftRunner;
import org.spongepowered.mctester.internal.BaseTest;
import org.spongepowered.mctester.internal.event.StandaloneEventListener;
import org.spongepowered.mctester.junit.TestUtils;
import scala.collection.concurrent.Debug;

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

}