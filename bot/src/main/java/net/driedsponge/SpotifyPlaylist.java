package net.driedsponge;

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.ArrayList;
import java.util.List;

public class SpotifyPlaylist implements AudioPlaylist {
    private String name;
    private String url;
    private ArrayList<AudioTrack> songs;
    public SpotifyPlaylist(String name,String url){
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<AudioTrack> getTracks() {
        return this.songs;
    }

    @Override
    public AudioTrack getSelectedTrack() {
        return null;
    }

    @Override
    public boolean isSearchResult() {
        return false;
    }


    public void setTracks(ArrayList<AudioTrack> songs) {
        this.songs = songs;
    }
}
