package com.example.psyche.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

import java.util.Random;

/**
 * Plays low-volume positional whispers with contextual likelihood.
 */
public final class WhisperSystem {
    private static final Random RNG = new Random();
    private static int cooldown = 0;

    private WhisperSystem() {}

    public static void tick(MinecraftClient client) {
        if (!ConfigManager.CONFIG.enableWhispers || client.player == null || client.world == null) return;
        if (cooldown > 0) {
            cooldown--;
            return;
        }

        ClientPlayerEntity p = client.player;
        boolean alone = client.world.getPlayers().stream().filter(pl -> pl != p && pl.squaredDistanceTo(p) < 24 * 24).findAny().isEmpty();
        boolean dark = client.world.getLightLevel(p.getBlockPos()) <= 5;
        boolean underground = p.getY() < client.world.getSeaLevel() - 8;

        double chance = 0.00004 + (MentalStateManager.getTension01() * 0.0017);
        if (alone) chance *= 1.8;
        if (dark) chance *= 1.7;
        if (underground) chance *= 1.5;
        chance *= ConfigManager.CONFIG.whisperFrequency;

        if (RNG.nextDouble() < chance) {
            playWhisper(client);
            cooldown = 600 + RNG.nextInt(1000);
        }
    }

    public static void debugTrigger(MinecraftClient client) {
        playWhisper(client);
    }

    private static void playWhisper(MinecraftClient client) {
        ClientPlayerEntity p = client.player;
        if (p == null) return;

        float yaw = p.getYaw() + (RNG.nextBoolean() ? 90f : -90f);
        if (RNG.nextDouble() < 0.25) yaw = p.getYaw() + 180f;
        double ox = -Math.sin(Math.toRadians(yaw)) * (2.2 + RNG.nextDouble() * 1.2);
        double oz = Math.cos(Math.toRadians(yaw)) * (2.2 + RNG.nextDouble() * 1.2);

        SoundEvent[] candidates = {SoundEvents.ENTITY_ALLAY_AMBIENT_WITH_ITEM, SoundEvents.BLOCK_CAVE_VINES_STEP, SoundEvents.ENTITY_PHANTOM_AMBIENT};
        SoundEvent sound = candidates[RNG.nextInt(candidates.length)];

        client.world.playSound(p.getX() + ox, p.getY() + 0.2, p.getZ() + oz,
                sound, SoundCategory.AMBIENT,
                (float) (0.03 + RNG.nextDouble() * 0.04),
                (float) (0.5 + RNG.nextDouble() * 0.4),
                false);
    }
}
