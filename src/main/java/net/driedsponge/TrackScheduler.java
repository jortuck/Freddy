package net.driedsponge;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.driedsponge.commands.Play;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import javax.sound.midi.Track;
import java.util.ArrayList;
import java.util.HashMap;

public class TrackScheduler extends AudioEventAdapter {
    public ArrayList<Song> queue = new ArrayList<Song>();
    public VoiceController vc;
    public TrackScheduler(VoiceController vc){
        this.vc = vc;
    }
    @Override
    public void onPlayerPause(AudioPlayer player) {
        // Player was paused
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        // Player was resumed
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        // A track started playing
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            if(queue.size() > 0){
                Song song = queue.get(0);
                this.vc.setNowPlaying(song.getTrack());
                player.playTrack(song.getTrack());
                song.getEvent().getHook().sendMessage("Now playing **"+song.getTrack().getInfo().title+"**").queue();
                queue.remove(0);
            }else{
                Guild guild = vc.getGuild();
                vc.getMsgChannel().sendMessage("No more songs to playing. Leaving now!").queue();
                vc.leave();
                Play.PLAYERS.remove(guild);


            }

        }

        // endReason == FINISHED: A track finished or died by an exception (mayStartNext = true).
        // endReason == LOAD_FAILED: Loading of a track failed (mayStartNext = true).
        // endReason == STOPPED: The player was stopped.
        // endReason == REPLACED: Another track started playing while this had not finished
        // endReason == CLEANUP: Player hasn't been queried for a while, if you want you can put a
        //                       clone of this back to your queue
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        // An already playing track threw an exception (track end event will still be received separately)
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        // Audio track has been unable to provide us any audio, might want to just start a new track
    }

    public void queue(AudioTrack track, SlashCommandEvent event){
        this.queue.add(new Song(track,event));
    }
}
