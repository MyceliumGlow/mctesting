package com.example.psyche.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

/**
 * Applies very subtle camera disturbances and proximity drift.
 */
public final class CameraDisturbance {
    private static final Random RNG = new Random();

    private static int microTwitchTicks = 0;
    private static int driftTicks = 0;
    private static PlayerEntity driftTarget;

    private CameraDisturbance() {}

    public static void tick(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null) return;

        if (microTwitchTicks > 0) {
            microTwitchTicks--;
            player.setYaw(player.getYaw() + (float) ((RNG.nextDouble() - 0.5) * 0.9));
            player.setPitch(player.getPitch() + (float) ((RNG.nextDouble() - 0.5) * 0.4));
        }

        if (driftTicks > 0 && driftTarget != null && driftTarget.isAlive()) {
            driftTicks--;
            Vec3d to = driftTarget.getEyePos().subtract(player.getEyePos());
            double targetYaw = Math.toDegrees(Math.atan2(-to.x, to.z));
            double targetPitch = -Math.toDegrees(Math.atan2(to.y, Math.sqrt(to.x * to.x + to.z * to.z)));

            float yawDiff = wrapDegrees((float) (targetYaw - player.getYaw()));
            float pitchDiff = (float) (targetPitch - player.getPitch());

            float strength = (float) (0.045 * ConfigManager.CONFIG.cameraDriftStrength);
            player.setYaw(player.getYaw() + yawDiff * strength);
            player.setPitch(player.getPitch() + pitchDiff * (strength * 0.6f));

            // Occasionally cancel midway to create uncertainty.
            if (driftTicks > 6 && RNG.nextDouble() < 0.03) {
                driftTicks = 0;
            }
        }
    }

    public static void triggerMicroTwitch() {
        microTwitchTicks = 1;
    }

    public static void startProximityDrift(PlayerEntity target) {
        driftTarget = target;
        driftTicks = 20 + RNG.nextInt(20);
    }

    private static float wrapDegrees(float value) {
        float wrapped = value % 360.0f;
        if (wrapped >= 180.0f) wrapped -= 360.0f;
        if (wrapped < -180.0f) wrapped += 360.0f;
        return wrapped;
    }
}
