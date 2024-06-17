package net.driedsponge;

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

public final class SpotifyPlaylist {
    private static final Timestamp RESET_STAMP = new Timestamp(new Date().getTime());
    private static final String clientId = System.getenv("SPOTIFY_CLIENT_ID");
    private static final String clientSecret = System.getenv("SPOTIFY_CLIENT_SECRET");

    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setClientId(clientId)
            .setClientSecret(clientSecret)
            .build();
    private static final ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials()
            .build();

    private String name;
    private String image;
    private String author;
    private String url;
    private List<PlaylistTrack> songs;

    private SpotifyPlaylist(Playlist playlist, List<PlaylistTrack> songs){
        this.name = playlist.getName();
        this.image = playlist.getImages()[0].getUrl();
        this.url = playlist.getHref();
        this.author = playlist.getOwner().getDisplayName();
        this.songs = songs;
    }

    private static void clientCredentials_Sync() {
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
     *
     * @param playListId The ID of the Spotify playlist.
     * @throws SpotifyWebApiException Spotify web API error
     */
    public static SpotifyPlaylist fromId(String playListId) throws IOException, ParseException, SpotifyWebApiException {
        int songsPerPage = 50;
        clientCredentials_Sync();
        // Fancy code for Paginating spotify api results
        GetPlaylistRequest request = spotifyApi.getPlaylist(playListId).build();
        Playlist playlist = request.execute();
        Paging<PlaylistTrack> tracks = spotifyApi.getPlaylistsItems(playListId).limit(songsPerPage).build().execute();
        PlaylistTrack[] selectedTracks = tracks.getItems();
        List<PlaylistTrack> listTracks = new ArrayList<>(Arrays.asList(selectedTracks));
        // Prevent playlist over 500
        int totalTracks = Math.min(tracks.getTotal(), TrackScheduler.QUEUE_LIMIT);
        if (totalTracks > 50) {
            int pages = (totalTracks + songsPerPage - 1) / songsPerPage;
            for (int i = 1; i < pages; i++) {
                PlaylistTrack[] addTracks = spotifyApi.getPlaylistsItems(playListId).limit(songsPerPage).offset(i * songsPerPage).build().execute().getItems();
                Collections.addAll(listTracks, addTracks);
            }
        }
        return new SpotifyPlaylist(playlist,listTracks);
    }

    public List<PlaylistTrack> getSongs(){
        return Collections.unmodifiableList(this.songs);
    }

    public String getName(){
        return name;
    }

    public String getImage(){
        return this.image;
    }

    public String getUrl(){
        return this.url;
    }

}
