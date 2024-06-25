package net.driedsponge.commands;

import net.driedsponge.commands.music.*;
import net.driedsponge.commands.util.Bug;
import net.driedsponge.commands.util.Help;
import net.driedsponge.commands.util.Ping;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public final class CommandListener extends ListenerAdapter {
    private static final HashMap<String, SlashCommand> commands = new HashMap<>();
    private static final Set<CommandData> commandData = new HashSet<>();
    public static final CommandListener INSTANCE = new CommandListener();
    private CommandListener() {
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
            for (CommandData data : command.getCommand()) {
                commands.put(data.getName(), command);
                commandData.add(data);
            }
        }
    }

    public static void upsertCommands(CommandListUpdateAction jda){
        jda.addCommands(commandData).queue();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        SlashCommand command = commands.get(event.getName());
        command.execute(event);
    }
}
