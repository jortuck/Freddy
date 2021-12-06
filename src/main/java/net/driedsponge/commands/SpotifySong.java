package net.driedsponge.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.driedsponge.Song;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class SpotifySong implements Song {
    private String name;
    private AudioTrack track;
    private SlashCommandEvent event;

    public SpotifySong(String name){

    }

    @Override
    public AudioTrack getTrack() {
        return null;
    }

    @Override
    public Member getRequester() {
        return null;
    }

    @Override
    public AudioTrackInfo getInfo() {
        return null;
    }

    @Override
    public SlashCommandEvent getEvent() {
        return this.event;
    }

    @Override
    public String getYoutubeUrl() {
        return null;
    }

    @Override
    public String getRealURL() {
        return null;
    }
}
