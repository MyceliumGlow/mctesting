package com.example.psyche.client;

import net.minecraft.client.MinecraftClient;

import java.util.Random;

/**
 * Organic probability-based event gate instead of fixed timers.
 */
public final class EventScheduler {
    private static final Random RNG = new Random();
    private static int cooldownTicks = 0;

    private EventScheduler() {}

    public static void tick(MinecraftClient client) {
        if (cooldownTicks > 0) {
            cooldownTicks--;
            return;
        }

        double t = MentalStateManager.getTension01();
        double intensity = ConfigManager.CONFIG.intensity;

        if (RNG.nextDouble() < (0.0008 + t * 0.004) * intensity && ConfigManager.CONFIG.enableIntrusiveThoughts) {
            HudMessageRenderer.pushRandomGeneralThought(client, false);
            cooldownTicks = 80 + RNG.nextInt(200);
        }

        if (RNG.nextDouble() < (t > 0.60 ? 0.0025 : 0.00015) * intensity && ConfigManager.CONFIG.enableInventoryFlicker) {
            HallucinationEffects.triggerInventoryFlicker();
            cooldownTicks = 30 + RNG.nextInt(70);
        }

        if (RNG.nextDouble() < (t > 0.55 ? 0.0019 : 0.0001) * intensity && ConfigManager.CONFIG.enableCameraTwitch) {
            CameraDisturbance.triggerMicroTwitch();
            cooldownTicks = 10 + RNG.nextInt(40);
        }

        if (RNG.nextDouble() < (t > 0.65 ? 0.0015 : 0.00008) * intensity && ConfigManager.CONFIG.enablePeripheralShadow) {
            HallucinationEffects.triggerPeripheralShadow();
            cooldownTicks = 50 + RNG.nextInt(120);
        }
    }
}
