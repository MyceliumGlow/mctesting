# Anti Tellraw Filter (Forge 1.20.1, server-side)

This mod blocks `tellraw` commands that contain ad text.

Blocked by default:
- `minestrator.com`
- `[myboxfree]`

It is **server-side only** (players do **not** need to install it).

---

## Quick answer: “How do I use this?”

If you are new, follow this exact order:

1. Build the mod jar.
2. Put the jar in your server `mods/` folder.
3. Restart the server.
4. Test with a command that contains blocked text.

That’s it.

---

## 1) Requirements

- A **Forge 1.20.1 server**.
- Java 17.
- This project source code (to build the jar).

> Important: the mod version here targets Forge for Minecraft `1.20.1`.

---

## 2) Build the jar

From this project folder, run:

```bash
gradle build
```

If build succeeds, your jar is created in:

```text
build/libs/
```

You should get something like:

- `anti-tellraw-filter-1.0.0.jar`

---

## 3) Install on your server

1. Stop your Minecraft server.
2. Upload `anti-tellraw-filter-1.0.0.jar` to the server's `mods` folder.
3. Start the server again.
4. Check `latest.log` for the mod id: `anti_tellraw_filter`.

---

## 4) Test that it works

Run one of these commands in server console (or as OP):

```mcfunction
/tellraw @a {"text":"visit minestrator.com"}
```

```mcfunction
/tellraw @a {"text":"[myboxfree] hello"}
```

Expected result:
- command is blocked
- sender gets: `Blocked tellraw: contains filtered advertising text.`

A normal tellraw without those words should still work:

```mcfunction
/tellraw @a {"text":"Welcome to the server!"}
```

---

## 5) Change blocked words (optional)

Edit this file:

- `src/main/java/com/example/antitellrawfilter/AntiTellrawFilterMod.java`

Update `BLOCKED_PATTERNS`:

```java
private static final List<String> BLOCKED_PATTERNS = List.of(
        "minestrator.com",
        "[myboxfree]",
        "another-word-you-want-to-block"
);
```

Then build again and replace the old jar in your server `mods` folder.

---

## Troubleshooting

### Build fails with repository/network errors
Some hosting/dev environments block Maven/Forge downloads. Try building on your local PC with normal internet.

### Players can't join
This mod is configured with `displayTest="IGNORE_ALL_VERSION"`, so clients should not need the mod. If players still cannot join, verify:
- server is really Forge 1.20.1
- mod jar is only on server (not required on client)
- no other mod is causing mismatch
