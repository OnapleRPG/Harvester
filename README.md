# Harvester  ![Github Action](https://github.com/OnapleRPG/Harvester/actions/workflows/gradle.yml/badge.svg) ![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=com.onaple%3AHarvester&metric=reliability_rating) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![sponge version](https://img.shields.io/badge/sponge-7.2.0-blue.svg)](https://www.spongepowered.org/) 


Harvester is a Sponge Minecraft plugin that restricts block break events for every non-creative players, 
according to the rules described in a configuration file.
## Permission
you can active harvester on sevral player with a permission. People who have : `harvester.block.breaking` will be able to freely break any blocks. People in creative Mode can break any blocks too.

## Configuration file

Different configuration files exist to declare how Harvester should change block breaks and drop events.  

### Global configuration

Global configuration is used for a few settings. It should be set within a __*global.conf*__ file if you wish to override the default behavior, existing within the _harvester_ folder within the _config_.   

You can set worlds where you want that Harvester is enabled in the global.conf. Be careful world name are case sensitive. (by default, every worlds will be taken into account)  
You can also prevent block growth by setting the blockGrowth boolean to false. Useful if you don't want crops to grow (like if you use different textures for crops stages for non related blocks). If equal to true or non specified, crops will grow normally.  
```
Worlds = ["worldName1","WorldName2"]
blockGrowth = true
``` 

### Harvest regeneration

A file named __*harvestables.conf*__ needs to be created into the _harvester_ folder of the server config folder.
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
Only the andesite variant of stone can be broken, and it will respawn between 5 and 10 minutes later._

### Harvest drops

A file named __*drops.conf*__ must exist into the _harvester_ folder within the _config_folder of the server.
It is used to define the items that will come out of the mined blocks.

The file also uses HOCON and contains two arrays :  
* The __default__ array contains the resources that will not disappear once mined. By default, any vanilla drop will not happen.
It contains a list of the resources that will drop as is on a vanilla server.
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

### Reload command
There is a command that reloads the configuration files without needing to restart the server : **/harvester reload**.  
Permission : *harvester.command.reload*
