package com.example.psyche.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Center-screen intrusive thought text with fade in/out.
 */
public final class HudMessageRenderer {
    private static final Random RNG = new Random();

    private static final List<String> GENERAL_KEYS = new ArrayList<>();
    private static final List<String> VIOLENT_KEYS = new ArrayList<>();
    private static final List<String> POST_KILL_KEYS = new ArrayList<>();
    private static final List<String> GUILT_KEYS = new ArrayList<>();

    private static String activeKey;
    private static String lastKey;
    private static int age;
    private static int maxAge;
    private static int forcedCrosshairTicks;

    private HudMessageRenderer() {}

    public static void init() {
        for (int i = 1; i <= 7; i++) GENERAL_KEYS.add("psychdisturb.thought.general." + i);
        for (int i = 1; i <= 3; i++) VIOLENT_KEYS.add("psychdisturb.thought.violent." + i);
        for (int i = 1; i <= 4; i++) POST_KILL_KEYS.add("psychdisturb.thought.postkill." + i);
        for (int i = 1; i <= 3; i++) GUILT_KEYS.add("psychdisturb.thought.guilt." + i);
    }

    public static void pushRandomGeneralThought(MinecraftClient client, boolean violentPool) {
        List<String> pool = violentPool ? VIOLENT_KEYS : GENERAL_KEYS;
        pushFromPool(pool, 45, 65);
    }

    public static void pushPostKillThought() {
        pushFromPool(POST_KILL_KEYS, 50, 70);
    }

    public static void pushGuiltMemoryThought() {
        pushFromPool(GUILT_KEYS, 55, 75);
    }

    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        if (!ConfigManager.CONFIG.enableIntrusiveThoughts || activeKey == null) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.textRenderer == null) return;

        age++;
        if (age >= maxAge) {
            activeKey = null;
            return;
        }

        int fadeIn = 12;
        int fadeOut = 18;
        float alpha;
        if (age < fadeIn) alpha = age / (float) fadeIn;
        else if (age > maxAge - fadeOut) alpha = (maxAge - age) / (float) fadeOut;
        else alpha = 1f;

        if ((forcedCrosshairTicks > 0 || (ConfigManager.CONFIG.enableCrosshairDisturbance
                && client.targetedEntity instanceof net.minecraft.entity.player.PlayerEntity
                && MentalStateManager.getTension() > 60
                && RNG.nextDouble() < 0.10))) {
            int cx = context.getScaledWindowWidth() / 2;
            int cy = context.getScaledWindowHeight() / 2;
            context.fill(cx - 2, cy, cx + 3, cy + 1, 0x99AA0000);
            context.fill(cx, cy - 2, cx + 1, cy + 3, 0x99AA0000);
            if (forcedCrosshairTicks > 0) forcedCrosshairTicks--;
        }

        Text msg = Text.translatable(activeKey).formatted(Formatting.DARK_RED);
        int width = client.textRenderer.getWidth(msg);
        int x = (context.getScaledWindowWidth() - width) / 2;
        int y = context.getScaledWindowHeight() / 2 - 14;
        int argb = ((int) (Math.max(0f, Math.min(1f, alpha)) * 190) << 24) | 0x7f0f0f;
        context.drawText(client.textRenderer, msg, x, y, argb, true);
    }

    public static void triggerCrosshairDisturbance() {
        forcedCrosshairTicks = 12;
    }

    private static void pushFromPool(List<String> pool, int minTicks, int maxTicks) {
        if (pool.isEmpty()) return;
        String selected = pool.get(RNG.nextInt(pool.size()));
        if (pool.size() > 1 && selected.equals(lastKey)) {
            selected = pool.get((pool.indexOf(selected) + 1 + RNG.nextInt(pool.size() - 1)) % pool.size());
        }
        activeKey = selected;
        lastKey = selected;
        age = 0;
        maxAge = minTicks + RNG.nextInt(Math.max(1, maxTicks - minTicks));
    }
}
