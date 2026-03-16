package com.example.psyche.client;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.util.Comparator;

/**
 * Client-side only debug/trigger commands for testing every disturbance event.
 */
public final class DebugCommandManager {
    private DebugCommandManager() {}

    public static void init() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                ClientCommandManager.literal("psychdebug")
                        .then(ClientCommandManager.literal("tension")
                                .then(ClientCommandManager.literal("get")
                                        .executes(ctx -> {
                                            double tension = MentalStateManager.getTension();
                                            ctx.getSource().sendFeedback(Text.literal("Mental tension: " + String.format("%.2f", tension)));
                                            return 1;
                                        }))
                                .then(ClientCommandManager.literal("set")
                                        .then(ClientCommandManager.argument("value", DoubleArgumentType.doubleArg(0, 1000))
                                                .executes(ctx -> {
                                                    double value = DoubleArgumentType.getDouble(ctx, "value");
                                                    MentalStateManager.setTension(value);
                                                    ctx.getSource().sendFeedback(Text.literal("Mental tension set to " + String.format("%.2f", MentalStateManager.getTension())));
                                                    return 1;
                                                })))
                                .then(ClientCommandManager.literal("add")
                                        .then(ClientCommandManager.argument("value", DoubleArgumentType.doubleArg(-1000, 1000))
                                                .executes(ctx -> {
                                                    double value = DoubleArgumentType.getDouble(ctx, "value");
                                                    MentalStateManager.addDebug(value);
                                                    ctx.getSource().sendFeedback(Text.literal("Mental tension now " + String.format("%.2f", MentalStateManager.getTension())));
                                                    return 1;
                                                }))))
                        .then(ClientCommandManager.literal("trigger")
                                .then(ClientCommandManager.literal("whisper").executes(ctx -> {
                                    WhisperSystem.debugTrigger(ctx.getSource().getClient());
                                    return feedback(ctx, "Triggered whisper.");
                                }))
                                .then(ClientCommandManager.literal("camera_twitch").executes(ctx -> {
                                    CameraDisturbance.triggerMicroTwitch();
                                    return feedback(ctx, "Triggered camera twitch.");
                                }))
                                .then(ClientCommandManager.literal("intrusive").executes(ctx -> {
                                    HudMessageRenderer.pushRandomGeneralThought(ctx.getSource().getClient(), false);
                                    return feedback(ctx, "Triggered intrusive thought.");
                                }))
                                .then(ClientCommandManager.literal("violent").executes(ctx -> {
                                    HudMessageRenderer.pushRandomGeneralThought(ctx.getSource().getClient(), true);
                                    return feedback(ctx, "Triggered violent impulse thought.");
                                }))
                                .then(ClientCommandManager.literal("postkill").executes(ctx -> {
                                    HudMessageRenderer.pushPostKillThought();
                                    return feedback(ctx, "Triggered post-kill thought.");
                                }))
                                .then(ClientCommandManager.literal("guilt").executes(ctx -> {
                                    HudMessageRenderer.pushGuiltMemoryThought();
                                    return feedback(ctx, "Triggered guilt memory thought.");
                                }))
                                .then(ClientCommandManager.literal("inventory_flicker").executes(ctx -> {
                                    HallucinationEffects.triggerInventoryFlicker();
                                    return feedback(ctx, "Triggered inventory flicker.");
                                }))
                                .then(ClientCommandManager.literal("peripheral_shadow").executes(ctx -> {
                                    HallucinationEffects.triggerPeripheralShadow();
                                    return feedback(ctx, "Triggered peripheral shadow.");
                                }))
                                .then(ClientCommandManager.literal("phantom_breathing").executes(ctx -> {
                                    HallucinationEffects.triggerPhantomBreathing();
                                    return feedback(ctx, "Triggered phantom breathing.");
                                }))
                                .then(ClientCommandManager.literal("false_footstep").executes(ctx -> {
                                    HallucinationEffects.triggerFalseFootstep();
                                    return feedback(ctx, "Triggered false footstep.");
                                }))
                                .then(ClientCommandManager.literal("crosshair").executes(ctx -> {
                                    HudMessageRenderer.triggerCrosshairDisturbance();
                                    return feedback(ctx, "Triggered crosshair disturbance.");
                                }))
                                .then(ClientCommandManager.literal("name_hallucination").executes(ctx -> {
                                    PlayerEntity target = findNearestOtherPlayer(ctx.getSource().getClient());
                                    if (target == null) {
                                        return feedback(ctx, "No nearby player found for name hallucination.");
                                    }
                                    NameHallucinationRenderer.triggerFor(target, 25);
                                    return feedback(ctx, "Triggered name hallucination for " + target.getName().getString() + ".");
                                }))
                                .then(ClientCommandManager.literal("proximity_urge").executes(ctx -> {
                                    boolean ok = ProximityEventHandler.debugTrigger(ctx.getSource().getClient());
                                    return feedback(ctx, ok ? "Triggered proximity urge." : "No player within 3 blocks for proximity urge.");
                                })))
                        .then(ClientCommandManager.literal("help").executes(ctx ->
                                feedback(ctx, "Use /psychdebug trigger <event> or /psychdebug tension <get|set|add>")))
        ));
    }

    private static int feedback(CommandContext<FabricClientCommandSource> ctx, String message) {
        ctx.getSource().sendFeedback(Text.literal(message));
        return 1;
    }

    private static PlayerEntity findNearestOtherPlayer(MinecraftClient client) {
        ClientPlayerEntity me = client.player;
        if (me == null || client.world == null) return null;
        return client.world.getPlayers().stream()
                .filter(p -> p != me)
                .filter(p -> p.squaredDistanceTo(me) < 16 * 16)
                .min(Comparator.comparingDouble(p -> p.squaredDistanceTo(me)))
                .orElse(null);
    }
}
