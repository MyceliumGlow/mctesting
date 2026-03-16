package com.example.psyche.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import java.util.Random;

/**
 * Misc subtle effects: inventory flicker, shadows, breathing, and false footsteps.
 */
public final class HallucinationEffects {
    private static final Random RNG = new Random();
    private static int flickerTicks = 0;
    private static int originalSlot = -1;

    private HallucinationEffects() {}

    public static void tick(MinecraftClient client) {
        ClientPlayerEntity p = client.player;
        if (p == null || client.world == null) return;

        if (flickerTicks > 0) {
            flickerTicks--;
            if (flickerTicks == 1 && originalSlot >= 0) {
                p.getInventory().setSelectedSlot(originalSlot);
                originalSlot = -1;
            }
        }

        long tod = client.world.getTimeOfDay() % 24000L;
        boolean night = tod > 13000L;
        boolean still = Math.abs(p.getVelocity().x) < 0.01 && Math.abs(p.getVelocity().z) < 0.01;
        if (night && still && RNG.nextDouble() < 0.0005 * ConfigManager.CONFIG.intensity) {
            triggerPhantomBreathing();
        }

        if (RNG.nextDouble() < 0.00025 * ConfigManager.CONFIG.intensity) {
            triggerFalseFootstep();
        }
    }


    public static void triggerPhantomBreathing() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;
        ClientPlayerEntity p = client.player;
        client.world.playSound(p, p.getX() + (RNG.nextBoolean() ? 1.5 : -1.5), p.getY(), p.getZ(),
                SoundEvents.ENTITY_PLAYER_BREATH, SoundCategory.AMBIENT, 0.05f, 0.85f);
    }

    public static void triggerFalseFootstep() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;
        ClientPlayerEntity p = client.player;
        double yaw = Math.toRadians(p.getYaw() + 180 + (RNG.nextDouble() - 0.5) * 30);
        client.world.playSound(p, p.getX() - Math.sin(yaw) * 2.1, p.getY(), p.getZ() + Math.cos(yaw) * 2.1,
                SoundEvents.BLOCK_STONE_STEP, SoundCategory.AMBIENT, 0.08f, 0.6f);
    }

    public static void triggerInventoryFlicker() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        ClientPlayerEntity p = client.player;
        originalSlot = p.getInventory().getSelectedSlot();
        int other = (originalSlot + 1 + RNG.nextInt(8)) % 9;
        p.getInventory().setSelectedSlot(other);
        flickerTicks = 3;
    }

    public static void triggerPeripheralShadow() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;
        ClientPlayerEntity p = client.player;

        double side = RNG.nextBoolean() ? 75 : -75;
        double yaw = Math.toRadians(p.getYaw() + side);
        double x = p.getX() - Math.sin(yaw) * 2.2;
        double z = p.getZ() + Math.cos(yaw) * 2.2;

        for (int i = 0; i < 4; i++) {
            client.world.addParticle(ParticleTypes.SMOKE, false, false,
                    x, p.getY() + 0.1 + i * 0.18, z,
                    0.0, 0.01, 0.0);
        }
    }
}
