package com.bawnorton.trimica_tests.client;

import com.bawnorton.trimica.platform.fabric.test.TrimicaTests;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;

@SuppressWarnings("UnstableApiUsage")
public class TrimicaClientGameTest implements FabricClientGameTest {
    @Override
    public void runTest(ClientGameTestContext context) {
        TrimicaTests.runTests(context);
    }
}
