package net.driedsponge;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
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
import java.util.ArrayList;

public class SpotifyLookup {
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


    public static SpotifyPlaylist playList(String playListId, SlashCommandEvent event) throws IOException, ParseException, SpotifyWebApiException {
        GetPlaylistRequest request = spotifyApi.getPlaylist(playListId).build();
        Playlist playlist = request.execute();

        Paging<PlaylistTrack> tracks = playlist.getTracks();

        SpotifyPlaylist spotifyPlaylist = new SpotifyPlaylist(playlist.getName(),playlist.getUri());

        ArrayList<YouTubeSong> playlistTracks = new ArrayList<YouTubeSong>();

        for (PlaylistTrack track : tracks.getItems()) {

        }
        spotifyPlaylist.setSongs(playlistTracks);
        return spotifyPlaylist;
    }
}
