package com.onaple.harvester.exception;

public class PluginNotFoundException extends Exception {

    public PluginNotFoundException(String pluginName) {
        super("The Plugin " + pluginName + " cannot be found. make sure the plugin is present in the server's mods folder.");
    }
}
