package com.bawnorton.trimica.networking;

//? if fabric {
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

//?} else {
/*import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber
*///?}
public class Networking {
	//? if fabric {
	public static void init() {
	}

	public static <T extends CustomPacketPayload> void send(ServerPlayer player, T payload) {
		ServerPlayNetworking.send(player, payload);
	}
	//?} else {
	/*public static void init() {
	}

	@SubscribeEvent
	public static void registerPackets(RegisterPayloadHandlersEvent event) {
		PayloadRegistrar registrar = event.registrar("1");
	}

	public static <T extends CustomPacketPayload> void send(ServerPlayer player, T payload) {
		PacketDistributor.sendToPlayer(player, payload);
	}
	*///?}
}