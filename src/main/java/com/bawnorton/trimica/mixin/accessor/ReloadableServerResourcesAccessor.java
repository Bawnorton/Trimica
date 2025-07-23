package com.bawnorton.trimica.mixin.accessor;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.core.Registry;
import net.minecraft.server.ReloadableServerResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.List;

@MixinEnvironment
@Mixin(ReloadableServerResources.class)
public interface ReloadableServerResourcesAccessor {
    @Accessor("postponedTags")
    List<Registry.PendingTags<?>> trimica$postponedTags();
}
