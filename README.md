# Psychological Disturbances (Fabric 1.21.x)

A **purely client-side** Fabric mod that introduces subtle, rare, and psychologically unsettling disturbances:
- whispers
- intrusive thought overlays
- slight camera disturbances
- proximity urge events
- player name hallucination flickers
- post-kill and long-delay guilt memory thoughts

No packets are sent, and no server behavior is modified.

## Project Structure

```text
psychdisturb/
├─ build.gradle
├─ gradle.properties
├─ settings.gradle
├─ src/
│  ├─ main/
│  │  └─ resources/
│  │     ├─ fabric.mod.json
│  │     └─ assets/psychdisturb/
│  │        ├─ lang/en_us.json
│  │        ├─ lang/tr_tr.json
│  │        ├─ sounds.json
│  │        └─ sounds/ (optional custom OGG files)
│  └─ client/
│     ├─ java/com/example/psyche/client/
│     │  ├─ PsychDisturbClient.java
│     │  ├─ ConfigManager.java
│     │  ├─ MentalStateManager.java
│     │  ├─ EventScheduler.java
│     │  ├─ WhisperSystem.java
│     │  ├─ CameraDisturbance.java
│     │  ├─ ProximityEventHandler.java
│     │  ├─ HallucinationEffects.java
│     │  ├─ HudMessageRenderer.java
│     │  ├─ NameHallucinationRenderer.java
│     │  ├─ CombatMemoryManager.java
│     │  └─ DebugCommandManager.java
│     └─ java/com/example/psyche/client/mixin/
│        └─ ClientPlayerInteractionManagerMixin.java
│
└─ src/client/resources/psychdisturb.client.mixins.json
```

## Build

1. Ensure Java 21 is installed.
2. Generate wrapper (optional if not already present):
   - `gradle wrapper`
3. Build:
   - `./gradlew build`

## Run in Development

- `./gradlew runClient`

## Config

Config file path:
- `config/psychdisturb.json`

You can tweak:
- event intensity
- whisper frequency
- camera drift strength
- max tension level
- toggles for each major effect

## Notes

- This mod uses translation keys for all thought/hallucination strings.
- English and Turkish localizations are included.
- Sound assets are represented with examples and can be replaced with custom OGG files.
- Note: non-asset documentation files (like `README.txt`) should not be placed under `assets/...` to avoid resource-pack path warnings.


## Client Debug Commands

All debug commands are **client-side only** and intended for testing event behavior in-game:

- `/psychdebug help`
- `/psychdebug tension get`
- `/psychdebug tension set <value>`
- `/psychdebug tension add <value>`
- `/psychdebug trigger whisper`
- `/psychdebug trigger camera_twitch`
- `/psychdebug trigger intrusive`
- `/psychdebug trigger violent`
- `/psychdebug trigger postkill`
- `/psychdebug trigger guilt`
- `/psychdebug trigger inventory_flicker`
- `/psychdebug trigger peripheral_shadow`
- `/psychdebug trigger phantom_breathing`
- `/psychdebug trigger false_footstep`
- `/psychdebug trigger crosshair`
- `/psychdebug trigger name_hallucination`
- `/psychdebug trigger proximity_urge`
