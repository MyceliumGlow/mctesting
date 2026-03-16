package com.example.psyche.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Lightweight JSON config for event tuning.
 */
public final class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("psychdisturb.json");

    public static Config CONFIG = new Config();

    private ConfigManager() {}

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                CONFIG = GSON.fromJson(reader, Config.class);
                if (CONFIG == null) {
                    CONFIG = new Config();
                }
            } catch (IOException ignored) {
                CONFIG = new Config();
            }
        }
        save();
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(CONFIG, writer);
            }
        } catch (IOException ignored) {
        }
    }

    public static class Config {
        public double intensity = 1.0;
        public double whisperFrequency = 1.0;
        public double cameraDriftStrength = 1.0;
        public int maxTension = 100;

        public boolean enableWhispers = true;
        public boolean enableCameraTwitch = true;
        public boolean enableIntrusiveThoughts = true;
        public boolean enableProximityUrge = true;
        public boolean enableNameHallucination = true;
        public boolean enableInventoryFlicker = true;
        public boolean enablePeripheralShadow = true;
        public boolean enableCrosshairDisturbance = true;
    }
}
