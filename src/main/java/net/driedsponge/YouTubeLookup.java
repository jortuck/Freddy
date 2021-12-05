package net.driedsponge;


import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.gson.Gson;
import net.driedsponge.commands.VideoObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class YouTubeLookup {
    private static final String DEVELOPER_KEY = System.getenv("YOUTUBE_API_KEY");

    private static final String APPLICATION_NAME = "DriedSponge";
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    /**
     * Gets a url for a search query
     * @return The url for the video
     */
    public static String getUrl(String query) throws IOException, GeneralSecurityException, NoSuchFieldException {
        return "https://www.youtube.com/watch?v="+getId(query);
    }
    /**
     * Gets a video id for a search query
     * @return The id for the video
     */
    public static String getId(String query) throws IOException, GeneralSecurityException, NoSuchFieldException {
        System.out.println("Searching YouTube for "+query);

        YouTube yt = getService();
        // Define and execute the API request
        List<String> list = new ArrayList<>();
        YouTube.Search.List request = yt.search().list( list);

        SearchListResponse response = request.setKey(DEVELOPER_KEY)
                .setQ(query)
                .setType(List.of("video"))
                .setMaxResults(1L)
                .execute();

        Gson json = new Gson();
        VideoObject o = json.fromJson(response.getItems().get(0).toString(), VideoObject.class);
        return o.getId().videoId;
    }
    /**
     * Build and return an authorized API client service.
     *
     * @return an authorized API client service
     * @throws GeneralSecurityException, IOException
     */
    public static YouTube getService() throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        return new YouTube.Builder(httpTransport, JSON_FACTORY, null)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

}
