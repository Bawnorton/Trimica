package com.bawnorton.trimica.api;

import net.minecraft.util.TriState;
import net.minecraft.world.item.Item;

public interface CraftingRecipeInterceptor {
    /**
     * Whether or not the item should be used as an addition (trim material)
     * @return {@link TriState#TRUE} if the item should be used as an addition, {@link TriState#FALSE} if it should not, and {@link TriState#DEFAULT} to pass the behaviour to the next interceptor
     * @implNote Default behaviour is {@link TriState#TRUE}
     * @apiNote You are off the main thread here, so avoid interacting with the world.
     */
    TriState allowAsAddition(Item item);

    /**
     * Whether or not the item should be used as a base (trimmed item)
     * @return {@link TriState#TRUE} if the item should be used as a base, {@link TriState#FALSE} if it should not, and {@link TriState#DEFAULT} to pass the behaviour to the next interceptor
     * @implNote Default behaviour is {@link TriState#FALSE}
     * @apiNote You are off the main thread here, so avoid interacting with the world.
     */
    TriState allowAsBase(Item item);
}
