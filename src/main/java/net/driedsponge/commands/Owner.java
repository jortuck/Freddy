package net.driedsponge.commands;

import net.driedsponge.Main;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class Owner extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        if(!event.getAuthor().getId().equals(Main.OWNER_ID)) return;
        if(event.getMessage().getContentRaw().startsWith("!initialize")){
            initialize(event.getJDA());
            event.getMessage().reply("Initializing slash commands. May take up to one hour.").queue();
        }
    }
    private void initialize(JDA jda){
        jda.upsertCommand("ping","Check the bots ping.").queue(); // Implemented
        jda.upsertCommand("leave","Tells the bot to leave the current voice channel.").queue(); // Implemented

        jda.upsertCommand("play","Tell the bot to play a song.")
                .addOption(OptionType.STRING,"song","The name of the song to play.",true).queue(); // Implemented
        jda.upsertCommand("playskip","Tells the bot to play the song immediately instead of adding it to the queue.")
                .addOption(OptionType.STRING,"song","The name of the song to play.",true).queue();

        jda.upsertCommand("pause","Pause the current song").queue(); // Implemented
        jda.upsertCommand("resume","Resume the current song").queue(); // Implemented
        jda.upsertCommand("np","Shows the song that is currently playing.").queue(); // Implemented
        jda.upsertCommand("skip","Skips the current song.").queue();
        jda.upsertCommand("queue","Returns the songs in the queue.").queue(); // Implemented
    }
}
