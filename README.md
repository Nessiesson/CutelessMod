# CleanCuteless

Minecraft 1.12.2 · Cleanroom 0.7.12-alpha · Java 25 · Gradle 9.8.1

```sh
nix develop
./gradlew build
./gradlew runClient
```

Use Java 25 for the Gradle JVM; `runClient` picks up the Nix native libraries on NixOS.

Result of the originally developed mod for LiteLoader called UselessMod with a
later created Forge branch UsefulMod. Adds various tweaks/fixes/improvements
using Mixin and Forge, features dedicated hotkeys and auto-generates a separate
list of keybinds for all settings. Ported the chunk display debug tool from
carpet client used for carpet servers.

### Client Commands:

| Command     | Parameters | Function                                                                            |
|-------------|------------|-------------------------------------------------------------------------------------|
| /back       | -          | Teleports you back to the last location where /tp or spectator teleport was used    |
| /ping       | -          | Returns current ping, might take a while after login to normalize                   |
| /repeatlast | -          | Repeats the last send command, alias /; theres also an additional hotkey            |
| /stone      | -          | Places a stone at the current location                                              |

Settings: `gradle.properties`. Install `build/libs/cleancuteless-<version>.jar`.

License: MIT.
