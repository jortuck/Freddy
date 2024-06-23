package net.driedsponge;

import org.apache.hc.core5.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static String clientId = System.getenv("SPOTIFY_CLIENT_ID");
    private static String clientSecret = System.getenv("SPOTIFY_CLIENT_SECRET");

    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setClientId(clientId)
            .setClientSecret(clientSecret)
            .build();
    private static final ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials()
            .build();
    private static final Logger logger = LoggerFactory.getLogger(SpotifyPlaylist.class);
    private String name;
    private String image;
    private String author;
    private String url;
    private List<PlaylistTrack> songs;

    private SpotifyPlaylist(Playlist playlist, List<PlaylistTrack> songs){
        this.name = playlist.getName();
        this.image = playlist.getImages()[0].getUrl();
        this.url = playlist.getExternalUrls().get("spotify");
        this.author = playlist.getOwner().getDisplayName();
        this.songs = songs;
    }

    private static void clientCredentials_Sync() {
        try {
            final ClientCredentials clientCredentials = clientCredentialsRequest.execute();

            // Set access token for further "spotifyApi" object usage
            spotifyApi.setAccessToken(clientCredentials.getAccessToken());

            logger.info("New Spotify Credentials, Expires In: {}", clientCredentials.getExpiresIn());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            logger.warn("Spotify Error: {}",  e.getMessage());
        }
    }

    /**
     * Fetches tracks for the specified playlist from the Spotify API.
     * Each track will be added to the queue.
     * TODO: Look into one loop for all of this. Something like space left and loop over that (split by
     * 50s then stop when limit is hit or playlist end, whichever comes first).
     * @param playListId The ID of the Spotify playlist.
     * @param space The space left in the queue.
     * @throws SpotifyWebApiException Spotify web API error
     */
    public static SpotifyPlaylist fromId(String playListId, int space) throws IOException, ParseException, SpotifyWebApiException {
        logger.info("Fetching Spotify Playlist ID {}",playListId);
        int songsPerPage = 50;
        clientCredentials_Sync();
        // Fancy code for Paginating spotify api results
        GetPlaylistRequest request = spotifyApi.getPlaylist(playListId).build();
        Playlist playlist = request.execute();
        Paging<PlaylistTrack> tracks = spotifyApi.getPlaylistsItems(playListId).limit(songsPerPage).build().execute();
        List<PlaylistTrack> listTracks = new ArrayList<>();
        for (PlaylistTrack track : tracks.getItems()){
            listTracks.add(track);
            space--;
            if(space == 0){
                break;
            }
        }
        // Prevent playlist over 500
        int pages = (tracks.getTotal() + songsPerPage - 1) / songsPerPage;
        for (int i = 1; i < pages && space!=0; i++) {
            PlaylistTrack[] addTracks = spotifyApi.getPlaylistsItems(playListId).limit(songsPerPage).offset(i * songsPerPage).build().execute().getItems();
            for (PlaylistTrack track : addTracks){
                listTracks.add(track);
                space--;
                if(space == 0){
                    break;
                }
            }
        }
        return new SpotifyPlaylist(playlist,listTracks);
    }

    public List<PlaylistTrack> getSongs(){
        return (this.songs);
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

    public static void updateCredentials(String spotifyClientID, String spotifyClientSecret){
        clientId = spotifyClientID;
        clientSecret = spotifyClientSecret;
    }

}
