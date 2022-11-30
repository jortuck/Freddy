package net.driedsponge;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
public class Song {
    private AudioTrack track;
    private SlashCommandInteractionEvent event;
    private String thumbnail = null;
    public Song(AudioTrack track, SlashCommandInteractionEvent event){
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

    public String getYoutubeUrl() {
        return this.track.getInfo().uri;
    }

    public String getRealURL() {
        return this.track.getInfo().uri;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
