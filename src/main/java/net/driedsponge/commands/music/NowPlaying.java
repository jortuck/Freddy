package net.driedsponge.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.driedsponge.*;
import net.driedsponge.buttons.SkipButton;
import net.driedsponge.commands.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import java.awt.*;

public class NowPlaying extends SlashCommand {
    public NowPlaying() {
        super("np");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event){
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Main.PRIMARY_COLOR);
        if(event.getGuild().getAudioManager().isConnected() &&  PlayerStore.get(event.getGuild().getIdLong()) != null){
            VoiceController vc =  PlayerStore.get(event.getGuild().getIdLong());
            Song np = vc.getNowPlaying();
            String title = String.format("Now Playing in %s",vc.getVoiceChannel().getName());
            event.replyEmbeds(TrackScheduler.songCard(title,np)
                            .addField("Time Left", TrackScheduler.duration(np.getTrack().getDuration() - np.getTrack().getPosition()),true)
                            .build())
                    .addActionRow(SkipButton.SKIP_BUTTON)
                    .queue();
        }else{
            embedBuilder.setTitle("Nothing is playing.");
            event.replyEmbeds(embedBuilder.build()).setEphemeral(true).queue();
        }
    }
}
