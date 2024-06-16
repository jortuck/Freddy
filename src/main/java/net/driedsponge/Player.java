package net.driedsponge;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;

import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;

import java.net.URI;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public final class Player {
    public static final HashMap<String, Player> PLAYERS = new HashMap<>();

    private final Guild guild;
    private AudioPlayer player;
    private AudioChannelUnion voiceChannel;
    private GuildMessageChannel textChannel;
    private TrackEvents schedule;
    private final BlockingQueue<AudioTrack> queue;

    /**
     * @param channel
     * @throws IllegalArgumentException –
     * If the provided channel was null.
     * If the provided channel is not part of the Guild that the current audio connection is connected to.
     * @throws UnsupportedOperationException – If audio is disabled due to an internal JDA error
     * @throws net.dv8tion.jda.api.exceptions.InsufficientPermissionException –
     * If the currently logged in account does not have the Permission VOICE_CONNECT
     * If the currently logged in account does not have the Permission VOICE_MOVE_OTHERS and the user limit has been exceeded!
     */
    public Player(AudioChannelUnion channel){
        this.guild = channel.getGuild();
        this.voiceChannel = channel;
        this.player = Main.PLAYER_MANAGER.createPlayer();
        this.schedule = new TrackEvents();
        player.addListener(schedule);
        guild.getAudioManager().openAudioConnection(channel);
        guild.getAudioManager().setSendingHandler(new MusicHandler(player));
        this.queue = new LinkedBlockingQueue<>();
        this.textChannel = channel.asGuildMessageChannel();

    }

    public void play(String song, SlashCommandInteractionEvent event){
        Main.PLAYER_MANAGER.loadItem(song,new StandardResultLoader(event));
    }

    public void play(URI song, SlashCommandInteractionEvent event) throws BadHostException{
        if(Main.getAllowedHosts().contains(song.getHost())){
            if(song.getHost().equals("open.spotify.com")){
                String[] paths = song.getPath().split("/", 3);
                if (paths[1].equals("playlist") && paths[2] != null) {
                    try {
                        SpotifyPlaylist playlist = SpotifyPlaylist.fromId(paths[2]);
                        for(PlaylistTrack spotify: playlist.getSongs()){
                            Main.PLAYER_MANAGER.loadItem("ytmsearch:"+spotify.getTrack().getName(), new SpotifyResultLoader(event));
                        }
                        event.getHook().sendMessage("good spot li nk").queue();
                    } catch (Exception e){
                        throw new BadHostException("Make sure it's a public playlist");
                    }
                } else {
                    throw new BadHostException("Invalid spotify Playlist link");
                }
            }else{
                Main.PLAYER_MANAGER.loadItem(song.toString(),new StandardResultLoader(event));
            }
        }else{
            throw new BadHostException("The URL must be valid YouTube URL or Spotify Playlist Link");
        }
    }

    public AudioChannelUnion getVoiceChannel(){
        return voiceChannel;
    }

    public void updateChannel(AudioChannelUnion channel){
        this.guild.getAudioManager().openAudioConnection(channel);
        this.voiceChannel = channel;
    }

    public void playNow(String song){

    }

    private final class TrackEvents extends AudioEventAdapter {
        @Override
        public void onTrackStart(AudioPlayer player, AudioTrack track) {
            voiceChannel.asVoiceChannel().modifyStatus(":musical_note: "+player.getPlayingTrack()
                    .getInfo().title).queue();
        }
        @Override
        public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
            if(queue.isEmpty()){
                textChannel.sendMessage("Queue is empty! No more songs to play!").queue();
            }else{
                player.playTrack(queue.poll());
                textChannel.sendMessage("Starting new song!").queue();
            }
        }
    }

    private final class SpotifyResultLoader implements AudioLoadResultHandler{
        private SlashCommandInteractionEvent event;
        public SpotifyResultLoader(SlashCommandInteractionEvent event){
            this.event = event;
        }
        @Override
        public void trackLoaded(AudioTrack track) {
            if(player.getPlayingTrack() == null){
                event.getHook().sendMessage("playing first song").queue();
            }else{
                queue.offer(track);
            }
        }

        @Override
        public void playlistLoaded(AudioPlaylist playlist) {
            trackLoaded(playlist.getTracks().getFirst());
        }

        @Override
        public void noMatches() {
            // do nothing
        }

        @Override
        public void loadFailed(FriendlyException exception) {
            // do nothing
        }
    }

    private final class StandardResultLoader implements AudioLoadResultHandler {
        private SlashCommandInteractionEvent event;
        public StandardResultLoader(SlashCommandInteractionEvent event){
            this.event = event;
        }

        @Override
        public void trackLoaded(AudioTrack track) {
            if(player.getPlayingTrack() == null){
                player.playTrack(track);
                event.getHook().sendMessage("Playing your song now!").queue();
            }else {
                queue.offer(track);
                event.getHook().sendMessage(track.getInfo().title+" added to queue!").queue();
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
            event.getHook().sendMessage("no matches for the desired song").queue();
        }

        @Override
        public void loadFailed(FriendlyException exception) {
            event.getHook().sendMessage(exception.getMessage()).queue();
        }
    }

}
