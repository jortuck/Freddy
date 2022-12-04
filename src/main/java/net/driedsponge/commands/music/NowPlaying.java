package net.driedsponge.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.driedsponge.PlayerStore;
import net.driedsponge.TrackScheduler;
import net.driedsponge.VoiceController;
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
        event.deferReply().queue();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.CYAN);
        if(event.getGuild().getAudioManager().isConnected() &&  PlayerStore.get(event.getGuild().getIdLong()) != null){
            VoiceController vc =  PlayerStore.get(event.getGuild().getIdLong());

            String title = String.format("Now Playing in %s",vc.getVoiceChannel().getName());
            event.getHook().sendMessageEmbeds(TrackScheduler.songCard(title,vc.getNowPlaying()).build())
                    .addActionRow(SkipButton.SKIP_BUTTON)
                    .queue();
        }else{
            embedBuilder.setTitle("Nothing is playing.");
            event.getHook().sendMessageEmbeds(embedBuilder.build()).queue();
        }
    }
}
