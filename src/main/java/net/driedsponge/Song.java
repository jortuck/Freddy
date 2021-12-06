package net.driedsponge;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public interface Song {
    public AudioTrack getTrack();

    public Member getRequester();

    public AudioTrackInfo getInfo();

    public SlashCommandEvent getEvent();
    public String getYoutubeUrl();
    public String getRealURL();
}


