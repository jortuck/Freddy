package net.driedsponge;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Song {
    private AudioTrack track;
    private SlashCommandInteractionEvent event;
    private String thumbnail = null;
    public Song(AudioTrack track, SlashCommandInteractionEvent event){
        this.track = track;
        this.event = event;
        try {
            List<NameValuePair> query = URLEncodedUtils.parse(new URI(track.getInfo().uri), StandardCharsets.UTF_8);
            this.thumbnail = "https://img.youtube.com/vi/"+query.get(0).getValue()+"/hqdefault.jpg";

        } catch ( URISyntaxException e) {
                this.thumbnail = "https://img.youtube.com/vi/8PbZjHyKiyo/hqdefault.jpg";
        }
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
