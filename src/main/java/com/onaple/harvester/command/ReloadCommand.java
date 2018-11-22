package com.onaple.harvester.command;

import com.onaple.harvester.Harvester;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.io.IOException;

public class ReloadCommand implements CommandExecutor{

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        try {
            int harvestable = Harvester.getHarvester().loadHarvestable();
            src.sendMessage(writeSucces("Harvestable",harvestable));
        } catch (IOException e) {
            writeError(src,e);
        } catch (ObjectMappingException e) {
            writeError(src,e);
        }
        try {
            int drops = Harvester.getHarvester().loadDrops();
            src.sendMessage(writeSucces("drops.conf", drops));
        } catch (IOException e) {
            writeError(src,e);
        } catch (ObjectMappingException e) {
            writeError(src,e);
        }
        return CommandResult.success();
    }
    private void writeError(CommandSource src,Exception e) {
        src.sendMessage(Text.builder()
                .append(Text.builder("Harvester configuration reload failed. ").color(TextColors.DARK_RED).build())
                .append(Text.builder(e.getMessage()).color(TextColors.RED).build())
                .build());
    }
    private Text writeSucces(String configName,int lineReloaded){
       return Text.builder()
                .append(Text.builder(configName+" configuration successfully reloaded.").color(TextColors.GREEN).build())
                .append(Text.builder(lineReloaded + "line reloaded" ).color(TextColors.GOLD).build())
               .build();
    }
}
