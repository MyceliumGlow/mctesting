package com.example.psyche.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Comparator;
import java.util.Random;
import java.util.UUID;

/**
 * Chooses brief client-side alias windows for target player names.
 */
public final class NameHallucinationRenderer {
    private static final Random RNG = new Random();
    private static UUID hauntedPlayer;
    private static long endTick;

    private NameHallucinationRenderer() {}

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!ConfigManager.CONFIG.enableNameHallucination || client.player == null || client.world == null) return;
            long now = client.world.getTime();
            if (hauntedPlayer != null && now > endTick) {
                hauntedPlayer = null;
            }

            if (hauntedPlayer != null) return;
            if (MentalStateManager.getTension() < 60) return;

            PlayerEntity me = client.player;
            PlayerEntity nearest = client.world.getPlayers().stream()
                    .filter(p -> p != me)
                    .filter(p -> p.squaredDistanceTo(me) < 36)
                    .min(Comparator.comparingDouble(p -> p.squaredDistanceTo(me)))
                    .orElse(null);

            if (nearest == null) return;
            if (client.targetedEntity != nearest) return;

            if (RNG.nextDouble() < 0.0022 * ConfigManager.CONFIG.intensity) {
                hauntedPlayer = nearest.getUuid();
                endTick = now + 10 + RNG.nextInt(20);
            }
        });
    }

    public static void triggerFor(PlayerEntity player, int ticks) {
        hauntedPlayer = player.getUuid();
        long now = player.getEntityWorld().getTime();
        endTick = now + Math.max(1, ticks);
    }

    public static boolean shouldHallucinate(UUID playerUuid) {
        return hauntedPlayer != null && hauntedPlayer.equals(playerUuid);
    }

    public static String getRandomHallucinationKey() {
        int n = 1 + RNG.nextInt(4);
        return "psychdisturb.namehallucination." + n;
    }
}
