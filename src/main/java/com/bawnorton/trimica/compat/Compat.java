package com.bawnorton.trimica.compat;

import com.bawnorton.trimica.platform.Platform;
import java.util.Optional;

public class Compat {
    public static Optional<SodiumCompat> getSodiumCompat() {
        if(Platform.isModLoaded("sodium")) {
            return Optional.of(new SodiumCompat());
        }
        return Optional.empty();
    }
}
