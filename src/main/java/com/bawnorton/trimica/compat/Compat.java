package com.bawnorton.trimica.compat;

import com.bawnorton.trimica.platform.Platform;
import java.util.function.Consumer;

public class Compat {
    public static void ifSodiumPresent(Consumer<SodiumCompat> consumer) {
        if(Platform.isModLoaded("sodium")) {
            consumer.accept(new SodiumCompat());
        }
    }
}
