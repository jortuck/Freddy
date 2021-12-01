package net.driedsponge.commands;

import net.driedsponge.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.awt.*;

public class Owner extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        if(!event.getAuthor().getId().equals(Main.OWNER_ID)) return;
        if(event.getMessage().getContentRaw().startsWith("!initialize")){
            initialize(event.getJDA());
            event.getMessage().reply("Initializing slash commands. May take up to one hour to propagate.").queue();
        }else if(event.getMessage().getContentRaw().startsWith("!statistics")){
            statistics(event);
        }else if(event.getMessage().getContentRaw().startsWith("!guildlist")){
            guildList(event);
        }
    }

    private void guildList(MessageReceivedEvent event){
        JDA jda = event.getJDA();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.CYAN);
        embedBuilder.setTitle(jda.getSelfUser().getName() + " Guilds");
        StringBuilder builder = new StringBuilder();
        builder.append("```");
        jda.getGuilds().forEach(guild -> {
            builder.append("\n ").append(guild.getName()).append(" (").append(guild.getId()).append(")");
        });
        builder.append("\n```");
        embedBuilder.setDescription(builder.toString());
        event.getMessage().replyEmbeds(embedBuilder.build()).queue();
    }

    private void statistics(MessageReceivedEvent event){
        JDA jda = event.getJDA();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.CYAN);
        embedBuilder.setTitle(jda.getSelfUser().getName() + " Statistics");
        embedBuilder.addField("Guilds",String.valueOf(jda.getGuilds().size()),true);
        embedBuilder.addField("Currently Entertaining",String.valueOf(Play.PLAYERS.size()),true);
        event.getMessage().replyEmbeds(embedBuilder.build()).queue();
    }

    private void initialize(JDA jda){
        jda.upsertCommand("ping","Check the bots ping.").queue(); // Implemented
        jda.upsertCommand("leave","Tells the bot to leave the current voice channel.").queue(); // Implemented

        jda.upsertCommand("play","Tell the bot to play a song.")
                .addOption(OptionType.STRING,"song","The name of the song to play.",true).queue(); // Implemented
        jda.upsertCommand("playskip","Tells the bot to play the song immediately instead of adding it to the queue.")
                .addOption(OptionType.STRING,"song","The name of the song to play.",true).queue();
        jda.upsertCommand("bug","Report a bug to me!")
                .addOption(OptionType.STRING,"description","The description of the bug that is occurring. Please include details on how to recreate it.",true).queue();

        jda.upsertCommand("help","Get a list of all the commands.").queue();
        jda.upsertCommand("pause","Pause the current song.").queue(); // Implemented
        jda.upsertCommand("resume","Resume the current song.").queue(); // Implemented
        jda.upsertCommand("np","Shows the song that is currently playing.").queue(); // Implemented
        jda.upsertCommand("skip","Skips the current song.").queue();
        jda.upsertCommand("queue","Returns the songs in the queue.").queue(); // Implemented
    }
}
