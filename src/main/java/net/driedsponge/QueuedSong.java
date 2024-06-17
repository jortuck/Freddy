package net.driedsponge;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public final class QueuedSong {
    private AudioTrack track;
    private SlashCommandInteractionEvent event;
    public QueuedSong(AudioTrack track, SlashCommandInteractionEvent event){
        this.track = track;
        this.event = event;
    }
    public AudioTrack getTrack() {
        return track;
    }
    public Member getRequester(){
        return this.event.getMember();
    }
    public AudioTrackInfo getInfo(){
        return this.track.getInfo();
    }
    public SlashCommandInteractionEvent getEvent() {
        return event;
    }
}
