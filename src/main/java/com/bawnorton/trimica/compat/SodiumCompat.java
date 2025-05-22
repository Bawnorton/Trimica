package com.bawnorton.trimica.compat;

import net.caffeinemc.mods.sodium.client.SodiumClientMod;

public class SodiumCompat {
    public void disableAnimationPeformance() {
        SodiumClientMod.options().performance.animateOnlyVisibleTextures = false;
    }
}
