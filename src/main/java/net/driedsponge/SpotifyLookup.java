package net.driedsponge;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Playlist;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistRequest;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

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


    /**
     * Fetches tracks for the specified playlist from the Spotify API.
     * Each track will be added to the queue.
     * @param playListId The ID of the Spotify playlist.
     * @param event The {@link SlashCommandInteractionEvent} that is associated with the playlist.
     * @throws SpotifyWebApiException Spotify web API error
     */
    public static void loadPlayList(String playListId, SlashCommandInteractionEvent event, VoiceController vc) throws IOException, ParseException, SpotifyWebApiException {
        int songsPerPage = 50;
        clientCredentials_Sync();
        // Fancy code for Paginating spotify api results
        GetPlaylistRequest request = spotifyApi.getPlaylist(playListId).build();
        Playlist playlist = request.execute();
        Paging<PlaylistTrack> tracks = spotifyApi.getPlaylistsItems(playListId).limit(songsPerPage).build().execute();
        PlaylistTrack[] selectedTracks = tracks.getItems();
        List<PlaylistTrack> listTracks = new ArrayList<>(Arrays.asList(selectedTracks));
        // Prevent playlist over 500
        int totalTracks =  Math.min(tracks.getTotal(),TrackScheduler.QUEUE_LIMIT);
        if(totalTracks > 50){
            int pages = (totalTracks + songsPerPage - 1) / songsPerPage;
            for(int i = 1; i<pages; i++){
                PlaylistTrack[] addTracks = spotifyApi.getPlaylistsItems(playListId).limit(songsPerPage).offset(i*songsPerPage).build().execute().getItems();
                Collections.addAll(listTracks,addTracks);
            }
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Added " + totalTracks + " songs to the Queue from " + playlist.getName() + "!");
        embedBuilder.setColor(Main.PRIMARY_COLOR);
        embedBuilder.setFooter("Requested by " + event.getUser().getName(), event.getUser().getEffectiveAvatarUrl());
        embedBuilder.setThumbnail(playlist.getImages()[0].getUrl());
        event.getHook().sendMessageEmbeds(embedBuilder.build())
                .addActionRow(Button.link(event.getOptions().get(0).getAsString(), "Playlist"))
                .queue();
        for (PlaylistTrack spotifyTrack : listTracks) {
            String searchTerm = spotifyTrack.getTrack().getName();
            vc.getPlayerManager().loadItem("ytmsearch:"+searchTerm, new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack track) {
                    Song song = new Song(track,event);
                    PlayerStore.get(event.getGuild()).getTrackScheduler().queue(song,false);
                }
                @Override
                public void playlistLoaded(AudioPlaylist playlist) {
                    Song song = new Song(playlist.getTracks().get(0),event);
                    PlayerStore.get(event.getGuild()).getTrackScheduler().queue(song,false);
                }
                @Override
                public void noMatches() {}
                @Override
                public void loadFailed(FriendlyException exception) {}

            });
        }

    }
}
