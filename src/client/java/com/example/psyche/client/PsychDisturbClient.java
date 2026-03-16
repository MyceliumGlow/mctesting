package com.example.psyche.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

/**
 * Main entrypoint for the purely client-side psychological disturbance systems.
 */
public class PsychDisturbClient implements ClientModInitializer {
    public static final String MOD_ID = "psychdisturb";

    private boolean wasSleeping;
    private boolean wasDead;

    @Override
    public void onInitializeClient() {
        ConfigManager.load();
        HudMessageRenderer.init();
        NameHallucinationRenderer.init();
        DebugCommandManager.init();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) {
                return;
            }

            if (client.player.isSleeping() && !wasSleeping) {
                MentalStateManager.onPlayerDeathOrSleep();
            }
            wasSleeping = client.player.isSleeping();

            boolean dead = client.player.isDead();
            if (dead && !wasDead) {
                MentalStateManager.onPlayerDeathOrSleep();
            }
            wasDead = dead;

            MentalStateManager.tick(client);
            EventScheduler.tick(client);
            WhisperSystem.tick(client);
            CameraDisturbance.tick(client);
            ProximityEventHandler.tick(client);
            HallucinationEffects.tick(client);
            CombatMemoryManager.tick(client);
        });

        HudRenderCallback.EVENT.register((drawContext, tickDelta) ->
                HudMessageRenderer.render(drawContext, tickDelta));

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> MentalStateManager.onDisconnect());
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> ConfigManager.save());
    }
}
