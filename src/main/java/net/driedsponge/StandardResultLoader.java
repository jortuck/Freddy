package net.driedsponge;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.concurrent.BlockingQueue;

public class StandardResultLoader implements AudioLoadResultHandler {

    private BlockingQueue<AudioTrack> queue;
    private AudioPlayer player;

    public StandardResultLoader(BlockingQueue<AudioTrack> queue, AudioPlayer player){
        this.queue = queue;
        this.player = player;
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        if(player.getPlayingTrack() == null){
            player.playTrack(track);
        }else {
            queue.offer(track);
        }
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        // ignore playlist from search results
        if(playlist.isSearchResult()){
            trackLoaded(playlist.getTracks().getFirst());
        }else{
            for(AudioTrack track : playlist.getTracks()){
                queue.offer(track);
            }
        }
    }

    @Override
    public void noMatches() {
        System.out.println("No matches");
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        System.out.println("exception ?");
        System.out.println(exception.getMessage());
    }
}
