//? if fabric {
package com.bawnorton.trimica.platform.fabric;

import com.bawnorton.trimica.Trimica;
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

@Entrypoint
public final class FabricEntrypoint implements ModInitializer {
    @Override
    public void onInitialize() {
        Trimica.initialize();

	    ServerLifecycleEvents.SERVER_STOPPING.register(server -> Trimica.refreshEverything(false, false));
    }
}
//?}
