package net.driedsponge;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;

public class VoiceController {
    private final String guildId;
    private final JDA jda;
    private String channel;
    private AudioPlayerManager playerManager;
    private TrackScheduler trackScheduler;
    private AudioPlayer player;
    private Song nowPlaying;
    private String msgChannel;

    /**
     * Constructor for new voice controller.
     * @param guild The guild in which the voice controller is for.
     * @param voiceChannel The voice channel the bot is bound to.
     * @param textChannel The text channel the bot is bound to.
     */
    public VoiceController(Guild guild, VoiceChannel voiceChannel, TextChannel textChannel){
        this.guildId = guild.getId();
        this.jda = voiceChannel.getJDA();
        this.channel = voiceChannel.getId();
        this.msgChannel = textChannel.getId();
        AudioManager audioManager = guild.getAudioManager();

        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        YoutubeAudioSourceManager youtubeAudioSourceManager = new YoutubeAudioSourceManager();

        this.playerManager = playerManager;

        AudioSourceManagers.registerRemoteSources(playerManager);

        AudioPlayer player = playerManager.createPlayer();
        this.player = player;
        audioManager.setSendingHandler(new MusicHandler(player));
        TrackScheduler trackScheduler = new TrackScheduler(this);
        this.trackScheduler = trackScheduler;
        player.addListener(trackScheduler);

    }

    public void setNowPlaying(Song nowPlaying) {
        this.nowPlaying = nowPlaying;
    }

    /**
     * Tells the bot to join the call.
     */
    public void join() throws PermissionException{
            this.getGuild().getAudioManager().openAudioConnection(this.getVoiceChannel());
            // This has the bot deafening itself so people don't think it's listening.
            this.getGuild().getAudioManager().setSelfDeafened(true);
    }


    public void play(String song, SlashCommandInteractionEvent event, boolean now){
        playerManager.loadItem(song, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                Song song = new Song(track, event);
                if(now){
                    trackScheduler.startNewTrack(song);
                }else{
                    if(nowPlaying == null){
                        trackScheduler.queue(song,true);
                    }else{
                        trackScheduler.queue(song,true);
                    }
                }


            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if(!playlist.isSearchResult()){
                    trackScheduler.queue(playlist,event);
                }else{
                    trackLoaded(playlist.getTracks().get(0));
                }

            }

            @Override
            public void noMatches() {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("We could not find that song!");
                embed.setColor(Color.RED);
                embed.setDescription("**If you know the song exists, try putting a direct link to the YouTube video!**");
                event.getHook().sendMessageEmbeds(embed.build()).setEphemeral(true).queue();
            }

            @Override
            public void loadFailed(FriendlyException throwable) {
                event.getHook().sendMessage("That song failed to load. I don't know why.").queue();
            }
        });
    }

    public void skip(Member member){
        this.trackScheduler.startNewTrack(member);
    }

    public Song getNowPlaying() {
        return nowPlaying;
    }

    public VoiceChannel getVoiceChannel() {
        return this.jda.getVoiceChannelById(this.channel);
    }

    public TextChannel getTextChannel() {
        return this.jda.getTextChannelById(this.msgChannel);
    }

    /**
     * Tells the bot to leave, you do not need to manually destroy the VoiceController.
     */
    public void leave(){
        this.getTrackScheduler().getQueue().clear();
        this.getGuild().getAudioManager().closeAudioConnection();
        this.nowPlaying = null;
        player.destroy();
        PlayerStore.remove(this.getGuild());
    }

    public TrackScheduler getTrackScheduler() {
        return trackScheduler;
    }

    public Guild getGuild() {
        return jda.getGuildById(this.guildId);
    }

    public AudioPlayer getPlayer() {
        return player;
    }

    public AudioPlayerManager getPlayerManager() {
        return playerManager;
    }


}
