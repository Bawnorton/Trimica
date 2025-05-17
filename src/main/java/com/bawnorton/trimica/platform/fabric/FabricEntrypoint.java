//? if fabric {
package com.bawnorton.trimica.platform.fabric;

import com.bawnorton.trimica.Trimica;
import net.fabricmc.api.ModInitializer;

public final class FabricEntrypoint implements ModInitializer {
    @Override
    public void onInitialize() {
        Trimica.initialize();
    }
}
//?}
