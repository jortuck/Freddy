package net.driedsponge.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.driedsponge.VoiceController;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class NowPlaying extends ListenerAdapter {
    @Override
    public void onSlashCommand(SlashCommandEvent event){
        if(!event.getName().equals("np")) return;
        if(event.getGuild().getAudioManager().isConnected()){
            VoiceController vc = Play.PLAYERS.get(event.getGuild());
            AudioTrackInfo np = vc.getNowPlaying().getInfo();
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(np.title, np.uri);
            embedBuilder.setColor(Color.CYAN);
            embedBuilder.addField("Author",np.author,true);

        }else{
            event.reply("Nothing is playing.").queue();
        }
    }
}
