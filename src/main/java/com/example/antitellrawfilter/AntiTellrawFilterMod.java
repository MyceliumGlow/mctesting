package com.example.antitellrawfilter;

import com.mojang.brigadier.ParseResults;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Locale;

@Mod(AntiTellrawFilterMod.MOD_ID)
public class AntiTellrawFilterMod {
    public static final String MOD_ID = "anti_tellraw_filter";

    private static final List<String> BLOCKED_PATTERNS = List.of(
            "minestrator.com",
            "[myboxfree]"
    );

    public AntiTellrawFilterMod() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onCommand(CommandEvent event) {
        ParseResults<CommandSourceStack> parseResults = event.getParseResults();
        String commandText = parseResults.getReader().getString();
        String normalized = stripLeadingSlash(commandText).toLowerCase(Locale.ROOT);

        if (!normalized.startsWith("tellraw ")) {
            return;
        }

        boolean containsBlockedPattern = BLOCKED_PATTERNS.stream()
                .map(pattern -> pattern.toLowerCase(Locale.ROOT))
                .anyMatch(normalized::contains);

        if (!containsBlockedPattern) {
            return;
        }

        event.setCanceled(true);

        CommandSourceStack source = event.getParseResults().getContext().getSource();
        source.sendFailure(Component.literal("Blocked tellraw: contains filtered advertising text."));
    }

    private static String stripLeadingSlash(String commandText) {
        if (commandText.startsWith("/")) {
            return commandText.substring(1);
        }
        return commandText;
    }
}
