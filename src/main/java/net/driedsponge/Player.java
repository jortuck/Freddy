package net.driedsponge;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;

import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Player {
    private final Guild guild;
    private TrackSchedule trackScheduler;
    private AudioPlayer player;
    private TrackSchedule schedule;
    public Player(Guild guild){
        this.guild = guild;
        player = Main.PLAYER_MANAGER.createPlayer();
        schedule = new TrackSchedule();
        player.addListener(schedule);
    }

    public void play(String song){

    }

    private class TrackSchedule extends AudioEventAdapter {
        private final BlockingQueue<AudioTrack> queue;
        public TrackSchedule(){
            this.queue = new LinkedBlockingQueue<>();
        }
    }


}
