package net.driedsponge;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

public class Interactions {


    public static void initialize(CommandListUpdateAction commands){

        CommandData[] cmds = {
                Commands.slash("ping","Check the bots ping"),
                Commands.slash("leave","Tells the bot to leave the current voice channel."),
                Commands.slash("play","Tells the bot to play a song. If a song is already playing, it will be added to the queue.")
                        .addOption(
                                OptionType.STRING,
                        "song",
                        "The song to play. This can be a song name, a YouTube link, or YouTube/Spotify playlist link.",
                        true),
                Commands.slash("playskip","Tells the bot to play the song immediately instead of adding it to the queue.")
                        .addOption(
                        OptionType.STRING,
                        "song",
                        "The song to play. This can be a song name, a YouTube link, or YouTube/Spotify playlist link.",
                        true),
                Commands.slash("bug","Report a bug to me!")
                        .addOption(
                                OptionType.STRING,
                        "description",
                        "The description of the bug that is occurring.",
                        true),
                Commands.slash("help","Get a list of all the commands."),
                Commands.slash("pause","Pause the current song."),
                Commands.slash("resume","Resume the current song."),
                Commands.slash("np","Shows the song that is currently playing"),
                Commands.slash("skip","Skips the current song."),
                Commands.slash("queue","Returns the songs in the queue."),
                Commands.slash("shuffle","Shuffles the songs in the queue."),
                Commands.slash("clear","Clears the songs from the queue."),
                Commands.slash("restart","Restarts the song that is currently playing.")
        };
        commands.addCommands(cmds);
        commands.queue();

    }
}
