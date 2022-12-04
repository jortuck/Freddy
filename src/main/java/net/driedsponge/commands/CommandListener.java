package net.driedsponge.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class CommandListener extends ListenerAdapter {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static final HashMap<String, SlashCommand> commands = new HashMap<>();
    public CommandListener() {
        SlashCommand[] cmds = new SlashCommand[]{
                new Skip(),
                new Ping(),
                new Play(),
                new Bug(),
                new Clear(),
                new Help(),
                new JoinLeave(),
                new NowPlaying(),
                new Pause(),
                new Queue(),
                new Shuffle(),
                new Restart()

        };
        ArrayList<SlashCommand> botCommands = new ArrayList<>(Arrays.asList(cmds));
        for (SlashCommand command : botCommands) {
            if(command.getAlias().length > 0){
                for(String name: command.getAlias()){
                    commands.put(name.toLowerCase(), command);
                }
            }else{
                commands.put(command.getName().toLowerCase(), command);
            }
        }
    }
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        SlashCommand command = commands.get(event.getName());
        command.execute(event);
    }
}
