package com.bawnorton.trimica.compat.elytratrims;

import com.bawnorton.trimica.client.TrimicaClient;
import com.bawnorton.trimica.client.palette.TrimPalette;
import com.bawnorton.trimica.client.texture.DynamicTrimTextureAtlasSprite;
import com.bawnorton.trimica.client.texture.RuntimeTrimAtlas;
import com.bawnorton.trimica.client.texture.RuntimeTrimAtlases;
import com.bawnorton.trimica.compat.Compat;
import com.bawnorton.trimica.item.component.AdditionalTrims;
import com.bawnorton.trimica.item.component.MaterialAdditions;
import com.google.auto.service.AutoService;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.kikugie.elytratrims.api.ETClientInitializer;
import dev.kikugie.elytratrims.api.render.ETRenderParameters;
import dev.kikugie.elytratrims.api.render.ETRenderingAPI;
import dev.kikugie.elytratrims.api.render.ETRenderingAPIUtils;
import dev.kikugie.elytratrims.render.impl.ETTrimsRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

//? if >=1.21.10 {
import net.minecraft.client.renderer.SubmitNodeCollector;
//?}

//? if fabric {
@dev.kikugie.fletching_table.annotation.fabric.Entrypoint("elytratrims-client")
//?} else {
/*@AutoService(ETClientInitializer.class)
 *///?}
public class ElytraTrimsClientEntrypoint implements ETClientInitializer {
	@Override
	public void onInitializeClientET() {
		ETRenderingAPI.wrapRenderCall(ETTrimsRenderer.type, this::renderWithTrimica);
	}

	//? if >=1.21.10 {
	private boolean renderWithTrimica(ETRenderParameters parameters, SubmitNodeCollector nodeCollector, BiFunction<ETRenderParameters, SubmitNodeCollector, Boolean> original) {
		Supplier<Boolean> callOriginal = () -> original.apply(parameters, nodeCollector);
		ItemStack stack = parameters.stack();
		List<ArmorTrim> trims = AdditionalTrims.getAllTrims(stack);
		if (trims.isEmpty()) return callOriginal.get();

		ClientLevel level = Minecraft.getInstance().level;
		if (level == null) return callOriginal.get();

		for (ArmorTrim trim : trims) {
			renderWithTrimicaInternal(level, stack, trim, parameters, callOriginal::get, (sprite, light) ->
					nodeCollector.order(parameters.order().getAndIncrement())
							.submitModel(
									parameters.elytra(),
									parameters.object(),
									parameters.matrices(),
									parameters.render(),
									light,
									parameters.overlay(),
									parameters.color(),
									sprite,
									parameters.outline(),
									null
							));
		}
		return true;
	}

	//?} else {
	/*private boolean renderWithTrimica(ETRenderParameters parameters, Function<ETRenderParameters, Boolean> original) {
		Supplier<Boolean> callOriginal = () -> original.apply(parameters);
		ItemStack stack = parameters.stack();
		List<ArmorTrim> trims = AdditionalTrims.getAllTrims(stack);
		if (trims.isEmpty()) return callOriginal.get();

		ClientLevel level = Minecraft.getInstance().level;
		if (level == null) return callOriginal.get();

		for (ArmorTrim trim : trims) {
			renderWithTrimicaInternal(level, stack, trim, parameters, callOriginal::get, (sprite, light) -> {
				VertexConsumer vertexConsumer = sprite.wrap(ItemRenderer.getArmorFoilBuffer(parameters.source(), sprite.getRenderType(), stack.hasFoil()));
				Model elytra = parameters.elytra();
				elytra.renderToBuffer(parameters.matrices(), vertexConsumer, light, OverlayTexture.NO_OVERLAY, parameters.color());
			});
		}
		return true;
	}
	*///?}

	private void renderWithTrimicaInternal(
			ClientLevel level,
			ItemStack stack,
			ArmorTrim trim,
			ETRenderParameters parameters,
			Runnable original,
			Renderer renderer
	) {
		RuntimeTrimAtlases atlases = TrimicaClient.getRuntimeAtlases();
		TrimMaterial material = trim.material().value();
		RuntimeTrimAtlas atlas = atlases.getEquipmentAtlas(level, material, EquipmentClientInfo.LayerType.WINGS);
		if (atlas == null) return;

		ResourceLocation overlayLocation = trim.layerAssetId(EquipmentClientInfo.LayerType.WINGS.trimAssetPrefix(), EquipmentAssets.ELYTRA);
		MaterialAdditions additions;
		if (MaterialAdditions.enableMaterialAdditions) {
			additions = stack.getOrDefault(MaterialAdditions.TYPE, MaterialAdditions.NONE);
			overlayLocation = additions.apply(overlayLocation);
		}

		TrimPattern pattern = trim.pattern().value();
		DynamicTrimTextureAtlasSprite newSprite = atlas.getSprite(stack, pattern, overlayLocation);
		TrimPalette palette = newSprite.getPalette();
		if (palette == null) {
			original.run();
			return;
		}

		if (palette.isAnimated()) {
			Compat.ifSodiumPresent(compat -> compat.markSpriteAsActive(newSprite));
		}
		int light = palette.isEmissive() ? LightTexture.FULL_BRIGHT : ETRenderingAPIUtils.getEffectiveLight(parameters);
	}

	interface Renderer {
		void render(DynamicTrimTextureAtlasSprite sprite, int light);
	}
}