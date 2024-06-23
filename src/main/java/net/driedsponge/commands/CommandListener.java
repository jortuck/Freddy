package net.driedsponge.commands;

import net.driedsponge.commands.music.*;
import net.driedsponge.commands.util.Bug;
import net.driedsponge.commands.util.Help;
import net.driedsponge.commands.util.Ping;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public final class CommandListener extends ListenerAdapter {
    private static final HashMap<String, SlashCommand> commands = new HashMap<>();

    public CommandListener() {
        SlashCommand[] botCommands = new SlashCommand[]{
                Skip.INSTANCE,
                Ping.INSTANCE,
                Play.INSTANCE,
                Bug.INSTANCE,
                Clear.INSTANCE,
                Help.INSTANCE,
                Leave.INSTANCE,
                NowPlaying.INSTANCE,
                Pause.INSTANCE,
                Queue.INSTANCE,
                Shuffle.INSTANCE,
                Restart.INSTANCE,
                Remove.INSTANCE,
                Seek.INSTANCE,
                Join.INSTANCE

        };
        for (SlashCommand command : botCommands) {
            if (command.getAlias().length > 0) {
                for (String name : command.getAlias()) {
                    commands.put(name.toLowerCase(), command);
                }
            } else {
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
