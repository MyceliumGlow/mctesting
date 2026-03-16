package com.example.psyche.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

/**
 * Dynamic mental tension model (0..maxTension) with contextual drift.
 */
public final class MentalStateManager {
    private static double tension = 6.0;
    private static long sessionTicks = 0;
    private static long idleTicks = 0;
    private static int trackedKills = 0;

    private MentalStateManager() {}

    public static void tick(MinecraftClient client) {
        PlayerEntity player = client.player;
        if (player == null || client.world == null) return;

        sessionTicks++;

        if (Math.abs(player.getVelocity().x) < 0.01 && Math.abs(player.getVelocity().z) < 0.01) {
            idleTicks++;
        } else {
            idleTicks = 0;
        }

        double delta = 0.0;
        int nearbyPlayers = client.world.getPlayers().stream()
                .filter(p -> p != player)
                .filter(p -> p.squaredDistanceTo(player) < 16 * 16)
                .toList().size();

        if (sessionTicks > 20L * 60L * 18L) delta += 0.010;
        if (nearbyPlayers == 0) delta += 0.013;
        if (nearbyPlayers >= 4) delta -= 0.025;
        if (player.getY() < client.world.getSeaLevel() - 10) delta += 0.015;
        if (client.world.getTimeOfDay() % 24000L > 13000L) delta += 0.011;
        BlockPos playerPos = player.getBlockPos();
        if (client.world.getLightLevel(playerPos) <= 4) delta += 0.018;
        if (client.world.getLightLevel(playerPos) >= 13) delta -= 0.015;
        if (idleTicks > 20L * 45L) delta += 0.012;
        if (nearbyPlayers > 0 && nearbyPlayers < 3) delta += 0.006;
        if (trackedKills > 0) delta += 0.004;

        add(delta * ConfigManager.CONFIG.intensity);
    }

    public static void onPlayerKill() {
        trackedKills++;
        add(18.0);
    }

    public static void onPlayerDeathOrSleep() {
        add(-24.0);
    }

    public static void onDisconnect() {
        add(-10.0);
    }

    public static void majorEventCooldownDrop() {
        add(-22.0);
    }

    public static int getKillCount() {
        return trackedKills;
    }

    public static double getTension01() {
        return tension / Math.max(1, ConfigManager.CONFIG.maxTension);
    }

    public static double getTension() {
        return tension;
    }

    public static void setTension(double value) {
        double max = Math.max(20, ConfigManager.CONFIG.maxTension);
        tension = Math.max(0.0, Math.min(max, value));
    }

    public static void addDebug(double value) {
        add(value);
    }

    private static void add(double delta) {
        double max = Math.max(20, ConfigManager.CONFIG.maxTension);
        tension = Math.max(0.0, Math.min(max, tension + delta));
    }
}
