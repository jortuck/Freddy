package net.driedsponge;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.driedsponge.commands.Play;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Playlist;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetSeveralTracksRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetTrackRequest;

import java.awt.*;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;

public class SpotifyLookup {
    private static final Timestamp RESET_STAMP = new Timestamp(new Date().getTime());
    private static final String clientId = System.getenv("SPOTIFY_CLIENT_ID");
    private static final String clientSecret = System.getenv("SPOTIFY_CLIENT_SECRET");

    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setClientId(clientId)
            .setClientSecret(clientSecret)
            .build();
    private static final ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials()
            .build();

    public static void clientCredentials_Sync() {
        try {
            final ClientCredentials clientCredentials = clientCredentialsRequest.execute();

            // Set access token for further "spotifyApi" object usage
            spotifyApi.setAccessToken(clientCredentials.getAccessToken());

            System.out.println("Expires in: " + clientCredentials.getExpiresIn());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    public static void loadPlayList(String playListId, SlashCommandEvent event) throws IOException, ParseException, SpotifyWebApiException {
        clientCredentials_Sync();

        GetPlaylistRequest request = spotifyApi.getPlaylist(playListId).build();
        Playlist playlist = request.execute();
        VoiceController vc = Play.PLAYERS.get(event.getGuild());
        Paging<PlaylistTrack> tracks = playlist.getTracks();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Added " + tracks.getItems().length + " songs to the Queue from " + playlist.getName() + "!");
        embedBuilder.setColor(Color.CYAN);
        embedBuilder.setFooter("Requested by " + event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl());
        embedBuilder.setThumbnail(playlist.getImages()[0].getUrl());
        event.getHook().sendMessageEmbeds(embedBuilder.build())
                .addActionRow(Button.link(event.getOptions().get(0).getAsString(), "Playlist"))
                .queue();


        for (PlaylistTrack spotifyTrack : tracks.getItems()) {
            String searchTerm = spotifyTrack.getTrack().getName();
            vc.getPlayerManager().loadItem("ytmsearch:"+searchTerm, new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack track) {
                    Song song = new Song(track,event);
                    Play.PLAYERS.get(event.getGuild()).getTrackScheduler().queue(song,false);
                }
                @Override
                public void playlistLoaded(AudioPlaylist playlist) {
                    Song song = new Song(playlist.getTracks().get(0),event);
                    Play.PLAYERS.get(event.getGuild()).getTrackScheduler().queue(song,false);
                }
                @Override
                public void noMatches() {}
                @Override
                public void loadFailed(FriendlyException exception) {}

            });
        }

    }
}
