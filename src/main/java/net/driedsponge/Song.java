package net.driedsponge;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class Song {
    private AudioTrack track;
    private SlashCommandEvent event;
    public Song(AudioTrack track, SlashCommandEvent event){
        this.track = track;
        this.event = event;
    }

    public AudioTrack getTrack() {
        return track;
    }

    public SlashCommandEvent getEvent() {
        return event;
    }
}
