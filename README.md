# Harvester

Harvester is a Sponge Minecraft plugin that restricts block break events for every non-creative players, 
according to the rules described in a configuration file.

## Configuration file

A file named __*harvestables.conf*__ needs to be created into the _config_ folder of the server.
This file is going to describe which blocks can be broke and when they will respawn.  

The file uses HOCON format, and must begin with the array __harvestables__.  
For each block that can be mined, you must specify some data :
* The __type__ field must contain the blocktype id (ex : _minecraft:iron_ore_)
* __respawnmin__ is the minimum amount of minutes before block respawn
* __respawnmax__ is the maximum amount of minutes before block respawn
* _(Optional)_ The __state__ object can contain a list of BlockState to authorize only a given subtype of block to be mined
  
```
harvestables = [
    {
        type: "minecraft:dirt",
        respawnmin: 2,
        respawnmax: 5
    },
    {
        type: "minecraft:stone",
        state {
            variant { value = "andesite" }
        },
        respawnmin: 5,
        respawnmax: 10
    }
]
```
_Following the above example, dirt can be mined and will respawn between 2 and 5 minutes later.
Only andesite type of stone can be broken, and it will respawn between 5 and 10 minutes later._