# Harvester

Harvester is a Sponge Minecraft plugin that restricts block break events for every non-creative players, 
according to the rules described in a configuration file.

## Configuration file

A file named __*harvestables.conf*__ need to be created into the _config_ folder of the server.
This file is going to describe which blocks can be broke and when they will respawn.  

The file uses HOCON format, and must begin with the array __harvestables__.  
For each block that can be mined, you must specify the __type__ using the id (ex : _minecraft:iron_ore_), 
and the respawn times in minutes : __respawnmin__ and __respawnmax__.  
The given block will respawn after a random time between the two values.
  
```
harvestables = [
    {
        type: "minecraft:dirt",
        respawnmin: 2,
        respawnmax: 5
    },
    {
        type: "minecraft:stone",
        respawnmin: 5,
        respawnmax: 10
    }
]
```
_Following the above example, dirt can be mined and will respawn between 2 and 5 minutes later.
Stone will respawn between 5 and 10 minutes later._