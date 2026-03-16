package com.example.psyche.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Tracks local combat impressions to trigger delayed post-kill/guilt thoughts.
 */
public final class CombatMemoryManager {
    private static final Random RNG = new Random();
    private static final Map<UUID, Long> recentPlayerHits = new HashMap<>();

    private static long postKillDueTick = -1;
    private static long guiltDueTick = -1;

    private CombatMemoryManager() {}

    public static void onAttack(Entity target) {
        if (target instanceof PlayerEntity player) {
            recentPlayerHits.put(player.getUuid(), player.getEntityWorld().getTime());
        }
    }

    public static void tick(MinecraftClient client) {
        if (client.player == null || client.world == null) return;
        long now = client.world.getTime();

        // Heuristic: if a recently hit player is now dead, assume possible local kill.
        recentPlayerHits.entrySet().removeIf(e -> {
            PlayerEntity p = client.world.getPlayerByUuid(e.getKey());
            if (p == null) return true;
            if (now - e.getValue() > 20 * 20L) return true;
            if (!p.isAlive()) {
                registerLikelyKill(now);
                return true;
            }
            return false;
        });

        if (postKillDueTick > 0 && now >= postKillDueTick) {
            HudMessageRenderer.pushPostKillThought();
            postKillDueTick = -1;
        }
        if (guiltDueTick > 0 && now >= guiltDueTick) {
            HudMessageRenderer.pushGuiltMemoryThought();
            guiltDueTick = -1;
        }
    }

    private static void registerLikelyKill(long now) {
        MentalStateManager.onPlayerKill();
        postKillDueTick = now + 40 + RNG.nextInt(60); // 2-5 seconds
        guiltDueTick = now + (20L * 60L * (18 + RNG.nextInt(60))); // ~18-78 minutes later
    }
}
