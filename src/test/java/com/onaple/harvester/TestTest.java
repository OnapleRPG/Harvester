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

@RunWith(MinecraftRunner.class)
public class TestTest extends BaseTest {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    public TestTest(TestUtils testUtils) {
        super(testUtils);
    }

    @Test
    public void testOneShotEventListenerException() throws Throwable {
        expectedEx.expect(AssertionError.class);
        expectedEx.expectMessage(CoreMatchers.containsString("Got message: One shot"));

        this.testUtils.listenOneShot(() -> {
            this.testUtils.getClient().lookAt(new Vector3d(0, -1, 0));
            this.testUtils.getClient().holdLeftClick(true);
            try {
                this.testUtils.getClient().wait(1000);
            } catch (InterruptedException e) {
                this.testUtils.getClient().sendMessage("One shot");
            }
        }, new StandaloneEventListener<>(MessageChannelEvent.Chat.class, (MessageChannelEvent.Chat event) -> Assert.fail("Got message: " + event.getRawMessage().toPlain())));
    }

    @Test
    public void testPermanentEventListenerException() throws Throwable {
        expectedEx.expect(AssertionError.class);
        expectedEx.expectMessage(CoreMatchers.containsString("Got message: Permanent message"));

        this.testUtils.listen(new StandaloneEventListener<>(MessageChannelEvent.Chat.class, (MessageChannelEvent.Chat event) -> Assert.fail("Got message: " + event.getRawMessage().toPlain())));
        this.testUtils.getClient().sendMessage("Permanent message");
    }

    @Test
    public void testTimeoutEventListenerException() throws Throwable {
        expectedEx.expect(AssertionError.class);
        expectedEx.expectMessage(CoreMatchers.containsString("Got message: Timeout message"));

        this.testUtils.listenTimeout(() -> {
                    this.testUtils.getClient().sendMessage("Timeout message");
                }, new StandaloneEventListener<>(MessageChannelEvent.Chat.class,
                        (MessageChannelEvent.Chat event) -> Assert.fail("Got message: " + event.getRawMessage().toPlain())),
                20);
    }

}