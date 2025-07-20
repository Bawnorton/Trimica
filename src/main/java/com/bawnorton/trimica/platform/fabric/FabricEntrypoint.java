//? if fabric {
package com.bawnorton.trimica.platform.fabric;

import com.bawnorton.trimica.Trimica;
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import net.fabricmc.api.ModInitializer;

@Entrypoint
public final class FabricEntrypoint implements ModInitializer {
    @Override
    public void onInitialize() {
        Trimica.initialize();
    }
}
//?}
