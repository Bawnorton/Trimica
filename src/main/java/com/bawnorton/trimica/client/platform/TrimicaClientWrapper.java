package com.bawnorton.trimica.client.platform;

//? if fabric {
import com.bawnorton.trimica.client.TrimicaClient;
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import net.fabricmc.api.ClientModInitializer;

@Entrypoint
public final class TrimicaClientWrapper implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		TrimicaClient.init();
	}
}
//?} else if neoforge {
/*import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.client.TrimicaClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = Trimica.MOD_ID, dist = Dist.CLIENT)
public final class TrimicaClientWrapper {
	public TrimicaClientWrapper() {
		TrimicaClient.init();
	}
}
*///?}
