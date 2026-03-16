package com.example.psyche.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;

import java.util.Comparator;
import java.util.Random;

/**
 * Rare proximity urge: weapon slot pull + camera drift + thought.
 */
public final class ProximityEventHandler {
    private static final Random RNG = new Random();
    private static int cooldown = 0;

    private ProximityEventHandler() {}

    public static void tick(MinecraftClient client) {
        if (!ConfigManager.CONFIG.enableProximityUrge || client.player == null || client.world == null) return;
        if (cooldown > 0) {
            cooldown--;
            return;
        }

        ClientPlayerEntity me = client.player;
        PlayerEntity nearest = client.world.getPlayers().stream()
                .filter(p -> p != me)
                .filter(p -> p.squaredDistanceTo(me) < 9.0)
                .min(Comparator.comparingDouble(p -> p.squaredDistanceTo(me)))
                .orElse(null);

        if (nearest == null) return;

        double base = 0.0003 + MentalStateManager.getTension01() * 0.0022;
        if (RNG.nextDouble() < base * ConfigManager.CONFIG.intensity) {
            pullToWeapon(me);
            CameraDisturbance.startProximityDrift(nearest);

            boolean violent = RNG.nextDouble() < (MentalStateManager.getTension01() > 0.68 ? 0.35 : 0.15);
            HudMessageRenderer.pushRandomGeneralThought(client, violent);

            cooldown = 20 * (12 + RNG.nextInt(28));
            MentalStateManager.majorEventCooldownDrop();
        }
    }


    public static boolean debugTrigger(MinecraftClient client) {
        if (client.player == null || client.world == null) return false;
        ClientPlayerEntity me = client.player;
        PlayerEntity nearest = client.world.getPlayers().stream()
                .filter(p -> p != me)
                .filter(p -> p.squaredDistanceTo(me) < 9.0)
                .min(Comparator.comparingDouble(p -> p.squaredDistanceTo(me)))
                .orElse(null);
        if (nearest == null) return false;

        pullToWeapon(me);
        CameraDisturbance.startProximityDrift(nearest);
        HudMessageRenderer.pushRandomGeneralThought(client, true);
        MentalStateManager.majorEventCooldownDrop();
        return true;
    }

    private static void pullToWeapon(ClientPlayerEntity me) {
        int original = me.getInventory().selectedSlot;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = me.getInventory().getStack(i);
            if (stack.getItem() instanceof SwordItem || stack.getItem() instanceof AxeItem) {
                me.getInventory().selectedSlot = i;
                return;
            }
        }
        me.getInventory().selectedSlot = original;
    }
}
