# Cuteless Mod

### Forge Mod

Result of the originally developed mod for LiteLoader called UselessMod with a later created Forge branch UsefulMod

Adds various tweaks/fixes/improvements using Mixin and Forge

Features some dedicated hotkeys and auto-generates a seperate list of keybinds for all settings.

Ported the chunk display debug tool from carpet client used for carpet servers. **Minimap currently not working**

### Client Commands:

| Command     | Parameters | Function                                                                            |
|-------------|------------|-------------------------------------------------------------------------------------|
| /back       | -          | Teleports you back to the last location where /tp or spectator teleport was used    |
| /ping       | -          | Returns current ping, might take a while after login to normalize                   |
| /repeatlast | -          | Repeats the last send command, alias /; theres also an additional hotkey            |
| /stone      | -          | Places a stone at the current location                                              |
| /undo       | \<index>   | Undo for /fill /clone and WorldEdit operations, default index 0 is latest operation |
| /undo       | toggle     | Toggle if operations are recorded for undo, defaults to true on startup             |

### WorldEdit Commands:

| Command          | Parameters                                                                         | Function                                                                                                               |
|------------------|------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------|
| /brush           | \<brush name> \<block> \[dataValue:state]                                          | Binds brush to currently held item                                                                                     |
| /cancel          | -                                                                                  | Cancels all running operations                                                                                         |
| /center          | \[block] \[dataValue:state]                                                        | Marks the center of the selection with default block glowstone, min. 1x1 max. 2x2                                      |
| /copyhere        | \[move selection:true, false]                                                      | Copies selection to current player position                                                                            |
| /count           | \<exclusive: false, true> \<block> \<dataValue:state>                              | Counts given block exclusive or inclusive                                                                              |
| /cyl             | \<block> \<dataValue:state> <radius> \[height]                                     | Generates a cylinder around 1x1 selection with given block and radius                                                  |
| /fillinventories | \<item> \<count> \[damage]                                                         | Fills all hoppers, droppers and chests in selection with specified item (Existing content gets deleted!)               |
| /drain           | \<radius>                                                                          | Drains water in player location up to default radius 100                                                               |
| /fixslabs        | -                                                                                  | Changes doubleslabs to full blocks and old oak slabs to planks                                                         |
| /flip            | -                                                                                  | Flips selection in the currently facing direction                                                                      |
| /floodfill       | \<block> \<dataValue:state> \[radius]                                              | Performs flood fill with given block at feet level with default radius 100                                             |
| /hcyl            | \<block> \<dataValue:state> \<radius> \[height]                                    | Generates a hollow cylinder around 1x1 selection with given block and radius                                           |
| /hollow          | \<block> \<dataValue:state> \[wall thickness]                                      | Generates a hollow cube around selection with default thickness of 1 block                                             |
| /hsphere         | \<block> \<dataValue:state> \<radius>                                              | Generates a hollow sphere centered on 1x1 selection with given block                                                   |
| /line            | \[block] \[dataValue:state]                                                        | Generates a line with default block glowstone from position 0 to 1                                                     |
| /move            | \<amount>                                                                          | Moves selection with blocks in currently facing direction by amount blocks                                             |
| /outlinefill     | \<block> \<dataValue:state> \<height> \[radius]                                    | Performs a 2d flood fill with given block at feet level with default radius 100, fills a pillar of height at each spot |
| /perimetervolume | \[radius] \[bottom y]                                                              | Counts isNormalCube() blocks in 2d flood fill columns down to y0, similar to /outlinefill                              |
| /polygon         | \<block> \<dataValue:state> \<radius> \<edges> \[halfstep:true, false]             | Generates polygon with given block, radius, edges can be rotated by 360/edges degrees                                  |
| /pos             | \<0, 1>                                                                            | Sets position 0 or 1 at current location                                                                               |
| /randomize       | \<percentage> \<block> \[dataValue:state],\<block> \[dataValue:state]...           | Fills selected area with array of blocks by given percentage                                                           |
| /replace         | \<block> \<dataValue:state> \<block> \[dataValue:state]                            | Replaces one block by another, * for replacing any state                                                               |
| /runbrush        | \<mask block> \<dataValue:state> <brush name> \<block> \[dataValue:state]          | Runs brush on the specified mask block in selection                                                                    |
| /selection       | \<move, expand> \<up, down, north, east, south, west> \[amount]                    | Expands or moves current selection in given direction by amount                                                        |
| /selection       | \clear                                                                             | Clears current selection                                                                                               |
| /set             | \<block> \<dataValue:state>                                                        | Fills the selection with given block                                                                                   |
| /size            | -                                                                                  | Outputs the current selection size and volume                                                                          |
| /sphere          | \<block> \<dataValue:state> \<radius>                                              | Generates a sphere centered on 1x1 selection with given block                                                          |
| /stack           | \<count> \[blocks in between] \[masked: true, false] \[move selection:true, false] | Repeats selection count times in currently facing direction with optional airgap                                       |
| /stackquarter    | \[blocks in between]                                                               | Mirrors one quarter to either a horizontal or vertical diagonal                                                        |
| /upscale         | \<factor>                                                                          | Upscales the selection by filling cubes of factor^2 size                                                               |
| /walls           | \<block> \<dataValue:state> \[wall thickness]                                      | Generates walls around selection with a default thickness of 1 block                                                   |

**Warning:** Some commands involving heavier calculations like sphere or flood fill are threaded and can take some time
before progress shows up

**Warning:** Only /move and /stack use the /clone command in the background and properly copy Tile Entitys, all other
commands **including /undo** do not copy Tile Entity data

Undo also records the vanilla commands /fill and /clone

The commands /selection clear and /ping can be executed in all gamemodes

Be aware of potential rounding errors when generating circles, polygons or spheres due to math and rasterisation...

