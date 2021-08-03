# Harvester  ![Github Action](https://github.com/OnapleRPG/Harvester/actions/workflows/gradle.yml/badge.svg) ![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=com.onaple%3AHarvester&metric=reliability_rating) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![sponge version](https://img.shields.io/badge/sponge-7.2.0-blue.svg)](https://www.spongepowered.org/) 


Harvester is a Sponge Minecraft plugin that restricts block break events for every non-creative players, according to the rules described in a configuration file. It handles block reappearance, so you can have a mining system with regenerating resources.  

## Installation

On a sponge server 1.12, download our [latest release](https://github.com/OnapleRPG/Harvester/releases), and drop the file into your server's `mods/` folder. Restart your server.  
Default configuration files should be generated into your `/config` folder, and your server logs should include a line about Harvester being loaded.  

## Configuration file

Different configuration files exist to declare how Harvester should change block breaks and drop events.  
They can be found in your `config/harvester/` folder.  

### Global configuration

Global configuration is used for a few settings. It should be set within a __*global.conf*__ file if you wish to override the default behavior.  

- You can set `worlds` where you want Harvester to be enabled within the global.conf. Please note that world name are case sensitive. (by default, every worlds will be taken into account)  
- You can also prevent block growth by setting the `blockGrowth` boolean to false. Useful if you don't want crops to grow (for instance if you use the crops different stage textures for multiple purpose). If equal to true or non specified, crops will grow normally.  
```
worlds = ["worldName1","WorldName2"]
blockGrowth = true
``` 

### Harvest regeneration

A file named __*harvestables.conf*__ is required within the _harvester_ config folder.  
This file is going to describe which blocks can be broken, and when they will respawn.  

The file uses HOCON format, and must begin with the array __harvestables__.  
For each block that can be mined, you must specify some data :  
* The __type__ field must contain the blocktype id (ex : _minecraft:iron_ore_)
* __respawnmin__ is the minimum amount of minutes before block respawn
* __respawnmax__ is the maximum amount of minutes before block respawn
* _(Optional)_ The __state__ object can contain a list of BlockState to authorize only a given subtype of block to be mined (like log/stone variants)  
  
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
Only the andesite variant of stone can be broken, and it will respawn between 5 and 10 minutes later._

### Harvest drops

The config file named __*drops.conf*__  is used to define the items that will come out of the mined blocks.

The file also uses HOCON and contains two arrays :  
* The __default__ array contains the resources that will use vanilla drops. By default, all vanilla drops won't happen.  
It can be used in addition to *harvest_items* if you'd like a resource to drop its normal loot plus something else.  
* The __harvest_items__ array contains the data specifying which item we want to be dropped for a block
    * __type__ is the name of the block affected
    * _(Optional)_ The __state__ object, if present, specify that only a subtype of a block is affected
    * _(Optional)_ The __item_name__, when present, defines an item by its name
    * _(Optional)_ The __item_ref__, when present, mean that the plugin will try to communicate with Itemizer plugin to 
    retrieve a configured item identified by its id
    * _(Optional)_ The __pool_ref__, when present, will fetch an item (or nothing) from an itempool from the plugin Itemizer

```
default = [
   	"minecraft:dirt",
   	"minecraft:stone"
]
harvest_items = [
   {
       type: "minecraft:stone",
       state {
           variant { value = "diorite" }
       },
       item_name: "minecraft:cobblestone",
       item_ref: 2,
       pool_ref: 1
   }
]
```
_Following the above example, dirt and stone are going to drop their default drop, but diorite stone will drop a
cobblestone block, the item number 2 of Itemizer, and an item from the first Itemizer pool, as well as its default drop (since diorite is a type of stone)._  

### Commands and permissions

* Granting a player with the `harvester.block.breaking` permission will allow him to break blocks normally, without being restricted by the plugin.  
  People in creative mode can also break blocks normally.  
* The `/harvester reload` command will reloads configuration files without needing to restart the server.  
  Permission: *harvester.command.reload*  
