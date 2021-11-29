package net.driedsponge.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.driedsponge.VoiceController;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class NowPlaying extends ListenerAdapter {
    @Override
    public void onSlashCommand(SlashCommandEvent event){
        if(!event.getName().equals("np")) return;
        event.deferReply().queue();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.CYAN);
        if(event.getGuild().getAudioManager().isConnected() && Play.PLAYERS.get(event.getGuild()) != null){
            VoiceController vc = Play.PLAYERS.get(event.getGuild());
            AudioTrackInfo np = vc.getNowPlaying().getInfo();
            embedBuilder.setTitle(np.title, np.uri);
            embedBuilder.addField("Author",np.author,true);
            embedBuilder.setAuthor("Now Playing in "+vc.getChannel().getName(),event.getJDA().getSelfUser().getAvatarUrl());
            MessageEmbed embed = embedBuilder.build();
            event.getHook().sendMessageEmbeds(embed).queue();
        }else{
            embedBuilder.setTitle("Nothing is playing.");
            event.getHook().sendMessageEmbeds(embedBuilder.build()).queue();
        }
    }
}
