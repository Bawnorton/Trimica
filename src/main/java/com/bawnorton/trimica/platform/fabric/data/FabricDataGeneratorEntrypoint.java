//? if fabric {
package com.bawnorton.trimica.platform.fabric.data;

import com.bawnorton.trimica.platform.fabric.data.provider.TrimicaModelProvider;
import com.bawnorton.trimica.platform.fabric.data.provider.TrimicaTagProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public final class FabricDataGeneratorEntrypoint implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(TrimicaModelProvider::new);
        pack.addProvider(TrimicaTagProvider::new);
    }
}
//?}