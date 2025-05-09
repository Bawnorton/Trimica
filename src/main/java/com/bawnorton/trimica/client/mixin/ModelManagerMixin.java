package com.bawnorton.trimica.client.mixin;

import com.bawnorton.trimica.client.TrimicaClient;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.resources.model.ModelManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ModelManager.class)
public abstract class ModelManagerMixin {
    @ModifyReturnValue(
            //? if neoforge {
            /*method = "discoverModelDependencies(Ljava/util/Map;Lnet/minecraft/client/resources/model/BlockStateModelLoader$LoadedModels;Lnet/minecraft/client/resources/model/ClientItemInfoLoader$LoadedClientInfos;Lnet/neoforged/neoforge/client/model/standalone/StandaloneModelLoader$LoadedModels;)Lnet/minecraft/client/resources/model/ModelManager$ResolvedModels;",
            *///?} else {
            method = "discoverModelDependencies",
            //?}
            at = @At("RETURN")
    )
    private static ModelManager.ResolvedModels captureResolvedModels(ModelManager.ResolvedModels resolvedModels) {
        TrimicaClient.getItemModelFactory().setResolvedModels(resolvedModels);
        return resolvedModels;
    }
}
